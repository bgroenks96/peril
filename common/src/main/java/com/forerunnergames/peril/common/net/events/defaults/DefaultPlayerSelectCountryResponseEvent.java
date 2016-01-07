package com.forerunnergames.peril.common.net.events.defaults;

import com.forerunnergames.peril.common.net.events.interfaces.PlayerSelectCountryResponseEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class DefaultPlayerSelectCountryResponseEvent implements PlayerSelectCountryResponseEvent
{
  private final String selectedCountryName;

  public DefaultPlayerSelectCountryResponseEvent (final String selectedCountryName)
  {
    Arguments.checkIsNotNull (selectedCountryName, "selectedCountryName");

    this.selectedCountryName = selectedCountryName;
  }

  @Override
  public String getSelectedCountryName ()
  {
    return selectedCountryName;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Selected Country Name: %2$s", getClass ().getSimpleName (), selectedCountryName);
  }

  @RequiredForNetworkSerialization
  protected DefaultPlayerSelectCountryResponseEvent ()
  {
    selectedCountryName = null;
  }
}
