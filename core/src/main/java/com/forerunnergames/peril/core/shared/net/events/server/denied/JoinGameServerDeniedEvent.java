package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.ServerConfiguration;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.DeniedEvent;

public final class JoinGameServerDeniedEvent implements JoinGameServerEvent, DeniedEvent <String>
{
  private final JoinGameServerRequestEvent requestEvent;
  private final DeniedEvent <String> deniedEvent;

  public JoinGameServerDeniedEvent (final JoinGameServerRequestEvent event, final String reason)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (reason, "reason");

    requestEvent = event;
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  @Override
  public ServerConfiguration getConfiguration ()
  {
    return requestEvent.getConfiguration ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Original request: %2$s | %3$s", ((Object) this).getClass ().getSimpleName (),
                          requestEvent, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerDeniedEvent ()
  {
    requestEvent = null;
    deniedEvent = null;
  }
}
