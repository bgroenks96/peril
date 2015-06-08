package com.forerunnergames.peril.core.shared.net.events.server.request;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.InputRequestEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryInputRequestEvent implements InputRequestEvent
{
  private final PlayerPacket player;

  public PlayerSelectCountryInputRequestEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryInputRequestEvent ()
  {
    player = null;
  }
}
