package com.forerunnergames.peril.core.shared.net.events.client.request.response;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryResponseEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

public final class PlayerSelectCountryResponseRequestEvent extends DefaultPlayerSelectCountryResponseEvent implements
        ResponseRequestEvent
{

  public PlayerSelectCountryResponseRequestEvent (final String selectedCountryName)
  {
    super (selectedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseRequestEvent ()
  {
    super (null);
  }
}
