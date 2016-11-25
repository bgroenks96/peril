package com.forerunnergames.peril.core.model.state.events;

import com.forerunnergames.tools.common.Strings;

public final class SuspendGameEvent implements StateEvent
{
  private final long timeoutMillis;

  public SuspendGameEvent ()
  {
    this (0);
  }

  public SuspendGameEvent (final long timeoutMillis)
  {
    this.timeoutMillis = timeoutMillis;
  }

  public boolean hasTimeout ()
  {
    return timeoutMillis > 0;
  }

  public long getTimeoutMillis ()
  {
    return timeoutMillis;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Timeout: {}", getClass ().getSimpleName (), hasTimeout () ? timeoutMillis : "None");
  }
}
