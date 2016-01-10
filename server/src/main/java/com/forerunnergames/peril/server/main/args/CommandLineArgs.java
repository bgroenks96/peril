package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.Parameter;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
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

  @Parameter (names = { "--map-name", "-m" }, description = "Map name", validateWith = MapNameParameterValidator.class,
              required = true)
  public String mapName;

  @Parameter (names = { "--players", "-pl" }, description = "Maximum number of players allowed", required = true)
  public Integer playerLimit;

  @Parameter (names = { "--port", "-p", }, description = "TCP port number", required = false)
  public Integer serverTcpPort = NetworkSettings.DEFAULT_TCP_PORT;

  @Parameter (names = { "--win-percent", "-w" },
              description = "Minimum percentage of countries one must conquer to win the game", required = false)
  public Integer winPercentage = 100;

  @Parameter (names = { "--assignment", "-a" }, description = "Initial country assignment",
              converter = InitialCountryAssignmentParameterConverter.class,
              validateWith = InitialCountryAssignmentParameterValidator.class, required = false)
  public InitialCountryAssignment initialCountryAssignment = InitialCountryAssignment.RANDOM;

  @Parameter (names = { "--help" }, help = true, description = "Show usage", required = false)
  public boolean help = false;
}
