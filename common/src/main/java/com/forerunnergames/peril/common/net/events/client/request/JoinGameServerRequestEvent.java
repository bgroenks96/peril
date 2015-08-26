package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

public final class JoinGameServerRequestEvent implements ClientRequestEvent
{
  @RequiredForNetworkSerialization
  public JoinGameServerRequestEvent ()
  {
  }
}
