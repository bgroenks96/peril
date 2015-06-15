package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;

import com.beust.jcommander.Parameter;

public final class CommandLineArgs
{
  @Parameter (names = { "-m", "--game-mode" }, description = "Game mode", required = true,
          converter = GameModeParameterConverter.class, validateWith = GameModeParameterValidator.class)
  public GameMode gameMode;

  @Parameter (names = { "-s", "--server-type" }, description = "Type of server, host-and-play, or dedicated",
          required = true, converter = ServerTypeParameterConverter.class,
          validateWith = ServerTypeParameterValidator.class)
  public GameServerType gameServerType;

  @Parameter (names = { "-c", "--countries" },
          description = "Total number of playable countries present on the game map", required = true)
  public Integer totalCountryCount;

  @Parameter (names = { "-t", "--title" }, description = "Server title", required = true)
  public String gameServerName;

  @Parameter (names = { "-p", "--port" }, description = "TCP port number")
  public Integer serverTcpPort = NetworkSettings.DEFAULT_TCP_PORT;

  @Parameter (names = { "-pl", "--players" }, description = "Maximum number of players allowed")
  public Integer playerLimit;

  @Parameter (names = { "-w", "--win-percent" },
          description = "Minimum percentage of countries one must conquer to win")
  public Integer winPercentage;

  @Parameter (names = { "-a", "--assignment" }, description = "Initial Country Assignment Mode",
          converter = InitialCountryAssignmentParameterConverter.class,
          validateWith = InitialCountryAssignmentParameterValidator.class)
  public InitialCountryAssignment initialCountryAssignment;
}
