package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryInputResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.InputResponseSuccessEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryInputResponseSuccessEvent extends DefaultPlayerSelectCountryInputResponseEvent
        implements InputResponseSuccessEvent
{
  public PlayerSelectCountryInputResponseSuccessEvent (final String selectedCountryName)
  {
    super (selectedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryInputResponseSuccessEvent ()
  {
    super (null);
  }
}
