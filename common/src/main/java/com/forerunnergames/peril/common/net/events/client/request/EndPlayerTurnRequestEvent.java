package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class EndPlayerTurnRequestEvent implements PlayerRequestEvent
{
  @RequiredForNetworkSerialization
  public EndPlayerTurnRequestEvent ()
  {
  }

  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
