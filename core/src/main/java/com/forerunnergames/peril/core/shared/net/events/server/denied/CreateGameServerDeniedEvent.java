package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.client.request.CreateGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CreateGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.DeniedEvent;

public final class CreateGameServerDeniedEvent implements CreateGameServerEvent, DeniedEvent <String>
{
  private final CreateGameServerRequestEvent requestEvent;
  private final DeniedEvent <String> deniedEvent;

  public CreateGameServerDeniedEvent (final CreateGameServerRequestEvent event, final String reason)
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
  public GameServerConfiguration getConfiguration ()
  {
    return requestEvent.getConfiguration ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Original request: %2$s | %3$s", getClass ().getSimpleName (), requestEvent,
                          deniedEvent);
  }

  @RequiredForNetworkSerialization
  private CreateGameServerDeniedEvent ()
  {
    requestEvent = null;
    deniedEvent = null;
  }
}
