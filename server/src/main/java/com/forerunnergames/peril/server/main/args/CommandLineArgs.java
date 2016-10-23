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

package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.Parameter;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;

public final class CommandLineArgs
{
  @Parameter (names = { "--game-mode", "-g", }, description = "Game mode", required = true,
              converter = GameModeParameterConverter.class, validateWith = GameModeParameterValidator.class)
  public GameMode gameMode;

  @Parameter (names = { "--server-type", "-s" }, description = "Type of server", required = true,
              converter = ServerTypeParameterConverter.class, validateWith = ServerTypeParameterValidator.class)
  public GameServerType gameServerType;

  @Parameter (names = { "--title", "-t" }, description = "Server title", required = true,
              validateWith = ServerTitleParameterValidator.class)
  public String gameServerName;

  @Parameter (names = { "--map-name", "-m" }, description = "Map name",
              validateWith = PlayMapNameParameterValidator.class, required = true)
  public String playMapName;

  @Parameter (names = { "--players", "-pl" }, description = "Maximum number of players allowed", required = true)
  public Integer playerLimit;

  @Parameter (names = { "--spectators", "-sp" }, description = "Maximum number of spectators allowed", required = false)
  public Integer spectatorLimit = GameSettings.DEFAULT_SPECTATOR_LIMIT;

  @Parameter (names = { "--port", "-p", }, description = "TCP port number", required = false)
  public Integer serverTcpPort = NetworkSettings.DEFAULT_TCP_PORT;

  @Parameter (names = { "--win-percent", "-w" },
              description = "Minimum percentage of countries one must conquer to win the game", required = false)
  public Integer winPercentage = 100;

  @Parameter (names = { "--assignment", "-a" }, description = "Initial country assignment",
              converter = InitialCountryAssignmentParameterConverter.class,
              validateWith = InitialCountryAssignmentParameterValidator.class, required = false)
  public InitialCountryAssignment initialCountryAssignment = InitialCountryAssignment.RANDOM;

  @Parameter (names = "--help", help = true, description = "Show usage", required = false)
  public boolean help = false;
}
