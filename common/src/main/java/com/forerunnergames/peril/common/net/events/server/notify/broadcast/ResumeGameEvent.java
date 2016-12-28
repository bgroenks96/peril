package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

public final class ResumeGameEvent implements BroadcastNotificationEvent
{
  @Override
  public String toString ()
  {
    return getClass ().getSimpleName ();
  }
}
