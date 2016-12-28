package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

public final class SuspendGameEvent implements BroadcastNotificationEvent
{
  public enum Reason
  {
    PLAYER_UNAVAILABLE,
    REQUESTED_BY_HOST
  }

  private final Reason reason;
  private final long timeoutMillis;

  public SuspendGameEvent (final Reason reason)
  {
    this (reason, 0);
  }

  public SuspendGameEvent (final Reason reason, final long timeoutMillis)
  {
    this.reason = reason;
    this.timeoutMillis = timeoutMillis;
  }

  public Reason getReason ()
  {
    return reason;
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
    return Strings.format ("{}: Reason: {} | Timeout: {}", getClass ().getSimpleName (), reason,
                           hasTimeout () ? timeoutMillis : "None");
  }
}
