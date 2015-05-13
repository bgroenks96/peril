package com.forerunnergames.peril.core.shared.net.events.server.request;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.InputRequestEvent;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryInputRequestEvent implements InputRequestEvent
{
  private final PlayerPacket player;

  public PlayerSelectCountryInputRequestEvent (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = new DefaultPlayerPacket (player);
  }

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
