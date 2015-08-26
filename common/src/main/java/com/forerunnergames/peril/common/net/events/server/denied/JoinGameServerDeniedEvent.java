package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class JoinGameServerDeniedEvent implements DeniedEvent <String>
{
  private final ClientConfiguration clientConfig;
  private final DeniedEvent <String> deniedEvent;

  public JoinGameServerDeniedEvent (final ClientConfiguration clientConfig, final String reason)
  {
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (reason, "reason");

    this.clientConfig = clientConfig;
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Client Configuration: {} | {}", getClass ().getSimpleName (), clientConfig,
                           deniedEvent);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerDeniedEvent ()
  {
    clientConfig = null;
    deniedEvent = null;
  }
}
