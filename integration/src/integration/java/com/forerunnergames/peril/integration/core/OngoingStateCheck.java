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
