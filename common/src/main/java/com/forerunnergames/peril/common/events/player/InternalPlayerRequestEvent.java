package com.forerunnergames.peril.common.events.player;

import com.forerunnergames.peril.common.events.InternalRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.RequestEvent;

public interface InternalPlayerRequestEvent <T extends RequestEvent> extends InternalRequestEvent
{
  PlayerPacket getPlayer ();

  T getRequestEvent ();
}
