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
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public final class DefaultCountryPacket extends AbstractTerritoryPacket implements CountryPacket
{
  private final int armyCount;

  public DefaultCountryPacket (final UUID countryId, final String name, final int armyCount)
  {
    super (name, countryId);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    this.armyCount = armyCount;
  }

  @Override
  public int getArmyCount ()
  {
    return armyCount;
  }

  @Override
  public boolean hasAnyArmies ()
  {
    return armyCount > 0;
  }

  @Override
  public boolean hasAtLeastNArmies (final int n)
  {
    Arguments.checkIsNotNegative (n, "n");

    return armyCount >= n;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Army Count: {}", super.toString (), armyCount);
  }

  @RequiredForNetworkSerialization
  private DefaultCountryPacket ()
  {
    armyCount = 0;
  }
}
