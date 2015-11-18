package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.google.common.collect.ImmutableSet;

public final class NonInteractiveDice extends AbstractDice
{
  public NonInteractiveDice (final ImmutableSet <Die> dice,
                             final int absoluteMinDieCount,
                             final int absoluteMaxDieCount)
  {
    super (dice, absoluteMinDieCount, absoluteMaxDieCount);
  }
}
