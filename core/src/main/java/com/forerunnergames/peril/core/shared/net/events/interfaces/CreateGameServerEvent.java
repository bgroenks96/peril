package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Event;

public interface CreateGameServerEvent extends Event
{
  GameServerConfiguration getConfiguration ();
}
