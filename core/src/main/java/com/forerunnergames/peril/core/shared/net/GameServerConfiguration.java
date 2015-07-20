package com.forerunnergames.peril.core.shared.net;

import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public interface GameServerConfiguration extends GameConfiguration, ServerConfiguration
{
  String getGameServerName ();

  GameServerType getGameServerType ();
}
