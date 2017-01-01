package com.forerunnergames.peril.common.net.events.client.interfaces;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputInformEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.InformRequestEvent;

public interface PlayerInformRequestEvent <T extends PlayerInputInformEvent>
        extends InformRequestEvent <T>, PlayerAnswerEvent <T>, PlayerRequestEvent
{
}
