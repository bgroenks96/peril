package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

public interface PlayerEvent
{
  PlayerPacket getPlayer ();

  String getPlayerName ();
}
