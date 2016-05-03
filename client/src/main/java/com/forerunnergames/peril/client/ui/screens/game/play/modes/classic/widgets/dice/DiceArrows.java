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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dice;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;

public final class DiceArrows
{
  private static final int DICE_ARROW_VERTICAL_SPACING = 30;
  private final ImmutableSet <DiceArrow> arrows;
  private final Table table;

  public DiceArrows (final ImmutableSet <DiceArrow> arrows)
  {
    Arguments.checkIsNotNull (arrows, "arrows");
    Arguments.checkHasNoNullElements (arrows, "arrows");

    this.arrows = arrows;

    table = new Table ();

    for (final DiceArrow arrow : arrows)
    {
      table.add (arrow.asActor ()).spaceTop (DICE_ARROW_VERTICAL_SPACING).spaceBottom (DICE_ARROW_VERTICAL_SPACING);
      table.row ();
    }
  }

  public void setOutcomes (final ImmutableList <DieOutcome> attackerDieOutcomes,
                           final ImmutableList <DieOutcome> defenderDieOutcomes)
  {
    Arguments.checkIsNotNull (attackerDieOutcomes, "attackerDieOutcomes");
    Arguments.checkHasNoNullElements (attackerDieOutcomes, "attackerDieOutcomes");
    Arguments.checkIsNotNull (defenderDieOutcomes, "defenderDieOutcomes");
    Arguments.checkHasNoNullElements (defenderDieOutcomes, "defenderDieOutcomes");

    final Iterator <DieOutcome> attackerOutcomeIter = attackerDieOutcomes.iterator ();
    final Iterator <DieOutcome> defenderOutcomeIter = defenderDieOutcomes.iterator ();

    for (final DiceArrow arrow : arrows)
    {
      if (!attackerOutcomeIter.hasNext () || !defenderOutcomeIter.hasNext ()) return;

      arrow.setOutcome (attackerOutcomeIter.next (), defenderOutcomeIter.next ());
    }
  }

  public void reset ()
  {
    for (final DiceArrow arrow : arrows)
    {
      arrow.reset ();
    }
  }

  public void refreshAssets ()
  {
    for (final DiceArrow arrow : arrows)
    {
      arrow.refreshAssets ();
    }
  }

  public Actor asActor ()
  {
    return table;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Arrows: {} | Vertical Spacing: {}", getClass ().getSimpleName (), arrows,
                           DICE_ARROW_VERTICAL_SPACING);
  }
}
