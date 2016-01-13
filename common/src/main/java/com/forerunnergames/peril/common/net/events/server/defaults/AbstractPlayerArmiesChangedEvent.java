package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public abstract class AbstractPlayerArmiesChangedEvent extends AbstractPlayerEvent
        implements PlayerArmiesChangedEvent, ServerNotificationEvent
{
  private final int deltaArmyCount;

  /**
   * @param deltaArmyCount
   *          army change delta value; negative values are ALLOWED
   */
  protected AbstractPlayerArmiesChangedEvent (final PlayerPacket player, @AllowNegative final int deltaArmyCount)
  {
    super (player);

    this.deltaArmyCount = deltaArmyCount;
  }

  @Override
  public int getPlayerDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DeltaArmyCount: {}", super.toString (), deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerArmiesChangedEvent ()
  {
    deltaArmyCount = 0;
  }
}
