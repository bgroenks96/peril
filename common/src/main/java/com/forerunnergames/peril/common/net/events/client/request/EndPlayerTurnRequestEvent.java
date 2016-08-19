package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;

public final class EndPlayerTurnRequestEvent implements PlayerRequestEvent
{
  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
