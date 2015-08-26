package com.forerunnergames.peril.client.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public final class ConnectToServerDeniedEvent implements LocalEvent
{
  private final ConnectToServerRequestEvent requestEvent;
  private final String reason;

  public ConnectToServerDeniedEvent (final ConnectToServerRequestEvent requestEvent, final String reason)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");
    Arguments.checkIsNotNull (reason, "reason");

    this.requestEvent = requestEvent;
    this.reason = reason;
  }

  public String getReason ()
  {
    return reason;
  }

  public ServerConfiguration getServerConfiguration ()
  {
    return requestEvent.getServerConfiguration ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Reason For Denial: {} | Original Request Event: {}", getClass ().getSimpleName (),
                           reason, requestEvent);
  }
}
