/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.net;

import com.forerunnergames.peril.common.net.kryonet.AbstractKryonetRemote;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.remote.RemoteServer;
import com.forerunnergames.tools.net.server.configuration.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import java.net.InetSocketAddress;

import javax.annotation.Nullable;

public final class KryonetRemoteServer extends AbstractKryonetRemote implements RemoteServer
{
  private final ServerConfiguration config;

  public KryonetRemoteServer (final int connectionId, @Nullable final InetSocketAddress address)
  {
    super (connectionId, address);

    config = new DefaultServerConfiguration (getAddress (), getPort ());
  }

  @Override
  public ServerConfiguration getConfiguration ()
  {
    return config;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ServerConfiguration: [{}]", super.toString (), config);
  }
}
