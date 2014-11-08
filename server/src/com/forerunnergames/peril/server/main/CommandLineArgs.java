package com.forerunnergames.peril.server.main;

import com.beust.jcommander.Parameter;

public final class CommandLineArgs
{
  @Parameter (names = { "-title" }, description = "Server title", required = true)
  public String title;

  @Parameter (names = { "-port" }, description = "TCP port number")
  public int tcpPort = NetworkSettings.DEFAULT_TCP_PORT;

  @Parameter (names = { "-players" }, description = "Maximum number of players allowed")
  public int playerLimit = GameSettings.MIN_PLAYERS;
}
