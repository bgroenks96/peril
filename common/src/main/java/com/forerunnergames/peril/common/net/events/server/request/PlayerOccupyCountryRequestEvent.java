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

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerOccupyCountryEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerOccupyCountryRequestEvent extends AbstractPlayerOccupyCountryEvent
        implements PlayerInputRequestEvent
{
  public PlayerOccupyCountryRequestEvent (final PlayerPacket player,
                                          final CountryPacket sourceCountry,
                                          final CountryPacket targetCountry,
                                          final int minOccupationArmyCount,
                                          final int maxOccupationArmyCount)
  {
    super (player, sourceCountry, targetCountry, minOccupationArmyCount, maxOccupationArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryRequestEvent ()
  {
  }
}
