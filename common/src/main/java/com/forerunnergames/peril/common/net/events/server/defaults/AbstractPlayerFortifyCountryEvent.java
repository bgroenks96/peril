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

public abstract class AbstractPlayerFortifyCountryEvent extends AbstractPlayerSourceTargetCountryEvent
{
  private final int minDeltaArmyCount;
  private final int maxDeltaArmyCount;

  public AbstractPlayerFortifyCountryEvent (final PlayerPacket player,
                                            final CountryPacket sourceCountry,
                                            final CountryPacket targetCountry,
                                            final int minDeltaArmyCount,
                                            final int maxDeltaArmyCount)
  {
    super (player, sourceCountry, targetCountry);

    Arguments.checkIsNotNegative (minDeltaArmyCount, "minDeltaArmyCount");
    Arguments.checkIsNotNegative (maxDeltaArmyCount, "maxDeltaArmyCount");

    this.maxDeltaArmyCount = maxDeltaArmyCount;
    this.minDeltaArmyCount = minDeltaArmyCount;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerFortifyCountryEvent ()
  {
    minDeltaArmyCount = 0;
    maxDeltaArmyCount = 0;
  }

  public final int getMinTargetCountryArmyCount ()
  {
    return getTargetCountryArmyCount () + minDeltaArmyCount;
  }

  public final int getMaxTargetCountryArmyCount ()
  {
    return getTargetCountryArmyCount () + maxDeltaArmyCount;
  }

  public final int getMinDeltaArmyCount ()
  {
    return minDeltaArmyCount;
  }

  public final int getMaxDeltaArmyCount ()
  {
    return maxDeltaArmyCount;
  }

  public final int getTotalArmyCount ()
  {
    return getSourceCountryArmyCount () + getTargetCountryArmyCount ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | MinDeltaArmyCount: {} | MaxDeltaArmyCount: {}", super.toString (), minDeltaArmyCount,
                           maxDeltaArmyCount);
  }
}
