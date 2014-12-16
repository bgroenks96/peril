package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.AbstractDeniedEvent;
import com.forerunnergames.tools.common.Arguments;

public final class PlayerJoinGameDeniedEvent extends AbstractDeniedEvent <PlayerJoinGameDeniedEvent.REASON>
{
  public enum REASON
  {
    GAME_IS_FULL,
    DUPLICATE_SELF_IDENTITY,
    DUPLICATE_ID,
    DUPLICATE_NAME,
    DUPLICATE_COLOR,
    DUPLICATE_TURN_ORDER
  }

  private final String playerName;

  public PlayerJoinGameDeniedEvent (final String playerName, final PlayerJoinGameDeniedEvent.REASON reason)
  {
    super (reason);

    Arguments.checkIsNotNull (playerName, "playerName");

    this.playerName = playerName;
  }

  public String getPlayerName()
  {
    return playerName;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Player name: %2$s | %3$s", getClass().getSimpleName(), playerName, super.toString());
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameDeniedEvent()
  {
    playerName = null;
  }
}
