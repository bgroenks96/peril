/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerReinforceInitialCountryRequestEvent extends AbstractPlayerEvent
        implements PlayerInputRequestEvent
{
  private final ImmutableSet <CountryPacket> playerOwnedCountries;
  private final int preSetReinforcementCount;
  private final int maxArmiesPerCountry;

  public PlayerReinforceInitialCountryRequestEvent (final PlayerPacket player,
                                                    final ImmutableSet <CountryPacket> playerOwnedCountries,
                                                    final int preSetReinforcementCount,
                                                    final int maxArmiesPerCountry)
  {
    super (player);

    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNegative (preSetReinforcementCount, "preSetReinforcementCount");
    Arguments.checkIsNotNegative (maxArmiesPerCountry, "maxArmiesPerCountry");

    this.playerOwnedCountries = playerOwnedCountries;
    this.preSetReinforcementCount = preSetReinforcementCount;
    this.maxArmiesPerCountry = maxArmiesPerCountry;
  }

  public ImmutableSet <CountryPacket> getPlayerOwnedCountries ()
  {
    return playerOwnedCountries;
  }

  public int getPreSetReinforcementCount ()
  {
    return preSetReinforcementCount;
  }

  public boolean isPlayerOwnedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    for (final CountryPacket country : playerOwnedCountries)
    {
      if (country.hasName (countryName)) return true;
    }

    return false;
  }

  public boolean isNotPlayerOwnedCountry (final String countryName)
  {
    return !isPlayerOwnedCountry (countryName);
  }

  public boolean canAddArmiesToCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    for (final CountryPacket country : playerOwnedCountries)
    {
      if (country.hasName (countryName)) return country.getArmyCount () < maxArmiesPerCountry;
    }

    return false;
  }

  public int getTotalReinforcements ()
  {
    return getPlayer ().getArmiesInHand ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | PresetReinforcementCount: {} | MaxArmiesPerCountry: {} | OwnedCountries: [{}]",
                           super.toString (), preSetReinforcementCount, maxArmiesPerCountry, playerOwnedCountries);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceInitialCountryRequestEvent ()
  {
    playerOwnedCountries = null;
    preSetReinforcementCount = 0;
    maxArmiesPerCountry = 0;
  }
}
