package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueFortifyOrderEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerOrderFortifyRequestEvent implements InformRequestEvent
{
  private final int deltaArmyCount;

  public PlayerOrderFortifyRequestEvent (final int deltaArmyCount)
  {
    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.deltaArmyCount = deltaArmyCount;
  }

  @Override
  public Class <? extends PlayerInformEvent> getInformType ()
  {
    return PlayerIssueFortifyOrderEvent.class;
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
  private PlayerOrderFortifyRequestEvent ()
  {
    deltaArmyCount = 0;
  }
}
