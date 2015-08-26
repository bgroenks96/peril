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
