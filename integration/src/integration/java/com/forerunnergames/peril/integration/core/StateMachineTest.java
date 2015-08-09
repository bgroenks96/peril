
package com.forerunnergames.peril.integration.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import com.forerunnergames.peril.core.model.state.GameStateMachine;
import com.forerunnergames.peril.core.model.state.GameStateMachine.StateListener;
import com.forerunnergames.tools.common.Arguments;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

/**
 * Utility type for state machine integration tests.
 */
public final class StateMachineTest
{
  public static final int DEFAULT_TEST_TIMEOUT = 5000;
  public static final int DEFAULT_STATE_CHANGE_TIMOUT = 3000;
  private final GameStateMachine stateMachine;
  private final Logger log;

  public StateMachineTest (final GameStateMachine stateMachine, final Logger log)
  {
    Arguments.checkIsNotNull (stateMachine, "stateMachine");
    Arguments.checkIsNotNull (log, "log");

    this.stateMachine = stateMachine;
    this.log = log;
  }

  /**
   * Waits for the state machine to enter the given state and then asserts that the current state returned by
   * GameStateMachine matches it. This should generally be used only for waiting states (not parent/ephemeral states).
   */
  public void checkStateIs (final String stateName)
  {
    Arguments.checkIsNotNull (stateMachine, "gameStateMachine");
    Arguments.checkIsNotNull (stateName, "stateName");

    assertFalse (stateMachine.checkError ().isPresent ());
    assertEquals (stateMachine.getCurrentGameStateName (), stateName);
  }

  public void waitForStateChange (final String newStateName, final long timeout)
  {
    Arguments.checkIsNotNull (newStateName, "newStateName");
    Arguments.checkIsNotNegative (timeout, "timeout");

    final StateChangeBarrier barrier = new StateChangeBarrier (newStateName);
    stateMachine.addStateListener (barrier);
    try
    {
      barrier.waitForNewStateEntry (timeout);
    }
    catch (final InterruptedException | TimeoutException e)
    {
      log.error ("Reached state change timeout.", e);
    }
  }

  private class StateChangeBarrier extends StateMachineEventAdapter
  {
    private final Phaser barrier = new Phaser (2);
    private final String newStateName;

    StateChangeBarrier (final String newStateName)
    {
      Arguments.checkIsNotNull (newStateName, "newStateName");

      this.newStateName = newStateName;
    }

    void waitForNewStateEntry (final long timeout) throws InterruptedException, TimeoutException
    {
      Arguments.checkIsNotNegative (timeout, "timeout");

      barrier.awaitAdvanceInterruptibly (barrier.arrive (), timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onEntry (final String context, final String state)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (state, "state");

      super.onEntry (context, state);

      if (state.equals (newStateName)) barrier.arrive ();
    }
  }

  private class StateMachineEventAdapter implements StateListener
  {
    @Override
    public void onEntry (final String context, final String state)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (state, "state");
    }

    @Override
    public void onExit (final String context, final String state)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (state, "state");
    }

    @Override
    public void onTransitionBegin (final String context,
                                   final String statePrevious,
                                   final String stateNext,
                                   final String transition)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (statePrevious, "statePrevious");
      Arguments.checkIsNotNull (stateNext, "stateNext");
      Arguments.checkIsNotNull (transition, "transition");
    }

    @Override
    public void onTransitionEnd (final String context,
                                 final String statePrevious,
                                 final String stateNext,
                                 final String transition)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (statePrevious, "statePrevious");
      Arguments.checkIsNotNull (stateNext, "stateNext");
      Arguments.checkIsNotNull (transition, "transition");
    }

    @Override
    public void onTimerStart (final String context, final String name, final long duration)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (name, "name");
      Arguments.checkIsNotNegative (duration, "duration");
    }

    @Override
    public void onTimerStop (final String context, final String name)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (name, "name");
    }

    @Override
    public void onActionException (final String context, final Throwable throwable)
    {
      Arguments.checkIsNotNull (context, "context");
      Arguments.checkIsNotNull (throwable, "throwable");
    }
  }
}
