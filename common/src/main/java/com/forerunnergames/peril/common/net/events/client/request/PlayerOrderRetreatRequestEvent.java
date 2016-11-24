package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueAttackOrderEvent;

public final class PlayerOrderRetreatRequestEvent implements InformRequestEvent
{
  @Override
  public Class <? extends PlayerInformEvent> getInformType ()
  {
    return PlayerIssueAttackOrderEvent.class;
  }

  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
