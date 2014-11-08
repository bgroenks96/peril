package com.forerunnergames.peril.core.model.player;

import com.forerunnergames.peril.core.model.person.AbstractPerson;
import com.forerunnergames.peril.core.model.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Id;

public final class DefaultPlayer extends AbstractPerson implements Player
{
  private PlayerColor color;
  private PlayerTurnOrder turnOrder;

  public DefaultPlayer (final String name,
                        final Id id,
                        final PersonIdentity identity,
                        final PlayerColor color,
                        final PlayerTurnOrder turnOrder)
  {
    super (name, id, identity);

    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.color = color;
    this.turnOrder = turnOrder;
  }

  @Override
  public PlayerColor getColor()
  {
    return color;
  }

  @Override
  public PlayerTurnOrder getTurnOrder()
  {
    return turnOrder;
  }

  @Override
  public boolean has (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color.equals (color);
  }

  @Override
  public boolean has (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return this.turnOrder.equals (turnOrder);
  }

  @Override
  public void setColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    this.color = color;
  }

  @Override
  public void setTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.turnOrder = turnOrder;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s | Color: %2$s | Turn order: %3$s", super.toString(), color, turnOrder);
  }

  // Required for network serialization
  protected DefaultPlayer()
  {
  }
}
