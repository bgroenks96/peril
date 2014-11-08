package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.tools.common.net.events.RequestEvent;

public interface ServerRequestEvent extends RequestEvent
{
  public String getServerAddress();
  public int getServerTcpPort();
}
