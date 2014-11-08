package com.forerunnergames.peril.core.model.player;

import com.forerunnergames.peril.core.model.person.Person;

public interface Player extends Person
{
  public PlayerColor getColor();
  public PlayerTurnOrder getTurnOrder();
  public boolean has (final PlayerColor color);
  public boolean has (final PlayerTurnOrder turnOrder);
  public void setColor (final PlayerColor color);
  public void setTurnOrder (final PlayerTurnOrder turnOrder);
}
