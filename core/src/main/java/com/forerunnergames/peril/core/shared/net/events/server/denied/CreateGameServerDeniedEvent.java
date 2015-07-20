package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.client.request.CreateGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CreateGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class CreateGameServerDeniedEvent implements CreateGameServerEvent, DeniedEvent <String>
{
  private final CreateGameServerRequestEvent requestEvent;
  private final ClientConfiguration clientConfig;
  private final DeniedEvent <String> deniedEvent;

  public CreateGameServerDeniedEvent (final CreateGameServerRequestEvent requestEvent,
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

  @Override
  public GameServerConfiguration getGameServerConfiguration ()
  {
    return requestEvent.getGameServerConfiguration ();
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
  private CreateGameServerDeniedEvent ()
  {
    requestEvent = null;
    clientConfig = null;
    deniedEvent = null;
  }
}
