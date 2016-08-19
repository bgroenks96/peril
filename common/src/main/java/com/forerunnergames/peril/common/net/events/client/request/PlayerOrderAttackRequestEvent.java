package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.client.interfaces.BattleRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class PlayerOrderAttackRequestEvent implements BattleRequestEvent
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
  public String toString ()
  {
    return Strings.format ("{}: DieCount: {}", getClass ().getSimpleName (), dieCount);
  }
}
