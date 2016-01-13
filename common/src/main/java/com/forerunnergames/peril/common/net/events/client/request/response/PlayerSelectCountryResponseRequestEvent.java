package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public final class PlayerSelectCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final String selectedCountryName;

  public PlayerSelectCountryResponseRequestEvent (final String selectedCountryName)
  {
    Arguments.checkIsNotNull (selectedCountryName, "selectedCountryName");

    this.selectedCountryName = selectedCountryName;
  }

  public String getSelectedCountryName ()
  {
    return selectedCountryName;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerSelectCountryRequestEvent.class;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: SelectedCountry: {}", getClass ().getSimpleName (), selectedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseRequestEvent ()
  {
    selectedCountryName = null;
  }
}
