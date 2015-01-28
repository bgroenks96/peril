package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.ServerConfiguration;

public interface JoinGameServerEvent extends Event
{
  public ServerConfiguration getConfiguration();
}
