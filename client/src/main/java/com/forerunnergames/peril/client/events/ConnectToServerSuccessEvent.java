package com.forerunnergames.peril.client.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public final class ConnectToServerSuccessEvent implements LocalEvent
{
  private final ConnectToServerRequestEvent requestEvent;

  public ConnectToServerSuccessEvent (final ConnectToServerRequestEvent requestEvent)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");

    this.requestEvent = requestEvent;
  }

  public ServerConfiguration getServerConfiguration ()
  {
    return requestEvent.getServerConfiguration ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Original Request Event: {}", getClass ().getSimpleName (), requestEvent);
  }
}
