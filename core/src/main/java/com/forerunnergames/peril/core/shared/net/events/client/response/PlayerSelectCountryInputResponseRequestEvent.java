package com.forerunnergames.peril.core.shared.net.events.client.response;

import com.forerunnergames.peril.core.shared.net.events.client.interfaces.InputResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryInputResponseEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryInputResponseRequestEvent extends DefaultPlayerSelectCountryInputResponseEvent
        implements InputResponseRequestEvent
{

  public PlayerSelectCountryInputResponseRequestEvent (final String selectedCountryName)
  {
    super (selectedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryInputResponseRequestEvent ()
  {
    super (null);
  }
}
