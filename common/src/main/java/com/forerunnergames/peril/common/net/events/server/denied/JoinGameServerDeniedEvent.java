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

package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.client.interfaces.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;

public final class JoinGameServerDeniedEvent extends AbstractDeniedEvent <JoinGameServerRequestEvent, String>
{
  private final ClientConfiguration clientConfig;

  public JoinGameServerDeniedEvent (final ClientConfiguration clientConfig,
                                    final JoinGameServerRequestEvent deniedRequest,
                                    final String reason)
  {
    super (deniedRequest, reason);

    Arguments.checkIsNotNull (clientConfig, "clientConfig");

    this.clientConfig = clientConfig;
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
    return Strings.format ("{} | Client Configuration: {} | {}", super.toString (), clientConfig);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerDeniedEvent ()
  {
    clientConfig = null;
  }
}
