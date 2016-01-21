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

  public PlayerReinforceInitialCountryResponseRequestEvent (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    this.countryName = countryName;
  }

  public String getCountryName ()
  {
    return countryName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: CountryName: {}", getClass ().getSimpleName (), countryName);
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
  }
}
