package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.person.AbstractPersonPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public final class DefaultPlayerPacket extends AbstractPersonPacket implements PlayerPacket
{
  private final String color;
  private final int turnOrder;
  private final int armiesInHand;

  public DefaultPlayerPacket (final UUID playerId,
                              final String name,
                              final String color,
                              final int turnOrder,
                              final int armiesInHand)
  {
    super (name, playerId);

    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");
    Arguments.checkIsNotNegative (armiesInHand, "armiesInHand");

    this.color = color;
    this.turnOrder = turnOrder;
    this.armiesInHand = armiesInHand;
  }

  @Override
  public String getColor ()
  {
    return color;
  }

  @Override
  public int getTurnOrder ()
  {
    return turnOrder;
  }

  @Override
  public int getArmiesInHand ()
  {
    return armiesInHand;
  }

  @Override
  public boolean has (final String color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color.equals (color);
  }

  @Override
  public boolean has (final int turnOrder)
  {
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");

    return this.turnOrder == turnOrder;
  }

  @Override
  public boolean hasArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armiesInHand");

    return armiesInHand == armies;
  }

  @Override
  public boolean doesNotHave (final String color)
  {
    Arguments.checkIsNotNull (color, "color");

    return !this.color.equals (color);
  }

  @Override
  public boolean doesNotHave (final int turnOrder)
  {
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");

    return this.turnOrder != turnOrder;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Color: {} | Turn Order: {} | Armies in Hand: {}", super.toString (), color, turnOrder,
                           armiesInHand);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerPacket ()
  {
    super (null, null);

    color = null;
    turnOrder = 0;
    armiesInHand = 0;
  }
}
