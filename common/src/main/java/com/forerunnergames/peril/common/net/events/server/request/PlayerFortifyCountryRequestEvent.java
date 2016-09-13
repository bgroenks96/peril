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

package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerFortifyCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final CountryPacket sourceCountry;
  private final CountryPacket targetCountry;
  private final int maxDeltaArmyCount;

  public PlayerFortifyCountryRequestEvent (final PlayerPacket player,
                                           final CountryPacket sourceCountry,
                                           final CountryPacket targetCountry,
                                           final int maxDeltaArmyCount)
  {
    super (player);

    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkIsNotNegative (maxDeltaArmyCount, "maxDeltaArmyCount");

    this.sourceCountry = sourceCountry;
    this.targetCountry = targetCountry;
    this.maxDeltaArmyCount = maxDeltaArmyCount;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public CountryPacket getTargetCountry ()
  {
    return targetCountry;
  }

  public int getMaxDeltaArmyCount ()
  {
    return maxDeltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: [{}] | TargetCountry: [{}] | MaxDeltaArmyCount: {}", super.toString (),
                           sourceCountry, targetCountry, maxDeltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryRequestEvent ()
  {
    sourceCountry = null;
    targetCountry = null;
    maxDeltaArmyCount = 0;
  }
}
