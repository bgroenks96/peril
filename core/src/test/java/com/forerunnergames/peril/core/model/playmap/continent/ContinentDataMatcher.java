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

import com.google.common.collect.ImmutableSet;

public final class ContinentDataMatcher
{
  private final ImmutableSet <ContinentData> continentData;

  public ContinentDataMatcher (final ImmutableSet <ContinentData> continentData)
  {
    Arguments.checkIsNotNull (continentData, "continentData");

    this.continentData = continentData;
  }

  public boolean continentDataMatch (final ContinentFactory factory)
  {
    Arguments.checkIsNotNull (factory, "factory");

    final ImmutableSet <Continent> continents = factory.getContinents ();

    if (continents.size () != continentData.size ()) return false;

    for (final Continent continent : continents)
    {
      boolean isMatch = false;
      for (final ContinentData matchData : continentData)
      {
        if (!matchData.name.equals (continent.getName ())) continue;
        if (matchData.reinforcementBonus != continent.getReinforcementBonus ()) continue;
        if (matchData.countryCount != continent.getCountryCount ()) continue;
        isMatch = true;
        break;
      }
      if (isMatch) return true;
    }

    return false;
  }

  public static class ContinentData
  {
    private final String name;
    private final int reinforcementBonus;
    private final int countryCount;

    public ContinentData (final String name, final int reinforcementBonus, final int countryCount)
    {
      Arguments.checkIsNotNull (name, "name");
      Arguments.checkIsNotNegative (reinforcementBonus, "reinforcementBonus");
      Arguments.checkIsNotNegative (countryCount, "countryCount");

      this.name = name;
      this.reinforcementBonus = reinforcementBonus;
      this.countryCount = countryCount;
    }
  }
}
