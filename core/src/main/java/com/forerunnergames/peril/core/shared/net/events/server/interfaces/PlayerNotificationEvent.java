package com.forerunnergames.peril.core.shared.net.events.server.interfaces;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public interface PlayerNotificationEvent extends ServerNotificationEvent
{
  PlayerPacket getPlayer ();
}
