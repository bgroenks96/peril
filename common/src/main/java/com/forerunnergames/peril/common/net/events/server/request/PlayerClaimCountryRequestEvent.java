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

public final class PlayerClaimCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final ImmutableSet <CountryPacket> unclaimedCountries;

  public PlayerClaimCountryRequestEvent (final PlayerPacket player,
                                         final ImmutableSet <CountryPacket> unclaimedCountries)
  {
    super (player);

    Arguments.checkIsNotNull (unclaimedCountries, "unclaimedCountries");

    this.unclaimedCountries = unclaimedCountries;
  }

  public ImmutableSet <CountryPacket> getUnclaimedCountries ()
  {
    return unclaimedCountries;
  }

  public boolean isUnclaimedCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    for (final CountryPacket country : unclaimedCountries)
    {
      if (country.hasName (countryName)) return true;
    }

    return false;
  }

  public boolean isClaimedCountry (final String countryName)
  {
    return !isUnclaimedCountry (countryName);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Unclaimed Countries: [{}]", super.toString (), unclaimedCountries);
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryRequestEvent ()
  {
    unclaimedCountries = null;
  }
}
