package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

public final class JoinGameServerRequestEvent implements ClientRequestEvent
{
  @Override
  public String toString ()
  {
    return this.getClass ().getSimpleName ();
  }
}
