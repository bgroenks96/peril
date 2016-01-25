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

package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.common.net.packets.defaults.DefaultContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;

final class ContinentPackets
{
  static ContinentPacket from (final Continent continent, final ImmutableSet <CountryPacket> countries)
  {
    Arguments.checkIsNotNull (continent, "country");

    return new DefaultContinentPacket (continent.getName (), continent.getId ().value (),
            continent.getReinforcementBonus (), countries);
  }

  private ContinentPackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
