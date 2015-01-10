package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.Person;

public interface Player extends Person
{
  public PlayerColor getColor ();

  public PlayerTurnOrder getTurnOrder ();

  public boolean has (final PlayerColor color);

  public boolean doesNotHave (final PlayerColor color);

  public boolean has (final PlayerTurnOrder turnOrder);

  public boolean doesNotHave (final PlayerTurnOrder turnOrder);

  public void setColor (final PlayerColor color);

  public void setTurnOrder (final PlayerTurnOrder turnOrder);

  public void addArmiesToHand (final int armies);

  public boolean canAddArmiesToHand (final int armies);

  public void removeArmiesFromHand (final int armies);

  public boolean canRemoveArmiesFromHand (final int armies);

  public int getArmiesInHand ();

  public boolean hasArmiesInHand (final int armies);
}
