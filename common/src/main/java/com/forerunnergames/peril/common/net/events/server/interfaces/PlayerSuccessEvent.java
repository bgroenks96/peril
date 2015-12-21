package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

public interface PlayerSuccessEvent extends SuccessEvent, ServerEvent
{
  PlayerPacket getPlayer ();
}
