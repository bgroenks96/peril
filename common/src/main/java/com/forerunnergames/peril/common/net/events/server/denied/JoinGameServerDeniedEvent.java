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

import com.forerunnergames.peril.common.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
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
    return Strings
            .format ("{}: Client Configuration: {} | {}", getClass ().getSimpleName (), clientConfig, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerDeniedEvent ()
  {
    clientConfig = null;
    deniedEvent = null;
  }
}
