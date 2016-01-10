package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public class PlayerOccupyCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final int deltaArmyCount;

  public PlayerOccupyCountryResponseRequestEvent (final int deltaArmyCount)
  {
    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.deltaArmyCount = deltaArmyCount;
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerOccupyCountryRequestEvent.class;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: DeltaArmyCount: {}", getClass ().getSimpleName (), deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryResponseRequestEvent ()
  {
    deltaArmyCount = 0;
  }
}
