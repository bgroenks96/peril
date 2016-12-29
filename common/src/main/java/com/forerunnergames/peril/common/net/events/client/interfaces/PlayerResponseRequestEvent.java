package com.forerunnergames.peril.common.net.events.client.interfaces;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

public interface PlayerResponseRequestEvent <T extends PlayerInputRequestEvent>
        extends ResponseRequestEvent <T>, PlayerAnswerEvent <T>
{
}
