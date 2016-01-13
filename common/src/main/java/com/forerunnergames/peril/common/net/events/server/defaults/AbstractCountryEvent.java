package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractCountryEvent implements CountryEvent
{
  private final CountryPacket country;

  public AbstractCountryEvent (final CountryPacket country)
  {
    Arguments.checkIsNotNull (country, "country");

    this.country = country;
  }

  @Override
  public CountryPacket getCountry ()
  {
    return country;
  }

  @Override
  public String getCountryName ()
  {
    return country.getName ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Country: [{}]", getClass ().getSimpleName (), country);
  }

  @RequiredForNetworkSerialization
  protected AbstractCountryEvent ()
  {
    country = null;
  }
}
