
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

package com.forerunnergames.peril.integration.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;

import java.util.Queue;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

/**
 * Utility type for state machine integration tests.
 */
public final class StateMachineMonitor extends StateMachineEventAdapter
{
  public static final int DEFAULT_TEST_TIMEOUT = 5000;
  public static final int DEFAULT_STATE_CHANGE_TIMOUT = 3000;
  final Logger log;
  private final StateMachineEventHandler stateMachine;
  private final Queue <String> stateChangeHistory = Queues.newLinkedBlockingQueue ();

  public StateMachineMonitor (final StateMachineEventHandler stateMachine, final Logger log)
  {
    Arguments.checkIsNotNull (stateMachine, "stateMachine");
    Arguments.checkIsNotNull (log, "log");

    this.stateMachine = stateMachine;
    this.log = log;

    stateMachine.addStateMachineListener (this);
  }

  @Override
  public void onEntry (final String context, final String state)
  {
    super.onEntry (context, state);

    stateChangeHistory.add (state);
  }

  /**
   * Waits for the state machine to enter the given state and then asserts that the current state returned by
   * GameStateMachine matches it. This should generally be used only for waiting states (not parent/ephemeral states).
   */
  public void checkCurrentStateIs (final String stateName)
  {
    Arguments.checkIsNotNull (stateName, "stateName");

    assertFalse (stateMachine.checkError ().isPresent ());
    assertEquals (stateMachine.getCurrentGameStateName (), stateName);
  }

  /**
   * StateMachineMonitor keeps an internal queue of all states transitioned into by the state machine. This method
   * essentially polls that queue, so the returned value may or may not be the current state the state machine is in,
   * but it allows for verification that the state machine reached the expected state in sequence. Subsequent calls to
   * this method after polling the state machine's current state will continue to return the name of that state.
   */
  public String pollNextState ()
  {
    final Optional <String> nextInQueue = Optional.fromNullable (stateChangeHistory.poll ());
    if (nextInQueue.isPresent ()) return nextInQueue.get ();
    return stateMachine.getCurrentGameStateName ();
  }

  /**
   * @return a list of all states since the last call to {@link #pollNextState()}
   */
  public ImmutableList <String> dumpStateHistory ()
  {
    return ImmutableList.copyOf (stateChangeHistory);
  }

  public boolean waitForStateChange (final String newStateName, final long timeout)
  {
    Arguments.checkIsNotNull (newStateName, "newStateName");
    Arguments.checkIsNotNegative (timeout, "timeout");

    final StateChangeBarrier barrier = new StateChangeBarrier (newStateName);
    stateMachine.addStateMachineListener (barrier);
    try
    {
      barrier.waitForNewStateEntry (timeout);
      return true;
    }
    catch (final InterruptedException | TimeoutException e)
    {
      log.error ("Reached state change timeout.", e);
      return false;
    }
    finally
    {
      stateMachine.removeStateListener (barrier);
    }
  }

  public OngoingStateCheck entered (final String stateName)
  {
    Arguments.checkIsNotNull (stateName, "stateName");

    final OngoingStateCheck stub = new OngoingStateCheck ();
    for (final String nextState : stateChangeHistory)
    {
      if (nextState.equals (stateName))
      {
        stub.state = Optional.of (stateName);
        break;
      }
      stub.priorStates.add (nextState);
    }
    return stub;
  }

  public Optional <Throwable> checkError ()
  {
    return stateMachine.checkError ();
  }
}
