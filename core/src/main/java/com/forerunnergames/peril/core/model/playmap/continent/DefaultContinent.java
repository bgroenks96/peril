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

package com.forerunnergames.peril.core.model.playmap.continent;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class DefaultContinent extends AbstractAsset implements Continent
{
  private final ImmutableSet <Id> countryIds;
  private final int reinforcementBonus;

  public DefaultContinent (final String name,
                           final Id id,
                           final int reinforcementBonus,
                           final ImmutableSet <Id> countryIds)
  {
    super (name, id);

    Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
    Arguments.checkIsNotNull (countryIds, "countryIds");
    Arguments.checkHasNoNullElements (countryIds, "countryIds");

    this.countryIds = countryIds;
    this.reinforcementBonus = reinforcementBonus;
  }

  @Override
  public ImmutableSet <Id> getCountryIds ()
  {
    return countryIds;
  }

  @Override
  public int getCountryCount ()
  {
    return countryIds.size ();
  }

  @Override
  public boolean hasCountry (final Id country)
  {
    Arguments.checkIsNotNull (country, "country");

    return countryIds.contains (country);
  }

  @Override
  public int getReinforcementBonus ()
  {
    return reinforcementBonus;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Reinforcement Bonus: {} | Country Count: {} | Country Id's: {}", super.toString (),
                           reinforcementBonus, countryIds.size (), countryIds);
  }

  @RequiredForNetworkSerialization
  private DefaultContinent ()
  {
    countryIds = null;
    reinforcementBonus = 0;
  }
}
