package com.forerunnergames.peril.core.shared.net.events.server.interfaces;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseDeniedEvent;

public interface PlayerResponseDeniedEvent <R> extends ResponseDeniedEvent <R>
{
  PlayerPacket getPlayer ();
}
