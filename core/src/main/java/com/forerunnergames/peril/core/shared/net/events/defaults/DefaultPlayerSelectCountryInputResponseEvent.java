package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerSelectCountryInputResponseEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class DefaultPlayerSelectCountryInputResponseEvent implements PlayerSelectCountryInputResponseEvent
{
  private final String selectedCountryName;

  public DefaultPlayerSelectCountryInputResponseEvent (final String selectedCountryName)
  {
    Arguments.checkIsNotNull (selectedCountryName, "selectedCountryName");

    this.selectedCountryName = selectedCountryName;
  }

  @Override
  public String getSelectedCountryName ()
  {
    return selectedCountryName;
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerSelectCountryInputResponseEvent ()
  {
    this.selectedCountryName = null;
  }
}
