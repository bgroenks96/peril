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

package com.forerunnergames.peril.core.model.map.country;

import static org.junit.Assert.assertEquals;

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

public class CountryPacketTest
{
  private static final int SAMPLE_SIZE_MANY = 1000000;

  @Test
  public void testTwoCommonCountryCountryPacketsAreEqual ()
  {
    final Country country = CountryFactory.builder ("Test Country").build ();

    final CountryPacket packet0 = CountryPackets.from (country);
    final CountryPacket packet1 = CountryPackets.from (country);

    assertEquals (packet0, packet1);
  }

  @Test
  public void testManyDifferentCountryCountryPacketsAreNotEqual ()
  {
    final ImmutableMap.Builder <CountryPacket, Country> mapBuilder = ImmutableMap.builder ();
    final int n = SAMPLE_SIZE_MANY;

    for (int i = 0; i < n; ++i)
    {
      final Country country = CountryFactory.builder ("Country-" + i).build ();
      final CountryPacket packet = CountryPackets.from (country);

      mapBuilder.put (packet, country);
    }

    // will fail if duplicates exist
    mapBuilder.build ();
  }
}
