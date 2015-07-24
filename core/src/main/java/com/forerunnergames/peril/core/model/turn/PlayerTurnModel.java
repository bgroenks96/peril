package com.forerunnergames.peril.core.model.turn;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

/**
 * Model class for tracking player turn.
 */
public interface PlayerTurnModel
{
  void advance ();

  int getTurn ();

  PlayerTurnOrder getTurnOrder ();

  boolean isFirstTurn ();

  boolean isLastTurn ();

  void reset ();

  int getTurnCount ();

  void setTurnCount (final int turnCount);

  @Override
  String toString ();
}
