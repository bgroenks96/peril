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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public abstract class AbstractCountryArmiesChangedEvent extends AbstractCountryEvent
        implements CountryArmiesChangedEvent, ServerNotificationEvent
{
  private final int deltaArmyCount;

  /**
   * @param deltaArmyCount
   *          army change delta value; negative values are ALLOWED
   */
  protected AbstractCountryArmiesChangedEvent (final CountryPacket country, @AllowNegative final int deltaArmyCount)
  {
    super (country);

    this.deltaArmyCount = deltaArmyCount;
  }

  @Override
  public int getCountryDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DeltaArmyCount: {}", super.toString (), deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  protected AbstractCountryArmiesChangedEvent ()
  {
    deltaArmyCount = 0;
  }
}
