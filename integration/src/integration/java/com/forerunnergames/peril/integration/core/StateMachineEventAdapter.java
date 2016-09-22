package com.forerunnergames.peril.integration.core;

import com.forerunnergames.peril.core.model.state.StateMachineListener;
import com.forerunnergames.tools.common.Arguments;

import javax.annotation.Nullable;

class StateMachineEventAdapter implements StateMachineListener
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
  public void onActionException (final String context, @Nullable final Throwable throwable)
  {
    Arguments.checkIsNotNull (context, "context");
  }

  @Override
  public void end (@Nullable final Throwable throwable)
  {
  }
}
