package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

public interface MessageSuccessEvent <T extends Message> extends MessageEvent <T>, SuccessEvent
{
}
