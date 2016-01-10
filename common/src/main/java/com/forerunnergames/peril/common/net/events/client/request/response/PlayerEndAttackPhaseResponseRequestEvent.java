package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerAttackCountryRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public final class PlayerEndAttackPhaseResponseRequestEvent implements ResponseRequestEvent
{
  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerAttackCountryRequestEvent.class;
  }

  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
