package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;

public final class DefaultPlayerPacket implements PlayerPacket
{
  private final String name;
  private final PlayerColor color;
  private final PlayerTurnOrder turnOrder;
  private final int armiesInHand;

  public DefaultPlayerPacket (final Player player)
  {
    name = player.getName ();
    color = player.getColor ();
    turnOrder = player.getTurnOrder ();
    armiesInHand = player.getArmiesInHand ();
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public PlayerColor getColor ()
  {
    return color;
  }

  @Override
  public PlayerTurnOrder getTurnOrder ()
  {
    return turnOrder;
  }

  @Override
  public int getArmiesInHand ()
  {
    return armiesInHand;
  }

  @Override
  public boolean has (final PlayerColor color)
  {
    return this.color.is (color);
  }

  @Override
  public boolean has (final PlayerTurnOrder turnOrder)
  {
    return this.turnOrder.is (turnOrder);
  }

  @Override
  public boolean hasArmiesInHand (final int armies)
  {
    return armiesInHand >= armies;
  }

  @Override
  public boolean doesNotHave (final PlayerColor color)
  {
    return this.color.isNot (color);
  }

  @Override
  public boolean doesNotHave (final PlayerTurnOrder turnOrder)
  {
    return this.turnOrder.isNot (turnOrder);
  }
}
