package com.forerunnergames.peril.client.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class SelectCountryEvent implements LocalEvent
{
  private final String selectedCountryName;

  public SelectCountryEvent (final String selectedCountryName)
  {
    Arguments.checkIsNotNull (selectedCountryName, "selectedCountryName");

    this.selectedCountryName = selectedCountryName;
  }

  public String getSelectedCountryName ()
  {
    return selectedCountryName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Selected Country Name: {}", getClass ().getSimpleName (), selectedCountryName);
  }
}
