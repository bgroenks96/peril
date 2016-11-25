package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerQuitGameSuccessEvent extends AbstractPlayerEvent implements PlayerSuccessEvent
{
  public PlayerQuitGameSuccessEvent (final PlayerPacket player)
  {
    super (player);
  }

  @RequiredForNetworkSerialization
  private PlayerQuitGameSuccessEvent ()
  {
  }
}
