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

import com.forerunnergames.tools.common.Arguments;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class StateChangeBarrier extends StateMachineEventAdapter
{
  private final Phaser barrier = new Phaser (2);
  private final String newStateName;

  StateChangeBarrier (final String newStateName)
  {
    Arguments.checkIsNotNull (newStateName, "newStateName");

    this.newStateName = newStateName;
  }

  @Override
  public void onEntry (final String context, final String state)
  {
    super.onEntry (context, state);

    if (state.equals (newStateName)) barrier.arrive ();
  }

  void waitForNewStateEntry (final long timeoutMs) throws InterruptedException, TimeoutException
  {
    Arguments.checkIsNotNegative (timeoutMs, "timeoutMs");

    barrier.awaitAdvanceInterruptibly (barrier.arrive (), timeoutMs, TimeUnit.MILLISECONDS);
  }
}
