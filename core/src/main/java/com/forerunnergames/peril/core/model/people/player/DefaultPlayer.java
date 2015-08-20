package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.AbstractPerson;
import com.forerunnergames.peril.core.shared.net.packets.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.math.IntMath;

public final class DefaultPlayer extends AbstractPerson implements Player
{
  private PlayerColor color;
  private PlayerTurnOrder turnOrder;
  private int armiesInHand;

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
  public void addArmiesToHand (final int armies)
  {
    Arguments.checkIsNotNull (armies, "armies");
    Arguments.checkIsNotNegative (armies, "armies");

    armiesInHand = IntMath.checkedAdd (armiesInHand, armies);
  }

  @Override
  public boolean doesNotHave (final PlayerColor color)
  {
    return !has (color);
  }

  @Override
  public boolean doesNotHave (final PlayerTurnOrder turnOrder)
  {
    return !has (turnOrder);
  }

  @Override
  public int getArmiesInHand ()
  {
    return armiesInHand;
  }

  @Override
  public PlayerColor getColor ()
  {
    return color;
  }

  @Override
  public void setColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    this.color = color;
  }

  @Override
  public PlayerTurnOrder getTurnOrder ()
  {
    return turnOrder;
  }

  @Override
  public int getTurnOrderPosition ()
  {
    return turnOrder.getPosition ();
  }

  @Override
  public void setTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.turnOrder = turnOrder;
  }

  @Override
  public void setTurnOrderByPosition (final int position)
  {
    Arguments.checkIsNotNegative (position, "position");

    turnOrder = PlayerTurnOrder.getNthValidTurnOrder (position);
  }

  @Override
  public boolean has (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color == color;
  }

  @Override
  public boolean has (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return this.turnOrder == turnOrder;
  }

  @Override
  public boolean hasArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return armiesInHand >= armies;
  }

  @Override
  public void removeArmiesFromHand (final int armies)
  {
    Arguments.checkIsNotNull (armies, "armies");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (armies <= armiesInHand, "Cannot remove more armies [" + armies
            + "] than are currently in hand [" + armiesInHand + "].");

    armiesInHand -= armies;
  }

  @Override
  public void removeAllArmiesFromHand ()
  {
    armiesInHand = 0;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | Color: %2$s | Turn order: %3$s | Armies in hand: %4$s", super.toString (), color,
                          turnOrder, armiesInHand);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayer ()
  {
  }
}
