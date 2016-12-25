package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

public class PlayerDisconnectEvent extends AbstractPlayerEvent
{
  public PlayerDisconnectEvent (final PlayerPacket player)
  {
    super (player);
  }

  public String getPlayerName ()
  {
    return getPersonName ();
  }

  public PlayerPacket getPlayer ()
  {
    return getPerson ();
  }
}
