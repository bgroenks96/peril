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

package com.forerunnergames.peril.ai.net;

import com.forerunnergames.tools.net.NullRemote;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

/**
 * Fake {@link RemoteClient} that uses the specified player name for a fake address.
 *
 * Although {@link #hasAddress()} & {@link #hasAddressAndPort()} will return false, {@link #getAddress()} will return
 * the fake address, i.e., the specified player name.
 */
public final class AiClient extends NullRemote implements RemoteClient
{
  private final ClientConfiguration config;

  public AiClient (final String playerName)
  {
    super (playerName);

    config = new AiClientConfiguration (getAddress (), getPort ());
  }

  @Override
  public ClientConfiguration getConfiguration ()
  {
    return config;
  }

  public String getPlayerName ()
  {
    return getAddress ();
  }
}
