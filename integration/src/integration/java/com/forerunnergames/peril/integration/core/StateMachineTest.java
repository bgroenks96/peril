
package com.forerunnergames.peril.integration.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.beust.jcommander.internal.Sets;

import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.core.model.state.StateMachineListener;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;
import com.google.common.collect.Queues;

import java.util.Queue;
import java.util.Set;
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
  private final StateMachineEventHandler stateMachine;
  private final StateChangeMonitor monitor = new StateChangeMonitor ();
  private final Logger log;

  public StateMachineTest (final StateMachineEventHandler stateMachine, final Logger log)
  {
    Arguments.checkIsNotNull (stateMachine, "stateMachine");
    Arguments.checkIsNotNull (log, "log");

    this.stateMachine = stateMachine;
    this.log = log;

    stateMachine.addStateMachineListener (monitor);
  }

  /**
   * Waits for the state machine to enter the given state and then asserts that the current state returned by
   * GameStateMachine matches it. This should generally be used only for waiting states (not parent/ephemeral states).
   */
  public void checkCurrentStateIs (final String stateName)
  {
    Arguments.checkIsNotNull (stateMachine, "gameStateMachine");
    Arguments.checkIsNotNull (stateName, "stateName");

    assertFalse (stateMachine.checkError ().isPresent ());
    assertEquals (stateMachine.getCurrentGameStateName (), stateName);
  }

  /**
   * StateMachineTest keeps an internal queue of all states transitioned into by the state machine. This method
   * essentially polls that queue, so the returned value may or may not be the current state the state machine is in,
   * but it allows for verification that the state machine reached the expected state in sequence. Subsequent calls to
   * this method after polling the state machine's current state will continue to return the name of that state.
   */
  public String pollNextState ()
  {
    final Optional <String> nextInQueue = Optional.fromNullable (monitor.stateChangeQueue.poll ());
    if (nextInQueue.isPresent ()) return nextInQueue.get ();
    return stateMachine.getCurrentGameStateName ();
  }

  public void waitForStateChange (final String newStateName, final long timeout)
  {
    Arguments.checkIsNotNull (newStateName, "newStateName");
    Arguments.checkIsNotNegative (timeout, "timeout");

    final StateChangeBarrier barrier = new StateChangeBarrier (newStateName);
    stateMachine.addStateMachineListener (barrier);
    try
    {
      barrier.waitForNewStateEntry (timeout);
    }
    catch (final InterruptedException | TimeoutException e)
    {
      log.error ("Reached state change timeout.", e);
    }
  }

  public OngoingStateCheck entered (final String stateName)
  {
    final OngoingStateCheck stub = new OngoingStateCheck ();
    for (final String nextState : monitor.stateChangeQueue)
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
      super.onEntry (context, state);

      if (state.equals (newStateName)) barrier.arrive ();
    }
  }

  private class StateChangeMonitor extends StateMachineEventAdapter
  {
    private final Queue <String> stateChangeQueue = Queues.newConcurrentLinkedQueue ();

    @Override
    public void onEntry (final String context, final String state)
    {
      super.onEntry (context, state);

      stateChangeQueue.add (state);
    }
  }

  private class StateMachineEventAdapter implements StateMachineListener
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

    @Override
    public void end (final Throwable throwable)
    {
      Arguments.checkIsNotNull (throwable, "throwable");
    }
  }

  public final class OngoingStateCheck
  {
    private Optional <String> state = Optional.absent ();
    private final Set <String> priorStates = Sets.newHashSet ();

    public void after (final String stateName)
    {
      assertTrue (state.isPresent ());
      log.debug (">>>>>>>> STATES BEFORE: {}", priorStates);
      assertTrue (priorStates.contains (stateName));
    }

    private OngoingStateCheck ()
    {
    }
  }
}
