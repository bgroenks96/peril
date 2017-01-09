/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public enum DieState
{
  ENABLED (DieRollable.TRUE, DieOutcomeable.TRUE),
  ENABLING (DieRollable.FALSE, DieOutcomeable.FALSE),
  DISABLING (DieRollable.TRUE, DieOutcomeable.TRUE),
  DISABLED (DieRollable.FALSE, DieOutcomeable.FALSE);

  private static final Table <DieState, DieStateTransition, DieState> TRANSITION_TABLE = HashBasedTable.create ();

  static
  {
    TRANSITION_TABLE.put (ENABLED, DieStateTransition.ON_HOVER_START, DISABLING);
    TRANSITION_TABLE.put (ENABLING, DieStateTransition.ON_HOVER_END, DISABLED);
    TRANSITION_TABLE.put (ENABLING, DieStateTransition.ON_CLICK, ENABLED);
    TRANSITION_TABLE.put (DISABLING, DieStateTransition.ON_CLICK, DISABLED);
    TRANSITION_TABLE.put (DISABLING, DieStateTransition.ON_HOVER_END, ENABLED);
    TRANSITION_TABLE.put (DISABLED, DieStateTransition.ON_HOVER_START, ENABLING);
  }

  private final DieRollable rollable;
  private final DieOutcomeable outcomeable;

  public boolean isRollable ()
  {
    return rollable == DieRollable.TRUE;
  }

  public boolean isOutcomeable ()
  {
    return outcomeable == DieOutcomeable.TRUE;
  }

  public DieState transition (final DieStateTransition transition,
                              final Table <DieState, DieStateTransition, DieStateTransitionAction> transitionActionsTable)
  {
    Arguments.checkIsNotNull (transition, "transition");
    Arguments.checkIsNotNull (transitionActionsTable, "transitionActionsTable");

    final DieState toState = TRANSITION_TABLE.get (this, transition);

    if (toState == null) return this;

    final DieStateTransitionAction transitionAction = transitionActionsTable.get (this, transition);
    if (transitionAction != null) transitionAction.onTransition (toState);

    return toState;
  }

  public String lowerCaseName ()
  {
    return name ().toLowerCase ();
  }

  DieState (final DieRollable rollable, final DieOutcomeable outcomeable)
  {
    this.rollable = rollable;
    this.outcomeable = outcomeable;
  }
}
