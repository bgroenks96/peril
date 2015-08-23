package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public interface PlayerInputRequestEvent extends ServerRequestEvent
{
  PlayerPacket getPlayer ();
}
