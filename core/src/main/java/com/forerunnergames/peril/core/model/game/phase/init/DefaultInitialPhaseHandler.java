package com.forerunnergames.peril.core.model.game.phase.init;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultPlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginReinforcementWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerClaimCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.GamePhaseEventFactory;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandler;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.peril.core.model.state.events.BeginManualCountryAssignmentEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultInitialPhaseHandler extends AbstractGamePhaseHandler implements InitialPhaseHandler
{
  private static final Logger log = LoggerFactory.getLogger (DefaultInitialPhaseHandler.class);

  private final GamePhaseEventFactory sharedEventFactory;

  public DefaultInitialPhaseHandler (final GameModelConfiguration gameModelConfig,
                                     final GamePhaseEventFactory sharedEventFactory)
  {
    super (gameModelConfig);

    this.sharedEventFactory = sharedEventFactory;
  }

  @Override
  @StateEntryAction
  public void determinePlayerTurnOrder ()
  {
    log.info ("Determining player turn order randomly...");

    final ImmutableSet <PlayerPacket> players = playerModel.getPlayerPackets ();
    final List <PlayerPacket> shuffledPlayers = Randomness.shuffle (players);
    final Iterator <PlayerPacket> randomPlayerItr = shuffledPlayers.iterator ();

    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!randomPlayerItr.hasNext ()) break;

      final PlayerPacket player = randomPlayerItr.next ();
      final Id playerId = playerModel.idOf (player.getName ());

      // Don't publish DefaultPlayerTurnOrderChangedEvent's for turn order mutation results because
      // the changes are temporary placeholders, and the final changes are published together in
      // DeterminePlayerTurnOrderCompleteEvent.
      playerModel.changeTurnOrderOfPlayer (playerId, turnOrder);

      log.info ("Set turn order of player [{}] to [{}].", player.getName (), turnOrder);
    }

    final ImmutableSortedSet.Builder <PlayerPacket> ordered = ImmutableSortedSet
            .orderedBy (PlayerPacket.TURN_ORDER_COMPARATOR);
    ordered.addAll (playerModel.getPlayerPackets ());
    publish (new DeterminePlayerTurnOrderCompleteEvent (ordered.build ()));
  }

  @Override
  @StateEntryAction
  public void distributeInitialArmies ()
  {
    final int armies = rules.getInitialArmies ();

    log.info ("Distributing {} armies each to {} players...", armies, playerModel.getPlayerCount ());

    for (final PlayerPacket player : playerModel.getTurnOrderedPlayers ())
    {
      final Id playerId = playerModel.idOf (player.getName ());
      playerModel.addArmiesToHandOf (playerId, armies);

      publish (new DefaultPlayerArmiesChangedEvent (playerModel.playerPacketWith (playerId), armies));
    }

    publish (new DistributeInitialArmiesCompleteEvent (playerModel.getPlayerPackets ()));
  }

  @Override
  @StateEntryAction
  public void waitForCountryAssignmentToBegin ()
  {
    final InitialCountryAssignment assignmentMode = rules.getInitialCountryAssignment ();
    publish (new BeginPlayerCountryAssignmentEvent (assignmentMode));
    switch (assignmentMode)
    {
      case RANDOM:
      {
        log.info ("Initial country assignment = RANDOM");
        publish (new RandomlyAssignPlayerCountriesEvent ());
        break;
      }
      case MANUAL:
      {
        log.info ("Initial country assignment = MANUAL");
        publish (new BeginManualCountryAssignmentEvent ());
        break;
      }
      default:
      {
        Exceptions.throwRuntime ("Unrecognized value for initial country assignment: {}", assignmentMode);
        break;
      }
    }
  }

  @Override
  @StateEntryAction
  public void randomlyAssignPlayerCountries ()
  {
    // if there are no players, just give up now!
    if (playerModel.isEmpty ())
    {
      log.info ("Skipping random country assignment... no players!");
      return;
    }

    final List <Id> countries = Randomness.shuffle (new HashSet <> (countryGraphModel.getCountryIds ()));
    final List <PlayerPacket> players = Randomness.shuffle (playerModel.getPlayerPackets ());
    final ImmutableList <Integer> playerCountryDistribution = rules
            .getInitialPlayerCountryDistribution (players.size ());

    log.info ("Randomly assigning {} countries to {} players...", countries.size (), players.size ());

    final Iterator <Id> countryItr = countries.iterator ();
    for (int i = 0; i < players.size (); ++i)
    {
      final PlayerPacket nextPlayer = players.get (i);
      final Id nextPlayerId = playerModel.idOf (nextPlayer.getName ());
      final int playerCountryCount = playerCountryDistribution.get (i);

      int assignSuccessCount = 0; // for logging purposes
      for (int count = 0; count < playerCountryCount && countryItr.hasNext (); count++)
      {
        final Id toAssign = countryItr.next ();
        MutatorResult <?> result = countryOwnerModel.requestToAssignCountryOwner (toAssign, nextPlayerId);
        if (result.failed ())
        {
          log.warn ("Failed to assign country [{}] to [{}] | Reason: {}", countryGraphModel.nameOf (toAssign),
                    nextPlayer, result.getFailureReason ());
          continue;
        }

        result.commitIfSuccessful ();

        result = countryArmyModel.requestToAddArmiesToCountry (toAssign, 1);
        if (result.failed ())
        {
          log.warn ("Failed to assign country [{}] to [{}] | Reason: {}", countryGraphModel.nameOf (toAssign),
                    nextPlayer, result.getFailureReason ());
          continue;
        }

        result.commitIfSuccessful ();

        playerModel.removeArmyFromHandOf (nextPlayerId);
        assignSuccessCount++;

        publish (new DefaultCountryArmiesChangedEvent (countryGraphModel.countryPacketWith (toAssign), 1));
        publish (new DefaultCountryOwnerChangedEvent (countryGraphModel.countryPacketWith (toAssign), nextPlayer));

        countryItr.remove ();
      }

      log.info ("Assigned {} countries to [{}].", assignSuccessCount, nextPlayer.getName ());
      final PlayerPacket updatedPlayerPacket = playerModel.playerPacketWith (nextPlayerId);
      publish (new DefaultPlayerArmiesChangedEvent (updatedPlayerPacket, -1 * assignSuccessCount));
    }

    // create map of country -> player packets for
    // PlayerCountryAssignmentCompleteEvent
    final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
    playMapViewPackets = buildPlayMapViewFrom (playerModel, playMapModel);

    publish (new PlayerCountryAssignmentCompleteEvent (rules.getInitialCountryAssignment (), playMapViewPackets));
  }

  @Override
  @StateEntryAction
  public void beginInitialReinforcementPhase ()
  {
    log.info ("Begin initial reinforcement phase...");

    playerTurnModel.resetCurrentTurn ();

    publish (new BeginInitialReinforcementPhaseEvent (getCurrentPlayerPacket ()));
  }

  @Override
  @StateEntryAction
  public void waitForPlayersToClaimInitialCountries ()
  {
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (countryOwnerModel.allCountriesAreOwned ())
    {
      // create map of country -> player packets for
      // PlayerCountryAssignmentCompleteEvent
      final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
      playMapViewPackets = buildPlayMapViewFrom (playerModel, playMapModel);
      publish (new PlayerCountryAssignmentCompleteEvent (rules.getInitialCountryAssignment (), playMapViewPackets));
      return;
    }

    if (currentPlayer.getArmiesInHand () == 0)
    {
      log.info ("Player [{}] has no armies. Skipping...", currentPlayer);
      publish (new SkipPlayerTurnEvent (currentPlayer));
      return;
    }

    log.info ("Waiting for player [{}] to claim a country...", currentPlayer.getName ());
    publish (new PlayerClaimCountryRequestEvent (currentPlayer, countryOwnerModel.getUnownedCountries ()));
    publish (new PlayerClaimCountryWaitEvent (currentPlayer));
    publish (new ActivePlayerChangedEvent (currentPlayer));
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerClaimCountryResponseRequest (final PlayerClaimCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();
    final Id currentPlayerId = playerModel.idOf (currentPlayer.getName ());

    final String claimedCountryName = event.getClaimedCountryName ();

    if (!playerModel.canRemoveArmyFromHandOf (currentPlayerId))
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName,
              PlayerClaimCountryResponseDeniedEvent.Reason.DELTA_ARMY_COUNT_OVERFLOW));
      republishRequestFor (event);
      return false;
    }

    if (!countryGraphModel.existsCountryWith (claimedCountryName))
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName,
              PlayerClaimCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
      republishRequestFor (event);
      return false;
    }

    final Id countryId = countryGraphModel.idOf (claimedCountryName);

    final MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> res1;
    res1 = countryOwnerModel.requestToAssignCountryOwner (countryId, currentPlayerId);
    if (res1.failed ())
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName, res1.getFailureReason ()));
      republishRequestFor (event);
      return false;
    }

    final MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> res2;
    res2 = countryArmyModel.requestToAddArmiesToCountry (countryId, 1);
    if (res2.failed ())
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName, res2.getFailureReason ()));
      republishRequestFor (event);
      return false;
    }

    MutatorResult.commitAllSuccessful (res1, res2);
    playerModel.removeArmyFromHandOf (currentPlayerId);

    final PlayerPacket updatedPlayer = playerModel.playerPacketWith (currentPlayerId);
    publish (new PlayerClaimCountryResponseSuccessEvent (updatedPlayer, countryGraphModel.countryPacketWith (countryId),
            1));

    return true;
  }

  @Override
  @StateEntryAction
  public void waitForPlayersToReinforceInitialCountries ()
  {
    int totalArmySum = 0;
    for (final Id playerId : playerModel.getPlayerIds ())
    {
      totalArmySum += playerModel.getArmiesInHand (playerId);
    }

    if (totalArmySum == 0)
    {
      publish (new EndInitialReinforcementPhaseEvent (buildPlayMapViewFrom (playerModel, playMapModel)));
      return;
    }

    final PlayerPacket playerPacket = getCurrentPlayerPacket ();
    final Id playerId = getCurrentPlayerId ();

    if (playerModel.getArmiesInHand (playerId) == 0)
    {
      log.trace ("Player [{}] has no armies remaining in hand. Skipping...", playerPacket);
      publish (new SkipPlayerTurnEvent (playerPacket));
      return;
    }

    log.trace ("Waiting for [{}] to place initial reinforcements...", playerPacket);

    publish (sharedEventFactory.createReinforcementEventFor (playerId));
    publish (new PlayerBeginReinforcementWaitEvent (playerPacket));
    publish (new ActivePlayerChangedEvent (playerPacket));
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerInitialCountryReinforcements (final PlayerReinforceCountryRequestEvent event)
  {
    log.info ("Event received [{}]", event);

    final Id playerId = getCurrentPlayerId ();
    final int requestedReinforcements = event.getReinforcementCount ();

    if (requestedReinforcements > playerModel.getArmiesInHand (playerId))
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND, event));
      return false;
    }

    if (requestedReinforcements < rules.getMinReinforcementsPlacedPerCountry ())
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_REINFORCEMENTS_PLACED, event));
      return false;
    }

    final String countryName = event.getCountryName ();
    if (!countryGraphModel.existsCountryWith (countryName))
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST, event));
      return false;
    }

    final Id countryId = countryGraphModel.countryWith (countryName);
    if (!countryOwnerModel.isCountryOwnedBy (countryId, playerId))
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY, event));
      return false;
    }

    final MutatorResult <PlayerReinforceCountryDeniedEvent.Reason> result;
    result = countryArmyModel.requestToAddArmiesToCountry (countryId, requestedReinforcements);

    if (result.failed ())
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (), result.getFailureReason (), event));
      return false;
    }

    result.commitIfSuccessful ();
    playerModel.removeArmiesFromHandOf (playerId, requestedReinforcements);

    final CountryPacket countryPacket = countryGraphModel.countryPacketWith (countryId);
    publish (new PlayerReinforceCountrySuccessEvent (getCurrentPlayerPacket (), countryPacket,
            requestedReinforcements));

    return true;
  }

  @Override
  protected void onBegin ()
  {
    log.trace ("Enter InitialPhaseHandler");
  }

  @Override
  protected void onEnd ()
  {
    log.trace ("Exit InitialPhaseHandler");
  }
}
