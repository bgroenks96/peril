package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.tools.common.Event;

public interface KickEvent extends Event
{
  public String getReasonForKick();
}
