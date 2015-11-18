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

public final class InteractiveDice implements Dice
{
  private static final Logger log = LoggerFactory.getLogger (InteractiveDice.class);
  private final ImmutableSortedSet <Die> dice;
  private final Table table;
  private final DieListener listener;
  private final int absoluteMinDieCount;
  private final int absoluteMaxDieCount;
  private int currentMinDieCount;
  private int currentMaxDieCount;
  private int activeDieCount = currentMaxDieCount;

  public InteractiveDice (final ImmutableSet <Die> dice, final int absoluteMinDieCount, final int absoluteMaxDieCount)
  {
    Arguments.checkIsNotNull (dice, "dice");
    Arguments.checkHasNoNullElements (dice, "dice");
    Arguments.checkIsNotNegative (absoluteMinDieCount, "absoluteMinDieCount");
    Arguments.checkIsNotNegative (absoluteMaxDieCount, "absoluteMaxDieCount");

    this.dice = ImmutableSortedSet.copyOf (dice);
    this.absoluteMinDieCount = absoluteMinDieCount;
    this.absoluteMaxDieCount = absoluteMaxDieCount;
    currentMinDieCount = absoluteMinDieCount;
    currentMaxDieCount = absoluteMaxDieCount;

    table = new Table ().top ().left ();

    activeDieCount = this.dice.size ();

    listener = new DieListener ()
    {
      @Override
      public void onActivate (final Die die)
      {
        log.trace ("Handling newly activated die [{}]... {}", die, InteractiveDice.this);

        ++activeDieCount;

        die.setTouchable (activeDieCount > currentMinDieCount);

        final Die previousDie = InteractiveDice.this.dice.lower (die);
        if (previousDie != null) previousDie.setTouchable (false);

        final Die nextDie = InteractiveDice.this.dice.higher (die);
        if (nextDie != null && activeDieCount < currentMaxDieCount) nextDie.setTouchable (true);

        log.trace ("Finished handling newly activated die [{}]. Previous [{}]. Next [{}]. {}", die, previousDie,
                   nextDie, InteractiveDice.this);
      }

      @Override
      public void onDeactivate (final Die die)
      {
        log.trace ("Handling newly deactivated die [{}]... {}", die, InteractiveDice.this);

        --activeDieCount;

        die.setTouchable (activeDieCount < currentMaxDieCount);

        final Die nextDie = InteractiveDice.this.dice.higher (die);
        if (nextDie != null) nextDie.setTouchable (false);

        final Die previousDie = InteractiveDice.this.dice.lower (die);
        if (previousDie != null && activeDieCount > currentMinDieCount) previousDie.setTouchable (true);

        log.trace ("Finished handling newly deactivated die [{}]. Previous [{}]. Next [{}]. {}", die, previousDie,
                   nextDie, InteractiveDice.this);
      }
    };

    for (final Die die : dice)
    {
      table.add (die.asActor ()).spaceTop (14).spaceBottom (14);
      table.row ();
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

    log.trace ("Clamping dice within range: [{} - {}].", minDieCount, maxDieCount);

    reset ();

    currentMinDieCount = minDieCount;
    currentMaxDieCount = maxDieCount;

    final Iterator <Die> descendingIter = dice.descendingIterator ();

    while (activeDieCount > maxDieCount && descendingIter.hasNext ())
    {
      descendingIter.next ().deactivate ();
    }
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
      die.setTouchable (false);
      die.addListener (listener);
    }

    dice.last ().setTouchable (true);
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

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Active Count: {} | Current Min: {} | Current Max: {} | Dice: {} | Absolute Min: {} | Absolute Max: {}",
                           getClass ().getSimpleName (), activeDieCount, currentMinDieCount, currentMaxDieCount, dice,
                           absoluteMinDieCount, absoluteMaxDieCount);
  }
}
