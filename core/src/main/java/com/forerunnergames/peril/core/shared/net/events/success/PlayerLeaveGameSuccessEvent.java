package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

public final class PlayerLeaveGameSuccessEvent implements SuccessEvent
{
  private final String playerName;

  public PlayerLeaveGameSuccessEvent (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    this.playerName = playerName;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", ((Object) this).getClass ().getSimpleName (), playerName);
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameSuccessEvent ()
  {
    playerName = null;
  }
}
