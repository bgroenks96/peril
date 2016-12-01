/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.DirectSuccessEvent;

public final class JoinGameServerSuccessEvent implements DirectSuccessEvent
{
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;

  public JoinGameServerSuccessEvent (final GameServerConfiguration gameServerConfig,
                                     final ClientConfiguration clientConfig)

  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");

    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return gameServerConfig;
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  public String getClientAddress ()
  {
    return clientConfig.getAddress ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Game Server Configuration: {} | Client Configuration: {}", getClass ().getSimpleName (),
                           gameServerConfig, clientConfig);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerSuccessEvent ()
  {
    gameServerConfig = null;
    clientConfig = null;
  }
}
