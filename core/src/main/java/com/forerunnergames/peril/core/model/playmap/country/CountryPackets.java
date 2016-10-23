/*
 * Copyright © 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.core.model.playmap.country;

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
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

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
