package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultCreateGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CreateGameServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.RequestEvent;

public final class CreateGameServerRequestEvent implements CreateGameServerEvent, RequestEvent
{
  private final CreateGameServerEvent createGameServerEvent;

  public CreateGameServerRequestEvent (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    createGameServerEvent = new DefaultCreateGameServerEvent (config);
  }

  @Override
  public GameServerConfiguration getConfiguration ()
  {
    return createGameServerEvent.getConfiguration ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", ((Object) this).getClass ().getSimpleName (), createGameServerEvent);
  }

  @RequiredForNetworkSerialization
  private CreateGameServerRequestEvent ()
  {
    createGameServerEvent = null;
  }
}
