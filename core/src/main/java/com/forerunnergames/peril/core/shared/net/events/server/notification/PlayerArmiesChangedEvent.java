package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.shared.net.events.server.defaults.AbstractArmiesChangedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerArmiesChangedEvent extends AbstractArmiesChangedEvent
{
  private final String playerName;

  public PlayerArmiesChangedEvent (final String playerName, @AllowNegative final int deltaArmyCount)
  {
    super (deltaArmyCount);

    Arguments.checkIsNotNull (playerName, "playerName");

    this.playerName = playerName;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  @RequiredForNetworkSerialization
  private PlayerArmiesChangedEvent ()
  {
    super (0);
    playerName = null;
  }
}
