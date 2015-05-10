package com.forerunnergames.peril.core.shared.net.events.server.interfaces;

import com.forerunnergames.peril.core.shared.net.events.interfaces.InputResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.InputResponseDeniedEvent.Reason;
import com.forerunnergames.tools.net.events.DeniedEvent;

public interface InputResponseDeniedEvent extends InputResponseEvent, DeniedEvent <Reason>
{
  // TODO
  public enum Reason
  {
  }
}
