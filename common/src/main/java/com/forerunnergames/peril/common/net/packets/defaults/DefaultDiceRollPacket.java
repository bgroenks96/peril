package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.battle.DiceRollResultPacket;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class DefaultDiceRollPacket implements DiceRollResultPacket
{
  private final int dieCount;
  private final ImmutableList <Integer> dieValues;

  public DefaultDiceRollPacket (final int dieCount, final ImmutableList <Integer> dieValues)
  {
    Arguments.checkIsNotNegative (dieCount, "dieCount");
    Arguments.checkIsNotNull (dieValues, "dieValues");
    Arguments.checkHasNoNullElements (dieValues, "dieValues");

    // ensure list sorting
    final List <Integer> mutableDieValues = Lists.newArrayList (dieValues);
    Collections.sort (mutableDieValues);

    this.dieCount = dieCount;
    this.dieValues = ImmutableList.copyOf (mutableDieValues);
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public ImmutableList <Integer> getDieValues ()
  {
    return dieValues;
  }
}
