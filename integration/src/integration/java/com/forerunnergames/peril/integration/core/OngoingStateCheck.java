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

package com.forerunnergames.peril.integration.core;

import static org.testng.Assert.assertTrue;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;
import com.google.common.collect.Queues;

import java.util.Queue;

public final class OngoingStateCheck
{
  final Queue <String> priorStates = Queues.newArrayDeque ();
  Optional <String> state = Optional.absent ();

  public boolean after (final String stateName)
  {
    Arguments.checkIsNotNull (stateName, "stateName");

    return state.isPresent () && priorStates.contains (stateName);
  }

  public boolean atLeastOnce ()
  {
    return state.isPresent ();
  }

  public boolean atLeastNTimes (final int n)
  {
    if (n <= 0) return true;

    assertTrue (state.isPresent ());

    final String stateName = state.get ();
    int count = 1;
    for (final String next : priorStates)
    {
      if (next.equals (stateName)) count++;
    }

    return count >= n;
  }

  public OngoingStateCheck delay (final long millis)
  {
    try
    {
      Thread.sleep (millis);
    }
    catch (final InterruptedException e)
    {
      e.printStackTrace ();
    }

    return this;
  }
}
