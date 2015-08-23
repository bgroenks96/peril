package com.forerunnergames.peril.core.shared.net.events.server.request;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class PlayerSelectCountryRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket player;

  public PlayerSelectCountryRequestEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Player: %2$s", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryRequestEvent ()
  {
    player = null;
  }
}
