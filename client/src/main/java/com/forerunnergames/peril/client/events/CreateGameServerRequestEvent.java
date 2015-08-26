package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameServerRequestEvent implements LocalEvent
{
  private final GameServerConfiguration config;

  public CreateGameServerRequestEvent (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    this.config = config;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return config;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Game Server Configuration: {}", getClass ().getSimpleName (), config);
  }
}
