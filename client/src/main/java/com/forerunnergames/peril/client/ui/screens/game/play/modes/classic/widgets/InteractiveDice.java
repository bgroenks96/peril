package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public final class InteractiveDice extends AbstractDice
{
  public InteractiveDice (final ImmutableSet <Die> dice, final int absoluteMinDieCount, final int absoluteMaxDieCount)
  {
    super (dice, absoluteMinDieCount, absoluteMaxDieCount);

    addListener (new DieListener ()
    {
      @Override
      public void onEnable (final Die die)
      {
        die.setTouchable (canDisableMoreDice ());

        final Optional <Die> previousDie = previousDieFrom (die);
        if (previousDie.isPresent ()) previousDie.get ().setTouchable (false);

        final Optional <Die> nextDie = nextDieFrom (die);
        if (nextDie.isPresent () && canEnableMoreDice ()) nextDie.get ().setTouchable (true);
      }

      @Override
      public void onDisable (final Die die)
      {
        die.setTouchable (canEnableMoreDice ());

        final Optional <Die> nextDie = nextDieFrom (die);
        if (nextDie.isPresent ()) nextDie.get ().setTouchable (false);

        final Optional <Die> previousDie = previousDieFrom (die);
        if (previousDie.isPresent () && canDisableMoreDice ()) previousDie.get ().setTouchable (true);
      }
    });

    reset ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void reset ()
  {
    super.reset ();

    lastDie ().setTouchable (true);
  }
}
