package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultCreateGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CreateGameServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

public final class CreateGameServerSuccessEvent implements CreateGameServerEvent, SuccessEvent
{
  private final CreateGameServerEvent createGameServerEvent;

  public CreateGameServerSuccessEvent (final GameServerConfiguration config)
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
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), createGameServerEvent);
  }

  @RequiredForNetworkSerialization
  private CreateGameServerSuccessEvent ()
  {
    createGameServerEvent = null;
  }
}
