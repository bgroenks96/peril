package com.forerunnergames.peril.core.model.state;

import com.forerunnergames.tools.common.Arguments;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingStateMachineListener implements StateMachineListener
{
  private static final Logger log = LoggerFactory.getLogger (LoggingStateMachineListener.class);

  @Override
  public void onEntry (final String context, final String state)
  {
    Arguments.checkIsNotNull (context, "context");
    Arguments.checkIsNotNull (state, "state");

    log.trace ("{}: entering in state {}", context, state);
  }

  @Override
  public void onExit (final String context, final String state)
  {
    Arguments.checkIsNotNull (context, "context");
    Arguments.checkIsNotNull (state, "state");

    log.trace ("{}: leaving from state {}", context, state);
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

    log.trace ("{}: transition begins from state {} to {}, event {}", context, statePrevious, stateNext, transition);
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

    log.trace ("{}: transition has ended from state {} to {}, event {}", context, statePrevious, stateNext, transition);
  }

  @Override
  public void onTimerStart (final String context, final String name, final long duration)
  {
    Arguments.checkIsNotNull (context, "context");
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (duration, "duration");

    log.trace ("{}: start timer {} for {} ms", context, name, duration);
  }

  @Override
  public void onTimerStop (final String context, final String name)
  {
    Arguments.checkIsNotNull (context, "context");
    Arguments.checkIsNotNull (name, "name");

    log.trace ("{}: stop timer {}", context, name);
  }

  @Override
  public void onActionException (final String context, final Throwable throwable)
  {
    Arguments.checkIsNotNull (context, "context");
    Arguments.checkIsNotNull (throwable, "throwable");

    log.trace ("{}: exception during action: {}", context, throwable.getMessage ());
  }

  @Override
  public void end (@Nullable final Throwable throwable)
  {
    if (throwable != null)
    {
      log.error ("The state machine ended with an error.", throwable);
    }
  }
}
