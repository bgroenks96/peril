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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerOccupyCountryEvent extends AbstractPlayerSourceTargetCountryEvent
{
  private final int minOccupationArmyCount;
  private final int maxOccupationArmyCount;

  public AbstractPlayerOccupyCountryEvent (final PlayerPacket player,
                                           final CountryPacket sourceCountry,
                                           final CountryPacket targetCountry,
                                           final int minOccupationArmyCount,
                                           final int maxOccupationArmyCount)
  {
    super (player, sourceCountry, targetCountry);

    Arguments.checkIsNotNegative (minOccupationArmyCount, "minOccupationArmyCount");
    Arguments.checkIsNotNegative (maxOccupationArmyCount, "maxOccupationArmyCount");

    this.minOccupationArmyCount = minOccupationArmyCount;
    this.maxOccupationArmyCount = maxOccupationArmyCount;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerOccupyCountryEvent ()
  {
    minOccupationArmyCount = 0;
    maxOccupationArmyCount = 0;
  }

  public final int getTotalArmyCount ()
  {
    return getSourceCountryArmyCount ();
  }

  public final int getMinOccupationArmyCount ()
  {
    return minOccupationArmyCount;
  }

  public final int getMaxOccupationArmyCount ()
  {
    return maxOccupationArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | MinOccupationArmyCount: [{}] | MaxOccupationArmyCount: [{}] | TotalArmyCount: [{}]",
                           super.toString (), minOccupationArmyCount, maxOccupationArmyCount, getTotalArmyCount ());
  }
}
