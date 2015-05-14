package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayerPacket implements PlayerPacket
{
  private final String name;
  private final String color;
  private final int turnOrder;
  private final int armiesInHand;

  public DefaultPlayerPacket (final String name, final String color, final int turnOrder, final int armiesInHand)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");
    Arguments.checkIsNotNegative (armiesInHand, "armiesInHand");

    this.name = name;
    this.color = color;
    this.turnOrder = turnOrder;
    this.armiesInHand = armiesInHand;
  }

  @Override
  public String getName ()
  {
    return name;
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
  public boolean hasArmiesInHand (final int armiesInHand)
  {
    Arguments.checkIsNotNegative (armiesInHand, "armiesInHand");

    return this.armiesInHand == armiesInHand;
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

  @RequiredForNetworkSerialization
  private DefaultPlayerPacket ()
  {
    this.name = null;
    this.color = null;
    this.turnOrder = 0;
    this.armiesInHand = 0;
  }
}
