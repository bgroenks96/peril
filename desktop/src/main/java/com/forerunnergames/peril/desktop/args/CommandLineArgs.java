package com.forerunnergames.peril.desktop.args;

import com.beust.jcommander.Parameter;

public final class CommandLineArgs
{
  @Parameter (names = { "--player-name", "-p", }, description = "Player Name", required = false,
              validateWith = PlayerNameParameterValidator.class)
  public String playerName = "";

  @Parameter (names = { "--clan-tag", "-c", }, description = "Clan Tag", required = false,
              validateWith = ClanTagParameterValidator.class)
  public String clanName = "";

  @Parameter (names = { "--help" }, help = true, description = "Show usage", required = false)
  public boolean help = false;
}
