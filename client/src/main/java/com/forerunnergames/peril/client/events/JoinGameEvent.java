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

package com.forerunnergames.peril.client.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

public final class JoinGameEvent implements Event
{
  private final String playerName;
  private final String serverAddress;

  public JoinGameEvent (final String playerName, final String serverAddress)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");

    this.playerName = playerName;
    this.serverAddress = serverAddress;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  public String getServerAddress ()
  {
    return serverAddress;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player Name: {} | Server Address: {}", getClass ().getSimpleName (), playerName,
                           serverAddress);
  }
}
