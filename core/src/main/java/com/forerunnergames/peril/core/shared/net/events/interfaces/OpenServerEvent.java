package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.tools.common.Event;

public interface OpenServerEvent extends Event
{
  public String getServerName();
  public int getServerTcpPort();
}
