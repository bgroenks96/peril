package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AttackerDice
{
  private static final int ABSOLUTE_MIN_DIE_COUNT = 1;
  private static final int ABSOLUTE_MAX_DIE_COUNT = 3;
  private static final Logger log = LoggerFactory.getLogger (AttackerDice.class);
  private final ImmutableSortedSet <AttackerDie> dice;
  private final Table table;
  private final DieListener listener;
  private int minDieCount = ABSOLUTE_MIN_DIE_COUNT;
  private int maxDieCount = ABSOLUTE_MAX_DIE_COUNT;
  private int activeDieCount = maxDieCount;

  public AttackerDice (final AttackerDie... dice)
  {
    Arguments.checkIsNotNull (dice, "dice");
    Arguments.checkHasNoNullElements (dice, "dice");

    this.dice = ImmutableSortedSet.copyOf (dice);
    table = new Table ().top ().left ();
    activeDieCount = this.dice.size ();
    listener = new DieListener ()
    {
      @Override
      public void onActivate (final AttackerDie die)
      {
        log.trace ("Handling newly activated attacker die [{}]... {}", die, AttackerDice.this);

        ++activeDieCount;

        die.setTouchable (activeDieCount > minDieCount);

        final AttackerDie previousDie = AttackerDice.this.dice.lower (die);
        if (previousDie != null) previousDie.setTouchable (false);

        final AttackerDie nextDie = AttackerDice.this.dice.higher (die);
        if (nextDie != null && activeDieCount < maxDieCount) nextDie.setTouchable (true);

        log.trace ("Finished handling newly activated attacker die [{}]. Previous [{}]. Next [{}]. {}", die,
                   previousDie, nextDie, AttackerDice.this);
      }

      @Override
      public void onDeactivate (final AttackerDie die)
      {
        log.trace ("Handling newly deactivated attacker die [{}]... {}", die, AttackerDice.this);

        --activeDieCount;

        die.setTouchable (activeDieCount < maxDieCount);

        final AttackerDie nextDie = AttackerDice.this.dice.higher (die);
        if (nextDie != null) nextDie.setTouchable (false);

        final AttackerDie previousDie = AttackerDice.this.dice.lower (die);
        if (previousDie != null && activeDieCount > minDieCount) previousDie.setTouchable (true);

        log.trace ("Finished handling newly deactivated attacker die [{}]. Previous [{}]. Next [{}]. {}", die,
                   previousDie, nextDie, AttackerDice.this);
      }
    };

    for (final AttackerDie die : dice)
    {
      table.add (die.asActor ()).spaceTop (14).spaceBottom (14);
      table.row ();
    }

    reset ();
  }

  public void clampAndSetToMax (final int minDieCount, final int maxDieCount)
  {
    Arguments.checkIsNotNegative (minDieCount, "minDieCount");
    Arguments.checkUpperInclusiveBound (minDieCount, maxDieCount, "minDieCount", "maxDieCount");

    log.trace ("Clamping attacker dice within range: [{} - {}].", minDieCount, maxDieCount);

    reset ();

    this.minDieCount = minDieCount;
    this.maxDieCount = maxDieCount;

    final Iterator <AttackerDie> descendingIter = dice.descendingIterator ();

    while (activeDieCount > maxDieCount && descendingIter.hasNext ())
    {
      descendingIter.next ().deactivate ();
    }
  }

  public int getActiveCount ()
  {
    return activeDieCount;
  }

  public void roll (final ImmutableList <DieFaceValue> dieFaceValues)
  {
    Arguments.checkIsNotNull (dieFaceValues, "dieFaceValues");
    Arguments.checkHasNoNullElements (dieFaceValues, "dieFaceValues");
    Arguments.checkLowerInclusiveBound (dieFaceValues.size (), minDieCount, "dieFaceValues.size ()", "minDieCount");
    Arguments.checkUpperInclusiveBound (dieFaceValues.size (), maxDieCount, "dieFaceValues.size ()", "maxDieCount");
    Arguments.checkIsTrue (dieFaceValues.size () == activeDieCount,
                           Strings.format ("You must roll exactly {}, but you rolled {}.",
                                           Strings.pluralize (activeDieCount, "die", "dice"), dieFaceValues.size ()));

    final List <DieFaceValue> sortedDieFaceValues = new ArrayList <> (dieFaceValues);
    Collections.sort (sortedDieFaceValues, DieFaceValue.DESCENDING_ORDER);
    final Iterator <DieFaceValue> dieFaceValueIterator = sortedDieFaceValues.iterator ();

    for (final AttackerDie die : dice)
    {
      if (!dieFaceValueIterator.hasNext ()) break;

      die.roll (dieFaceValueIterator.next ());
    }
  }

  public void refreshAssets ()
  {
    for (final AttackerDie die : dice)
    {
      die.refreshAssets ();
    }
  }

  public Actor asActor ()
  {
    return table;
  }

  public void reset ()
  {
    minDieCount = ABSOLUTE_MIN_DIE_COUNT;
    maxDieCount = ABSOLUTE_MAX_DIE_COUNT;
    activeDieCount = maxDieCount;

    for (final AttackerDie die : dice)
    {
      die.reset ();
      die.setTouchable (false);
      die.addListener (listener);
    }

    dice.last ().setTouchable (true);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Active: {} | Min: {} | Max: {} | Dice: {}", getClass ().getSimpleName (),
                           activeDieCount, minDieCount, maxDieCount, dice);
  }
}
