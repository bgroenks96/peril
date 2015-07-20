package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public final class DestroyGameServerEvent implements ServerNotificationEvent
{
  @RequiredForNetworkSerialization
  public DestroyGameServerEvent ()
  {
  }
}
