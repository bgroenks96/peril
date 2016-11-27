package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginReinforcementWaitEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.GamePhaseEventFactory;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandler;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultReinforcementPhaseHandler extends AbstractGamePhaseHandler
        implements ReinforcementPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (DefaultReinforcementPhaseHandler.class);

  private final TurnPhaseHandler turnPhaseHandler;
  private final GamePhaseEventFactory gamePhaseEventFactory;

  public DefaultReinforcementPhaseHandler (final GameModelConfiguration gameModelConfig,
                                           final TurnPhaseHandler turnPhaseHandler,
                                           final GamePhaseEventFactory gamePhaseEventFactory)
  {
    super (gameModelConfig);

    this.turnPhaseHandler = turnPhaseHandler;
    this.gamePhaseEventFactory = gamePhaseEventFactory;
  }

  @Override
  @StateEntryAction
  protected void onBegin ()
  {
    final Id playerId = getCurrentPlayerId ();

    log.info ("Begin reinforcement phase for player [{}].", getCurrentPlayerPacket ());

    // add country reinforcements and publish event
    final int countryReinforcementBonus = rules
            .calculateCountryReinforcements (countryOwnerModel.countCountriesOwnedBy (playerId));
    int continentReinforcementBonus = 0;
    final ImmutableSet <ContinentPacket> playerOwnedContinents = continentOwnerModel.getContinentsOwnedBy (playerId);
    for (final ContinentPacket cont : playerOwnedContinents)
    {
      continentReinforcementBonus += cont.getReinforcementBonus ();
    }
    final int totalReinforcementBonus = countryReinforcementBonus + continentReinforcementBonus;
    playerModel.addArmiesToHandOf (playerId, totalReinforcementBonus);

    final PlayerPacket playerPacket = getCurrentPlayerPacket ();

    // publish phase begin event and trade in request
    publish (new BeginReinforcementPhaseEvent (playerPacket, countryReinforcementBonus, continentReinforcementBonus));
    publish (gamePhaseEventFactory.createReinforcementEventFor (playerId));
    turnPhaseHandler.publishTradeInEventIfNecessary ();
    publish (new PlayerBeginReinforcementWaitEvent (playerPacket));
  }

  @Override
  @StateExitAction
  protected void onEnd ()
  {
    final PlayerPacket player = getCurrentPlayerPacket ();

    log.info ("End reinforcement phase for player [{}].", player);
  }

  /* (non-Javadoc)
   * @see com.forerunnergames.peril.core.model.game.phase.turn.ReinforcementPhaseHandler#waitForPlayerToPlaceReinforcements()
   */
  @Override
  @StateEntryAction
  public void waitForPlayerToPlaceReinforcements ()
  {
    final Id playerId = getCurrentPlayerId ();
    final PlayerPacket playerPacket = getCurrentPlayerPacket ();

    if (playerModel.getArmiesInHand (playerId) > 0)
    {
      publish (gamePhaseEventFactory.createReinforcementEventFor (playerId));
      publish (new PlayerBeginReinforcementWaitEvent (playerPacket));
      log.info ("Waiting for player [{}] to place reinforcements...", playerPacket);
    }
    else
    {
      publish (new EndReinforcementPhaseEvent (playerPacket, countryOwnerModel.getCountryPacketsOwnedBy (playerId)));
      log.info ("Player [{}] has no more armies in hand. Moving to next phase...", playerPacket);
    }
  }

  /* (non-Javadoc)
   * @see com.forerunnergames.peril.core.model.game.phase.turn.ReinforcementPhaseHandler#verifyPlayerReinforceCountry(com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent)
   */
  @Override
  @StateTransitionCondition
  public boolean verifyPlayerReinforceCountry (final PlayerReinforceCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id playerId = getCurrentPlayerId ();

    if (cardModel.countCardsInHand (playerId) >= rules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE))
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.TRADE_IN_REQUIRED, event));
      return false;
    }

    // --- process country reinforcements --- //

    final String countryName = event.getCountryName ();
    final int reinforcementCount = event.getReinforcementCount ();

    MutatorResult <PlayerReinforceCountryDeniedEvent.Reason> result;
    final ImmutableSet.Builder <MutatorResult <PlayerReinforceCountryDeniedEvent.Reason>> resultBuilder;
    resultBuilder = ImmutableSet.builder ();

    if (reinforcementCount > playerModel.getArmiesInHand (playerId))
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND);
      resultBuilder.add (result);
    }

    if (reinforcementCount < rules.getMinReinforcementsPlacedPerCountry ())
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_REINFORCEMENTS_PLACED);
      resultBuilder.add (result);
    }

    if (!countryGraphModel.existsCountryWith (countryName))
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST);
      resultBuilder.add (result);
    }

    final Id countryId = countryGraphModel.idOf (countryName);
    if (!countryOwnerModel.isCountryOwnedBy (countryId, playerId))
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY);
      resultBuilder.add (result);
    }

    result = countryArmyModel.requestToAddArmiesToCountry (countryId, reinforcementCount);
    resultBuilder.add (result);

    final ImmutableSet <MutatorResult <PlayerReinforceCountryDeniedEvent.Reason>> results = resultBuilder.build ();
    final Optional <MutatorResult <PlayerReinforceCountryDeniedEvent.Reason>> firstFailure;
    firstFailure = Result.firstFailedFrom (results);

    if (firstFailure.isPresent ())
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              firstFailure.get ().getFailureReason (), event));
      return false;
    }

    // commit results
    playerModel.removeArmiesFromHandOf (playerId, reinforcementCount);
    MutatorResult.commitAllSuccessful (results.toArray (new MutatorResult <?> [results.size ()]));

    final CountryPacket countryPacket = countryGraphModel.countryPacketWith (countryId);
    publish (new PlayerReinforceCountrySuccessEvent (getCurrentPlayerPacket (), countryPacket, reinforcementCount));

    return true;
  }
}
