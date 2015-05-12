package com.forerunnergames.peril.core.shared.net.events.server.interfaces;

import com.forerunnergames.peril.core.shared.net.events.interfaces.InputResponseEvent;
import com.forerunnergames.tools.net.events.DeniedEvent;

public interface InputResponseDeniedEvent <T> extends InputResponseEvent, DeniedEvent <T>
{
}
