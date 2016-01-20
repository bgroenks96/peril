package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceInitialCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public final class PlayerReinforceInitialCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final String countryName;
  private final int reinforcementCount;

  public PlayerReinforceInitialCountryResponseRequestEvent (final String countryName, final int reinforcementCount)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (reinforcementCount, "reinforcementCount");

    this.countryName = countryName;
    this.reinforcementCount = reinforcementCount;
  }

  public String getCountryName ()
  {
    return countryName;
  }

  public int getReinforcementCount ()
  {
    return reinforcementCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: CountryName: {} | ReinforcementCount: {}", getClass ().getSimpleName (), countryName,
                           reinforcementCount);
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerReinforceInitialCountryRequestEvent.class;
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceInitialCountryResponseRequestEvent ()
  {
    countryName = null;
    reinforcementCount = 0;
  }
}
