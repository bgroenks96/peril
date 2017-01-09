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

package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.Parameter;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.net.GameServerType;
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

  @Parameter (names = { "--human-players", "-h" }, description = "Maximum number of human players allowed",
              validateWith = HumanPlayersParameterValidator.class, required = true)
  public Integer humanPlayers;

  @Parameter (names = { "--ai-players", "-ai" }, description = "Maximum number of AI players allowed",
              validateWith = AiPlayersParameterValidator.class)
  public Integer aiPlayers = ClassicGameRules.MIN_AI_PLAYERS;

  @Parameter (names = { "--spectators", "-sp" }, description = "Maximum number of spectators allowed",
              validateWith = SpectatorsParameterValidator.class)
  public Integer spectators = ClassicGameRules.MIN_SPECTATORS;

  @Parameter (names = { "--port", "-p", }, description = "TCP port number")
  public Integer serverTcpPort = NetworkSettings.DEFAULT_TCP_PORT;

  @Parameter (names = { "--win-percent", "-w" },
              description = "Minimum percentage of countries one must conquer to win the game")
  public Integer winPercentage = 100;

  @Parameter (names = { "--assignment", "-a" }, description = "Initial country assignment",
              converter = InitialCountryAssignmentParameterConverter.class,
              validateWith = InitialCountryAssignmentParameterValidator.class)
  public InitialCountryAssignment initialCountryAssignment = InitialCountryAssignment.RANDOM;

  @Parameter (names = "--help", help = true, description = "Show usage")
  public boolean help = false;
}
