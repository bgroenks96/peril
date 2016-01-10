package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public class AbstractArmiesChangedEvent implements ServerNotificationEvent
{
  private final int deltaArmyCount;

  /**
   * @param deltaArmyCount
   *          army change delta value; negative values are ALLOWED
   */
  protected AbstractArmiesChangedEvent (@AllowNegative final int deltaArmyCount)
  {
    this.deltaArmyCount = deltaArmyCount;
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: DeltaArmyCount: {}", getClass ().getSimpleName (), deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  protected AbstractArmiesChangedEvent ()
  {
    deltaArmyCount = 0;
  }
}
