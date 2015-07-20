package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public final class JoinGameServerDeniedEvent implements DeniedEvent <String>
{
  private final JoinGameServerRequestEvent requestEvent;
  private final ClientConfiguration clientConfig;
  private final DeniedEvent <String> deniedEvent;

  public JoinGameServerDeniedEvent (final JoinGameServerRequestEvent requestEvent,
                                    final ClientConfiguration clientConfig,
                                    final String reason)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (reason, "reason");

    this.requestEvent = requestEvent;
    this.clientConfig = clientConfig;
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  public ServerConfiguration getServerConfiguration ()
  {
    return requestEvent.getServerConfiguration ();
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Original request: %2$s | Client Configuration: %3$s | %4$s", getClass ()
            .getSimpleName (), requestEvent, clientConfig, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerDeniedEvent ()
  {
    requestEvent = null;
    clientConfig = null;
    deniedEvent = null;
  }
}
