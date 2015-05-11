package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.GameNotificationEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DestroyGameServerEvent implements GameNotificationEvent
{
  @RequiredForNetworkSerialization
  private DestroyGameServerEvent ()
  {
  }
}
