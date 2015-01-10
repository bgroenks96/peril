package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.tools.common.Event;

public interface JoinServerEvent extends Event
{
  public String getServerAddress ();

  public int getServerTcpPort ();
}
