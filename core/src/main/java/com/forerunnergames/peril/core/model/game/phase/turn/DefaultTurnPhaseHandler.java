package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerEndTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultPlayerCardsChangedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndTurnSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.game.CacheKey;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.GamePhaseEventFactory;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandler;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public final class DefaultTurnPhaseHandler extends AbstractGamePhaseHandler implements TurnPhaseHandler
{
  protected final GamePhaseEventFactory gamePhaseEventFactory;

  public DefaultTurnPhaseHandler (final GameModelConfiguration gameModelConfig,
                                  final GamePhaseEventFactory gamePhaseEventFactory)
  {
    super (gameModelConfig);

    this.gamePhaseEventFactory = gamePhaseEventFactory;
  }

  /* (non-Javadoc)
   * @see com.forerunnergames.peril.core.model.game.phase.turn.TurnPhaseHandler#isFirstTurn()
   */
  @Override
  public boolean isFirstTurn ()
  {
    return playerTurnModel.isFirstTurn ();
  }

  /* (non-Javadoc)
   * @see com.forerunnergames.peril.core.model.game.phase.turn.TurnPhaseHandler#isLastTurn()
   */
  @Override
  public boolean isLastTurn ()
  {
    return playerTurnModel.isLastTurn ();
  }

  @Override
  public void advancePlayerTurn ()
  {
    playerTurnModel.advance ();
  }

  /* (non-Javadoc)
   * @see com.forerunnergames.peril.core.model.game.phase.turn.TurnPhaseHandler#verifyPlayerCardTradeIn(com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent)
   */
  @Override
  @StateTransitionCondition
  public boolean verifyPlayerCardTradeIn (final PlayerTradeInCardsRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id playerId = playerModel.idOf (getCurrentPlayerName ());

    Result <PlayerTradeInCardsDeniedEvent.Reason> result = Result.success ();

    final CardSetPacket tradeIn = event.getTradeIn ();

    final ImmutableSet <Card> cards = CardPackets.toCardSet (tradeIn.getCards (), cardModel);
    final CardSet cardSet = new CardSet (rules, cards);
    if (!cardSet.isEmpty () && !cardSet.isMatch ())
    {
      result = Result.failure (PlayerTradeInCardsDeniedEvent.Reason.INVALID_CARD_SET);
    }

    final int cardTradeInBonus = cardModel.getNextTradeInBonus ();

    if (!cardSet.isEmpty () && result.succeeded ())
    {
      result = cardModel.requestTradeInCards (playerId, cardSet.match (), TurnPhase.REINFORCE);
    }

    if (!cardSet.isEmpty () && result.succeeded ())
    {
      playerModel.addArmiesToHandOf (playerId, cardTradeInBonus);
    }
    else if (result.failed ())
    {
      publish (new PlayerTradeInCardsDeniedEvent (getCurrentPlayerPacket (), event, result.getFailureReason ()));
      return false;
    }

    publish (new PlayerTradeInCardsResponseSuccessEvent (getCurrentPlayerPacket (), event.getTradeIn (),
            cardTradeInBonus, cardModel.getNextTradeInBonus ()));
    publish (new DefaultPlayerCardsChangedEvent (getCurrentPlayerPacket (), -event.getTradeInCardCount ()));

    final boolean shouldWaitForNextTradeIn = publishTradeInEventIfNecessary ();
    return !shouldWaitForNextTradeIn;
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerEndTurnRequest (final PlayerEndTurnRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket player = getCurrentPlayerPacket ();
    final Optional <PlayerPacket> sender = eventRegistry.senderOf (event);
    if (!sender.isPresent () || player.isNot (sender.get ()))
    {
      // defer to GameModel::onEvent(PlayerEndTurnRequestEvent) for publishing denial
      return false;
    }

    publish (new PlayerEndTurnSuccessEvent (player));

    turnDataCache.put (CacheKey.END_PLAYER_TURN_VERIFIED, true);

    return true;
  }

  @Override
  public boolean publishTradeInEventIfNecessary ()
  {
    final Id playerId = getCurrentPlayerId ();
    final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (playerId);
    final boolean shouldPublish = !matches.isEmpty ();
    if (shouldPublish)
    {
      publish (gamePhaseEventFactory.createCardTradeInEventFor (playerId, matches, TurnPhase.REINFORCE));
    }

    return shouldPublish;
  }

  @Override
  protected void onBegin ()
  {
    log.trace ("Begin DefaultTurnPhaseHandler");

    playerTurnModel.setRoundIncreasing (true);

    changeGamePhaseTo (GamePhase.TURN);
  }

  @Override
  protected void onEnd ()
  {
    log.trace ("End DefaultTurnPhaseHandler");

    changeGamePhaseTo (GamePhase.TURN);
  }
}
