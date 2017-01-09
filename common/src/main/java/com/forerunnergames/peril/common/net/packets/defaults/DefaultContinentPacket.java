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

package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.territory.AbstractTerritoryPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

import java.util.UUID;

public class DefaultContinentPacket extends AbstractTerritoryPacket implements ContinentPacket
{
  private final ImmutableSet <CountryPacket> countries;
  private final int reinforcementBonus;

  public DefaultContinentPacket (final String name,
                                 final UUID id,
                                 final int reinforcementBonus,
                                 final ImmutableSet <CountryPacket> countries)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

    this.countries = ImmutableSet.copyOf (countries);
    this.reinforcementBonus = reinforcementBonus;
  }

  @Override
  public ImmutableSet <CountryPacket> getCountries ()
  {
    return countries;
  }

  @Override
  public boolean hasCountry (final CountryPacket country)
  {
    Arguments.checkIsNotNull (country, "country");

    return countries.contains (country);
  }

  @Override
  public int getReinforcementBonus ()
  {
    return reinforcementBonus;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Reinforcement Bonus: {} | Countries: {}", super.toString (), reinforcementBonus,
                           countries);
  }

  @RequiredForNetworkSerialization
  private DefaultContinentPacket ()
  {
    countries = null;
    reinforcementBonus = 0;
  }

}
