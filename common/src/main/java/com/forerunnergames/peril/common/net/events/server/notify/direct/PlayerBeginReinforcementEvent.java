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

package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.common.net.packets.territory.TerritoryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public final class PlayerBeginReinforcementEvent extends AbstractPlayerEvent implements PlayerInformEvent
{
  private final ImmutableMap <String, Integer> reinforceableCountryNamesToCountryArmyCounts;
  private final ImmutableSet <CountryPacket> reinforceableCountries;
  private final int minReinforcementsPlacedPerCountry;
  private final int maxArmiesPerCountry;

  public PlayerBeginReinforcementEvent (final PlayerPacket player,
                                        final ImmutableSet <CountryPacket> reinforceableCountries,
                                        final int minReinforcementsPlacedPerCountry,
                                        final int maxArmiesPerCountry)
  {
    super (player);

    Arguments.checkIsNotNull (reinforceableCountries, "reinforceableCountries");
    Arguments.checkIsNotNegative (maxArmiesPerCountry, "maxArmiesPerCountry");
    Arguments.checkIsNotNegative (minReinforcementsPlacedPerCountry, "minReinforcementsPlacedPerCountry");

    final ImmutableMap.Builder <String, Integer> builder = ImmutableMap.builder ();

    for (final CountryPacket country : reinforceableCountries)
    {
      builder.put (country.getName (), country.getArmyCount ());
    }

    reinforceableCountryNamesToCountryArmyCounts = builder.build ();

    this.reinforceableCountries = reinforceableCountries;
    this.minReinforcementsPlacedPerCountry = minReinforcementsPlacedPerCountry;
    this.maxArmiesPerCountry = maxArmiesPerCountry;
  }

  public int getTotalReinforcements ()
  {
    return getPlayerArmiesInHand ();
  }

  public int getMinReinforcementsPlacedPerCountry ()
  {
    return minReinforcementsPlacedPerCountry;
  }

  public int getMaxArmiesPerCountry ()
  {
    return maxArmiesPerCountry;
  }

  public ImmutableSet <CountryPacket> getReinforceableCountries ()
  {
    return reinforceableCountries;
  }

  public int getReinforceableCountryCount ()
  {
    return reinforceableCountries.size ();
  }

  public ImmutableSet <String> getReinforceableCountryNames ()
  {
    return ImmutableSet
            .copyOf (Lists.transform (reinforceableCountries.asList (), TerritoryPacket.GET_BY_NAME_FUNCTION));
  }

  public boolean isReinforceableCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return reinforceableCountryNamesToCountryArmyCounts.containsKey (countryName);
  }

  public boolean canReinforceCountryWithMinArmies (final String countryName)
  {
    return canReinforceCountryWithArmies (countryName, minReinforcementsPlacedPerCountry);
  }

  public boolean canReinforceCountryWithArmies (final String countryName, final int armies)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (armies, "armies");

    final Integer armyCount = reinforceableCountryNamesToCountryArmyCounts.get (countryName);

    return armyCount != null && armyCount + armies <= maxArmiesPerCountry && armies <= getPlayerArmiesInHand ();
  }

  public int getMaxReinforcementsToPlaceOn (final String reinforceableCountryName)
  {
    Arguments.checkIsNotNull (reinforceableCountryName, "reinforceableCountryName");

    final Integer countryArmyCount = reinforceableCountryNamesToCountryArmyCounts.get (reinforceableCountryName);

    if (countryArmyCount == null)
    {
      throw new IllegalStateException (
              Strings.format ("Cannot get armies on {} (not a reinforceable country.) Reinforceable countries: [{}]",
                              reinforceableCountryName, reinforceableCountries));
    }

    return Math.min (getTotalReinforcements (), maxArmiesPerCountry - countryArmyCount);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | MaxArmiesPerCountry: {} | MinReinforcementsPlacedPerCountry: [{}] | "
                                   + "ReinforceableCountries: [{}] | PlayerOwnedCountryNamesToCountryArmyCounts: [{}]",
                           super.toString (),
                           maxArmiesPerCountry, minReinforcementsPlacedPerCountry,
                           reinforceableCountryNamesToCountryArmyCounts, reinforceableCountryNamesToCountryArmyCounts);
  }

  @RequiredForNetworkSerialization
  private PlayerBeginReinforcementEvent ()
  {
    reinforceableCountryNamesToCountryArmyCounts = null;
    reinforceableCountries = null;
    minReinforcementsPlacedPerCountry = 0;
    maxArmiesPerCountry = 0;
  }
}
