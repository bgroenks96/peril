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

package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class BeginReinforcementPhaseEvent extends AbstractPlayerArmiesChangedEvent
{
  private final int countryReinforcementBonus;
  private final int continentReinforcementBonus;

  public BeginReinforcementPhaseEvent (final PlayerPacket currentPlayer,
                                       final int countryReinforcementBonus,
                                       final int continentReinforcementBonus)
  {
    super (currentPlayer, countryReinforcementBonus + continentReinforcementBonus);

    this.countryReinforcementBonus = countryReinforcementBonus;
    this.continentReinforcementBonus = continentReinforcementBonus;
  }

  public int getCountryReinforcementBonus ()
  {
    return this.countryReinforcementBonus;
  }

  public int getContinentReinforcementBonus ()
  {
    return this.continentReinforcementBonus;
  }

  @RequiredForNetworkSerialization
  private BeginReinforcementPhaseEvent ()
  {
    this.countryReinforcementBonus = 0;
    this.continentReinforcementBonus = 0;
  }
}
