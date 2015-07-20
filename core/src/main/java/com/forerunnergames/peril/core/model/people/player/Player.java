package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.Person;

import java.util.Comparator;

public interface Player extends Person
{
  void addArmiesToHand (final int armies);

  boolean doesNotHave (final PlayerColor color);

  boolean doesNotHave (final PlayerTurnOrder turnOrder);

  int getArmiesInHand ();

  PlayerColor getColor ();

  void setColor (final PlayerColor color);

  PlayerTurnOrder getTurnOrder ();

  void setTurnOrder (final PlayerTurnOrder turnOrder);

  /**
   * Position 1 is PlayerTurnOrder.FIRST, position 2 is PlayerTurnOrder.SECOND, etc.
   * Position 0 is invalid & cannot be set.
   */
  int getTurnOrderPosition ();

  /**
   * Position 1 is PlayerTurnOrder.FIRST, position 2 is PlayerTurnOrder.SECOND, etc.
   * Position 0 is invalid & cannot be set.
   */
  void setTurnOrderByPosition (final int position);

  boolean has (final PlayerColor color);

  boolean has (final PlayerTurnOrder turnOrder);

  boolean hasArmiesInHand (final int armies);

  void removeArmiesFromHand (final int armies);

  void removeAllArmiesFromHand ();

  Comparator <Player> TURN_ORDER_COMPARATOR = new Comparator <Player> ()
  {
    @Override
    public int compare (final Player o1, final Player o2)
    {
      if (o1.getTurnOrderPosition () < o2.getTurnOrderPosition ())
      {
        return -1;
      }
      else if (o1.getTurnOrderPosition () > o2.getTurnOrderPosition ())
      {
        return 1;
      }
      else
      {
        return 0;
      }
    }
  };
}
