package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.net.packets.defaults.DefaultCountryPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Collection;

final class CountryPackets
{
  static CountryPacket from (final Country country)
  {
    Arguments.checkIsNotNull (country, "country");

    return new DefaultCountryPacket (country.getId ().value (), country.getName (), country.getArmyCount ());
  }

  static ImmutableSet <CountryPacket> fromCountries (final Collection <Country> countries)
  {
    Arguments.checkIsNotNull (countries, "countries");

    Arguments.checkIsNotNull (countries, "players");
    Arguments.checkHasNoNullElements (countries, "players");

    final Builder <CountryPacket> packetSetBuilder = ImmutableSet.builder ();
    for (final Country country : countries)
    {
      packetSetBuilder.add (from (country));
    }
    return packetSetBuilder.build ();
  }

  static boolean countryMatchesPacket (final Country country, final CountryPacket countryPacket)
  {
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkIsNotNull (countryPacket, "countryPacket");

    return from (country).is (countryPacket);
  }

  private CountryPackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
