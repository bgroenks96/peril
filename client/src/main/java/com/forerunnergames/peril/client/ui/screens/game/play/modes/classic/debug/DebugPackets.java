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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug;

import com.forerunnergames.peril.common.net.packets.defaults.DefaultCountryPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public final class DebugPackets
{
  private static final Map <String, UUID> packetIdCache = Maps.newHashMap ();

  public static CountryPacket from (final String countryName, final int armyCount)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    return new DefaultCountryPacket (idFor (countryName), countryName, armyCount);
  }

  public static CountryPacket from (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return from (countryName, 0);
  }

  private static UUID idFor (final String str)
  {
    final boolean existsInCache = packetIdCache.containsKey (str);
    final UUID uuid = existsInCache ? packetIdCache.get (str) : UUID.randomUUID ();
    if (!existsInCache) packetIdCache.put (str, uuid);
    return uuid;
  }

  private DebugPackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
