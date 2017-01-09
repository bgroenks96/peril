/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface CountryArmyModel
{
  MutatorResult <Reason> requestToAddArmiesToCountry (final Id countryId, final int armyCount);

  MutatorResult <Reason> requestToRemoveArmiesFromCountry (final Id countryId, final int armyCount);

  int getArmyCountFor (final Id countryId);

  boolean armyCountIs (final int armyCount, final Id countryId);

  boolean armyCountIsAtLeast (final int armyCount, final Id countryId);

  void resetAllCountries ();

  ImmutableSet <CountryArmiesMutation> resetCountries (final ImmutableSet <Id> countryIds);

  final class CountryArmiesMutation
  {
    private final CountryPacket country;
    @AllowNegative
    private final int deltaArmies;

    CountryArmiesMutation (final CountryPacket country, @AllowNegative final int deltaArmies)
    {
      Arguments.checkIsNotNull (country, "country");

      this.country = country;
      this.deltaArmies = deltaArmies;
    }

    @Override
    public int hashCode ()
    {
      return country.hashCode ();
    }

    @Override
    public boolean equals (final Object o)
    {
      if (this == o) return true;
      if (o == null || getClass () != o.getClass ()) return false;

      final CountryArmiesMutation mutation = (CountryArmiesMutation) o;

      return country.equals (mutation.country);
    }

    @Override
    public String toString ()
    {
      return Strings.format ("{}: Country: [{}] | DeltaArmies: [{}]", getClass ().getSimpleName (), country,
                             deltaArmies);
    }

    public int getDeltaArmies ()
    {
      return deltaArmies;
    }

    public CountryPacket getCountry ()
    {
      return country;
    }
  }
}
