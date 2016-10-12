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
