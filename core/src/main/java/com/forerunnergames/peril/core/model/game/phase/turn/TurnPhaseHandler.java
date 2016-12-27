package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.core.model.game.phase.GamePhaseHandler;

public interface TurnPhaseHandler extends GamePhaseHandler
{
  boolean isFirstTurn ();

  boolean isLastTurn ();

  void advancePlayerTurn ();

  /**
   * @return true if trade-ins are complete and state machine should advance to normal reinforcement state, false if
   *         additional trade-ins are available
   */
  boolean verifyPlayerCardTradeIn (PlayerTradeInCardsRequestEvent event);

  boolean verifyPlayerEndTurnRequest (EndPlayerTurnRequestEvent event);

  boolean publishTradeInEventIfNecessary ();

  void beginPlayerTurn ();

  void endPlayerTurn ();
}
