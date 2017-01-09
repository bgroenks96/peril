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

package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

public final class CreateGameEvent implements Event
{
  private final String serverName;
  private final GameConfiguration gameConfig;
  private final String playerName;

  public CreateGameEvent (final String serverName, final GameConfiguration gameConfig, final String playerName)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (playerName, "playerName");

    this.serverName = serverName;
    this.gameConfig = gameConfig;
    this.playerName = playerName;
  }

  public String getServerName ()
  {
    return serverName;
  }

  public GameConfiguration getGameConfiguration ()
  {
    return gameConfig;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Server Name: {} | Game Configuration: {} | Player Name: {}",
                           getClass ().getSimpleName (), serverName, gameConfig, playerName);
  }
}
