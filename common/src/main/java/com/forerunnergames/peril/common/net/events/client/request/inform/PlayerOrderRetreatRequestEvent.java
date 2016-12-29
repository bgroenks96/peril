package com.forerunnergames.peril.common.net.events.client.request.inform;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueAttackOrderEvent;

public final class PlayerOrderRetreatRequestEvent implements PlayerInformRequestEvent <PlayerIssueAttackOrderEvent>
{
  @Override
  public Class <PlayerIssueAttackOrderEvent> getQuestionType ()
  {
    return PlayerIssueAttackOrderEvent.class;
  }

  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
