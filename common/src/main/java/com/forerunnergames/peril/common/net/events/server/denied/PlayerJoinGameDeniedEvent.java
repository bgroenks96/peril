package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerJoinGameDeniedEvent extends AbstractDeniedEvent <PlayerJoinGameDeniedEvent.Reason>
{
  private final String playerName;

  public PlayerJoinGameDeniedEvent (final String playerName, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (playerName, "playerName");

    this.playerName = playerName;
  }

  public enum Reason
  {
    GAME_IS_FULL,
    DUPLICATE_NAME,
    DUPLICATE_COLOR,
    DUPLICATE_TURN_ORDER,
    INVALID_NAME
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | PlayerName: {}", super.toString (), playerName);
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameDeniedEvent ()
  {
    playerName = null;
  }
}
