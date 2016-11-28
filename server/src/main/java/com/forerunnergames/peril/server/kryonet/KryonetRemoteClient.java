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

package com.forerunnergames.peril.server.kryonet;

import com.forerunnergames.peril.common.net.kryonet.AbstractKryonetRemote;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.client.configuration.DefaultClientConfiguration;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

import java.net.InetSocketAddress;

import javax.annotation.Nullable;

public final class KryonetRemoteClient extends AbstractKryonetRemote implements RemoteClient
{
  private final ClientConfiguration config;

  public KryonetRemoteClient (final int connectionId, @Nullable final InetSocketAddress address)
  {
    super (connectionId, address);

    config = new DefaultClientConfiguration (getAddress (), getPort ());
  }

  @Override
  public ClientConfiguration getConfiguration ()
  {
    return config;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ClientConfiguration: [{}]", super.toString (), config);
  }
}
