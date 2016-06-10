package com.forerunnergames.peril.core.events.internal.player;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

public interface InboundPlayerResponseRequestEvent <T extends ResponseRequestEvent, R extends PlayerInputRequestEvent>
        extends InboundPlayerRequestEvent <T>
{
  R getOriginalRequestEvent ();
}
