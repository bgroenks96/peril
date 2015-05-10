package com.forerunnergames.peril.core.shared.net.events.server.interfaces;

import com.forerunnergames.peril.core.shared.net.events.interfaces.MessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;

public interface StatusMessageEvent extends MessageEvent <StatusMessage>, GameNotificationEvent
{
}
