package com.forerunnergames.peril.common.net;

import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public interface GameServerConfiguration extends GameConfiguration, ServerConfiguration
{
  String getGameServerName ();

  GameServerType getGameServerType ();
}
