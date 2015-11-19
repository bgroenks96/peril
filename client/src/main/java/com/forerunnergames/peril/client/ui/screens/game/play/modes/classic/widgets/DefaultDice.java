package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultDice implements Dice
{
  private static final Logger log = LoggerFactory.getLogger (DefaultDice.class);
  private final ImmutableSortedSet <Die> dice;
  private final Table table;
  private final DieListener listener;
  private final int absoluteMinDieCount;
  private final int absoluteMaxDieCount;
  private boolean isTouchable = true;
  private int currentMinDieCount;
  private int currentMaxDieCount;
  private int activeDieCount;

  public DefaultDice (final ImmutableSet <Die> dice, final int absoluteMinDieCount, final int absoluteMaxDieCount)
  {
    Arguments.checkIsNotNull (dice, "dice");
    Arguments.checkHasNoNullElements (dice, "dice");
    Arguments.checkIsNotNegative (absoluteMinDieCount, "absoluteMinDieCount");
    Arguments.checkIsNotNegative (absoluteMaxDieCount, "absoluteMaxDieCount");

    this.dice = ImmutableSortedSet.copyOf (dice);
    this.absoluteMinDieCount = absoluteMinDieCount;
    this.absoluteMaxDieCount = absoluteMaxDieCount;
    currentMaxDieCount = absoluteMaxDieCount;
    currentMinDieCount = absoluteMinDieCount;
    activeDieCount = this.dice.size ();

    table = new Table ().top ().left ();

    listener = new DieListener ()
    {
      @Override
      public void onEnable (final Die die)
      {
        Arguments.checkIsNotNull (die, "die");

        log.trace ("Handling newly activated die [{}]... {}", die, DefaultDice.this);

        ++activeDieCount;

        if (!isTouchable) return;

        die.setTouchable (canDisableMoreDice ());
        previousDieFrom (die).setTouchable (false);
        nextDieFrom (die).setTouchable (canEnableMoreDice ());

        log.trace ("Finished handling newly activated die [{}]. Previous [{}]. Next [{}]. {}", die,
                   DefaultDice.this.dice.lower (die), DefaultDice.this.dice.higher (die), DefaultDice.this);
      }

      @Override
      public void onDisable (final Die die)
      {
        Arguments.checkIsNotNull (die, "die");

        log.trace ("Handling newly deactivated die [{}]... {}", die, DefaultDice.this);

        --activeDieCount;

        if (!isTouchable) return;

        die.setTouchable (canEnableMoreDice ());
        nextDieFrom (die).setTouchable (false);
        previousDieFrom (die).setTouchable (canDisableMoreDice ());

        log.trace ("Finished handling newly deactivated die [{}]. Previous [{}]. Next [{}]. {}", die,
                   DefaultDice.this.dice.lower (die), DefaultDice.this.dice.higher (die), DefaultDice.this);
      }
    };

    for (final Die die : dice)
    {
      table.add (die.asActor ()).spaceTop (14).spaceBottom (14);
      table.row ();

      die.addListener (listener);
    }

    reset ();
  }

  @Override
  public int getActiveCount ()
  {
    return activeDieCount;
  }

  @Override
  public void roll (final ImmutableList <DieFaceValue> dieFaceValues)
  {
    // @formatter:off
    Arguments.checkIsNotNull (dieFaceValues, "dieFaceValues");
    Arguments.checkHasNoNullElements (dieFaceValues, "dieFaceValues");
    Arguments.checkLowerInclusiveBound (dieFaceValues.size (), currentMinDieCount, "dieFaceValues.size ()", "minDieCount");
    Arguments.checkUpperInclusiveBound (dieFaceValues.size (), currentMaxDieCount, "dieFaceValues.size ()", "maxDieCount");
    Arguments.checkIsTrue (dieFaceValues.size () == activeDieCount,
                           Strings.format ("You must roll exactly {}, but you rolled {}.",
                                           Strings.pluralize (activeDieCount, "die", "dice"), dieFaceValues.size ()));
    // @formatter:on

    final List <DieFaceValue> sortedDieFaceValues = new ArrayList <> (dieFaceValues);
    Collections.sort (sortedDieFaceValues, DieFaceValue.DESCENDING_ORDER);
    final Iterator <DieFaceValue> dieFaceValueIterator = sortedDieFaceValues.iterator ();

    for (final Die die : dice)
    {
      if (!dieFaceValueIterator.hasNext ()) break;

      die.roll (dieFaceValueIterator.next ());
    }
  }

  @Override
  public void clampToMax (final int minDieCount, final int maxDieCount)
  {
    Arguments.checkIsNotNegative (minDieCount, "minDieCount");
    Arguments.checkUpperInclusiveBound (minDieCount, maxDieCount, "minDieCount", "maxDieCount");

    clampToCount (maxDieCount, minDieCount, maxDieCount);
  }

  @Override
  public void setTouchable (final boolean isTouchable)
  {
    this.isTouchable = isTouchable;

    clampToCount (activeDieCount, currentMinDieCount, currentMaxDieCount);
  }

  @Override
  public void reset ()
  {
    currentMinDieCount = absoluteMinDieCount;
    currentMaxDieCount = absoluteMaxDieCount;
    activeDieCount = currentMaxDieCount;

    for (final Die die : dice)
    {
      die.reset ();
    }

    lastDie ().setTouchable (isTouchable);
  }

  @Override
  public void resetPreservingFaceValue ()
  {
    currentMinDieCount = absoluteMinDieCount;
    currentMaxDieCount = absoluteMaxDieCount;
    activeDieCount = currentMaxDieCount;

    for (final Die die : dice)
    {
      die.resetPreservingFaceValue ();
    }

    lastDie ().setTouchable (isTouchable);
  }

  @Override
  public void refreshAssets ()
  {
    for (final Die die : dice)
    {
      die.refreshAssets ();
    }
  }

  @Override
  public Actor asActor ()
  {
    return table;
  }

  private void clampToCount (final int desiredActiveDieCount, final int minDieCount, final int maxDieCount)
  {
    assert minDieCount >= absoluteMinDieCount;
    assert maxDieCount <= absoluteMaxDieCount;
    assert minDieCount <= maxDieCount;
    assert desiredActiveDieCount >= minDieCount;
    assert desiredActiveDieCount <= maxDieCount;

    log.trace ("Clamping dice within range: [{} - {}] to [{}].", minDieCount, maxDieCount, desiredActiveDieCount);

    resetPreservingFaceValue ();

    currentMinDieCount = minDieCount;
    currentMaxDieCount = maxDieCount;

    final Iterator <Die> descendingIter = dice.descendingIterator ();

    while (activeDieCount > desiredActiveDieCount && descendingIter.hasNext ())
    {
      descendingIter.next ().disable ();
    }
  }

  private Die lastDie ()
  {
    return dice.isEmpty () ? Die.NULL_DIE : dice.last ();
  }

  private boolean canEnableMoreDice ()
  {
    return activeDieCount < currentMaxDieCount;
  }

  private boolean canDisableMoreDice ()
  {
    return activeDieCount > currentMinDieCount;
  }

  private Die previousDieFrom (final Die die)
  {
    Arguments.checkIsNotNull (die, "die");

    final Die previousDie = dice.lower (die);

    return previousDie != null ? previousDie : Die.NULL_DIE;
  }

  private Die nextDieFrom (final Die die)
  {
    Arguments.checkIsNotNull (die, "die");

    final Die nextDie = dice.higher (die);

    return nextDie != null ? nextDie : Die.NULL_DIE;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Active Count: {} | Current Min: {} | Current Max: {} | Dice: {} | Absolute Min: {} | Absolute Max: {}",
                           getClass ().getSimpleName (), activeDieCount, currentMinDieCount, currentMaxDieCount, dice,
                           absoluteMinDieCount, absoluteMaxDieCount);
  }
}
