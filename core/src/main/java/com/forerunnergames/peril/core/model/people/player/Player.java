package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.Person;

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

  boolean has (final PlayerColor color);

  boolean has (final PlayerTurnOrder turnOrder);

  boolean hasArmiesInHand (final int armies);

  void removeArmiesFromHand (final int armies);
}
