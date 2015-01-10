package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.armies.Army;
import com.forerunnergames.peril.core.model.people.person.Person;

import com.google.common.collect.ImmutableSet;

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

  public void addArmyToHand (final Army army);

  public void addArmiesToHand (final ImmutableSet <Army> armies);

  public void removeArmyFromHand (final Army army);

  public void removeArmiesFromHand (final ImmutableSet <Army> armies);

  public int getArmiesInHandCount ();

  public boolean hasArmiesInHandCount (final int count);

  public ImmutableSet <Army> getArmiesInHand ();
}
