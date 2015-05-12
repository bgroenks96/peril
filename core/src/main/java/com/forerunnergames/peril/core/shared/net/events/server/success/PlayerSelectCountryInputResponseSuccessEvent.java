package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryInputResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.InputResponseSuccessEvent;

public final class PlayerSelectCountryInputResponseSuccessEvent extends DefaultPlayerSelectCountryInputResponseEvent
        implements InputResponseSuccessEvent
{

  public PlayerSelectCountryInputResponseSuccessEvent (String selectedCountryName)
  {
    super (selectedCountryName);
  }

  private PlayerSelectCountryInputResponseSuccessEvent ()
  {
    super (null);
  }
}
