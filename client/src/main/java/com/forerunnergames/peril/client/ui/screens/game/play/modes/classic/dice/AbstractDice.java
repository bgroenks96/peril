/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.DieRoll;
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

abstract class AbstractDice implements Dice
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final ImmutableSortedSet <Die> dice;
  private final Table table;
  private final DieListener listener;
  private final DieRange absoluteDieRange;
  private DieRange currentDieRange;
  private boolean isTouchable = Dice.DEFAULT_IS_TOUCHABLE;
  private int activeDieCount;

  protected AbstractDice (final ImmutableSet <Die> dice, final DieRange absoluteDieRange)
  {
    Arguments.checkIsNotNull (dice, "dice");
    Arguments.checkHasNoNullElements (dice, "dice");
    Arguments.checkIsNotNull (absoluteDieRange, "absoluteDieRange");

    this.dice = ImmutableSortedSet.copyOf (dice);
    this.absoluteDieRange = absoluteDieRange;
    currentDieRange = absoluteDieRange;
    activeDieCount = this.dice.size ();

    table = new Table ();

    listener = new DieListener ()
    {
      @Override
      public void onEnable (final Die die)
      {
        Arguments.checkIsNotNull (die, "die");

        log.trace ("Handling newly activated die [{}]... {}", die, AbstractDice.this);

        ++activeDieCount;

        if (!isTouchable) return;

        die.setTouchable (canDisableMoreDice ());
        previousDieFrom (die).setTouchable (false);
        nextDieFrom (die).setTouchable (canEnableMoreDice ());

        log.trace ("Finished handling newly activated die [{}]. Previous [{}]. Next [{}]. {}", die,
                   AbstractDice.this.dice.lower (die), AbstractDice.this.dice.higher (die), AbstractDice.this);
      }

      @Override
      public void onDisable (final Die die)
      {
        Arguments.checkIsNotNull (die, "die");

        log.trace ("Handling newly deactivated die [{}]... {}", die, AbstractDice.this);

        --activeDieCount;

        if (!isTouchable) return;

        die.setTouchable (canEnableMoreDice ());
        nextDieFrom (die).setTouchable (false);
        previousDieFrom (die).setTouchable (canDisableMoreDice ());

        log.trace ("Finished handling newly deactivated die [{}]. Previous [{}]. Next [{}]. {}", die,
                   AbstractDice.this.dice.lower (die), AbstractDice.this.dice.higher (die), AbstractDice.this);
      }
    };

    for (final Die die : dice)
    {
      table.add (die.asActor ()).spaceTop (2).spaceBottom (2).size (die.getWidth (), die.getHeight ());
      table.row ();

      die.addListener (listener);
    }

    resetAll ();
  }

  @Override
  public final int getActiveCount ()
  {
    return activeDieCount;
  }

  @Override
  public final void roll (final ImmutableList <DieRoll> rolls)
  {
    Arguments.checkIsNotNull (rolls, "rolls");
    Arguments.checkHasNoNullElements (rolls, "rolls");
    Arguments.checkIsTrue (rolls.size () == activeDieCount,
                           Strings.format ("You must roll exactly {}, but you rolled {}.",
                                           Strings.pluralize (activeDieCount, "die", "dice"), rolls.size ()));

    final Iterator <DieRoll> dieRollIterator = sortDescendingByFaceValue (rolls);

    for (final Die die : dice)
    {
      if (!dieRollIterator.hasNext ()) return;

      die.roll (dieRollIterator.next ().getDieValue ());
    }
  }

  @Override
  public void setOutcomes (final ImmutableList <DieRoll> rolls)
  {
    Arguments.checkIsNotNull (rolls, "rolls");
    Arguments.checkHasNoNullElements (rolls, "rolls");
    Arguments.checkIsTrue (rolls.size () == activeDieCount,
                           Strings.format ("You must specify exactly {} rolls, but you specified {}.",
                                           Strings.pluralize (activeDieCount, "die", "dice"), rolls.size ()));

    final Iterator <DieRoll> dieRollIterator = sortDescendingByFaceValue (rolls);

    for (final Die die : dice)
    {
      if (!dieRollIterator.hasNext ()) return;

      die.setOutcome (dieRollIterator.next ().getOutcome ());
    }
  }

  @Override
  public final void clamp (final DieRange dieRange)
  {
    Arguments.checkIsNotNull (dieRange, "dieRange");

    if (dieRange.includes (activeDieCount))
    {
      clampToCount (activeDieCount, dieRange);
    }
    else if (dieRange.excludesHigh (activeDieCount))
    {
      clampToCount (dieRange.max (), dieRange);
    }
    else
    {
      clampToCount (dieRange.min (), dieRange);
    }
  }

  @Override
  public void clampToCount (final int dieCount, final DieRange dieRange)
  {
    Arguments.checkIsNotNegative (dieCount, "dieCount");
    Arguments.checkIsNotNull (dieRange, "dieRange");

    log.trace ("Clamping dice within range: [{}] to [{}].", dieRange, dieCount);

    resetPreserveFaceValueAndOutcome ();

    currentDieRange = dieRange;

    final Iterator <Die> descendingIter = dice.descendingIterator ();

    while (activeDieCount > dieCount && descendingIter.hasNext ())
    {
      descendingIter.next ().disable ();
    }
  }

  @Override
  public final void setTouchable (final boolean isTouchable)
  {
    this.isTouchable = isTouchable;

    clampToCount (activeDieCount, currentDieRange);
  }

  @Override
  public final void resetFaceValues ()
  {
    for (final Die die : dice)
    {
      die.resetFaceValue ();
    }
  }

  @Override
  public final void resetOutcomes ()
  {
    for (final Die die : dice)
    {
      die.resetOutcome ();
    }
  }

  @Override
  public final void resetSpinning ()
  {
    for (final Die die : dice)
    {
      die.resetSpinning ();
    }
  }

  @Override
  public final void resetAll ()
  {
    currentDieRange = absoluteDieRange;
    activeDieCount = absoluteDieRange.max ();

    for (final Die die : dice)
    {
      die.resetAll ();
    }

    lastDie ().setTouchable (isTouchable);
  }

  @Override
  public final void refreshAssets ()
  {
    for (final Die die : dice)
    {
      die.refreshAssets ();
    }
  }

  @Override
  public final void update (final float delta)
  {
    for (final Die die : dice)
    {
      die.update (delta);
    }
  }

  @Override
  public final Actor asActor ()
  {
    return table;
  }

  private Iterator <DieRoll> sortDescendingByFaceValue (final ImmutableList <DieRoll> dieRolls)
  {
    final List <DieRoll> sortedDieRolls = new ArrayList <> (dieRolls);

    Collections.sort (sortedDieRolls, DieRoll.DESCENDING_BY_FACE_VALUE);

    return sortedDieRolls.iterator ();
  }

  private void resetPreserveFaceValueAndOutcome ()
  {
    currentDieRange = absoluteDieRange;
    activeDieCount = absoluteDieRange.max ();

    for (final Die die : dice)
    {
      die.setTouchable (false);
      die.resetState ();
    }

    lastDie ().setTouchable (isTouchable);
  }

  private Die lastDie ()
  {
    return dice.isEmpty () ? Die.NULL_DIE : dice.last ();
  }

  private boolean canEnableMoreDice ()
  {
    return activeDieCount < currentDieRange.max ();
  }

  private boolean canDisableMoreDice ()
  {
    return activeDieCount > currentDieRange.min ();
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
    return Strings.format ("{}: Active Count: [{}] | Current Range: [{}] | Absolute Range: [{}] | Dice: [{}]",
                           getClass ().getSimpleName (), activeDieCount, currentDieRange, absoluteDieRange, dice);
  }
}
