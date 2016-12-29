package com.forerunnergames.peril.common.net.events.client.request.inform;

import com.forerunnergames.peril.common.net.events.client.interfaces.BattleRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueAttackOrderEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class PlayerOrderAttackRequestEvent
        implements BattleRequestEvent, PlayerInformRequestEvent <PlayerIssueAttackOrderEvent>
{
  private final int dieCount;

  public PlayerOrderAttackRequestEvent (final int dieCount)
  {
    Arguments.checkIsNotNegative (dieCount, "dieCount");

    this.dieCount = dieCount;
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public Class <PlayerIssueAttackOrderEvent> getQuestionType ()
  {
    return PlayerIssueAttackOrderEvent.class;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: DieCount: {}", getClass ().getSimpleName (), dieCount);
  }
}
