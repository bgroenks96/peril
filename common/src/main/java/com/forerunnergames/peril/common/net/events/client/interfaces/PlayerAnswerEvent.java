package com.forerunnergames.peril.common.net.events.client.interfaces;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientAnswerEvent;

public interface PlayerAnswerEvent <T extends PlayerInputEvent> extends ClientAnswerEvent <T>
{
}
