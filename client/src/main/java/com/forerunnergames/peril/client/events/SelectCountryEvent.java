package com.forerunnergames.peril.client.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class SelectCountryEvent implements LocalEvent
{
  private final String countryName;

  public SelectCountryEvent (final String countryName)
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
    return Strings.format ("{}: Country Name: {}", getClass ().getSimpleName (), countryName);
  }
}
