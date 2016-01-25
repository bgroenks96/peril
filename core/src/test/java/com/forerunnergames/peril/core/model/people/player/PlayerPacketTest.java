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

package com.forerunnergames.peril.core.model.people.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class PlayerPacketTest
{
  private static final int SAMPLE_SIZE_MANY = 1000000;
  private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

  @Test
  public void testTwoCommonPlayerPacketsAreEqual ()
  {
    final Player player = PlayerFactory.create ("TestPlayer-1");

    final PlayerPacket packet0 = PlayerPackets.from (player);
    final PlayerPacket packet1 = PlayerPackets.from (player);

    assertEquals (packet0, packet1);
  }

  @Test
  public void testTwoCommonPlayerPacketsInHashSet ()
  {
    final Player player = PlayerFactory.create ("TestPlayer-1");

    final PlayerPacket packet0 = PlayerPackets.from (player);
    final PlayerPacket packet1 = PlayerPackets.from (player);

    final Set <PlayerPacket> set = Sets.newHashSet (packet0);

    assertTrue (set.contains (packet0));
    assertTrue (set.contains (packet1));
  }

  @Test
  public void testTwoCommonPlayerPacketsDifferentAttribsAreEqual ()
  {
    final Player player = PlayerFactory.create ("TestPlayer-1");

    final PlayerPacket packet0 = PlayerPackets.from (player);
    player.setTurnOrder (PlayerTurnOrder.TENTH);
    final PlayerPacket packet1 = PlayerPackets.from (player);

    assertEquals (packet0, packet1);
  }

  @Test
  public void testTwoCommonPlayerPacketsDifferentAttribsInHashSet ()
  {
    final Player player = PlayerFactory.create ("TestPlayer-1");

    final PlayerPacket packet0 = PlayerPackets.from (player);
    player.setTurnOrder (PlayerTurnOrder.TENTH);
    final PlayerPacket packet1 = PlayerPackets.from (player);

    final Set <PlayerPacket> set = Sets.newHashSet (packet0);

    assertTrue (set.contains (packet0));
    assertTrue (set.contains (packet1));
  }

  @Test
  public void testTwoDifferentPlayerPacketsAreNotEqual ()
  {
    final Player player0 = PlayerFactory.builder ("TestPlayer-0").build ();
    final Player player1 = PlayerFactory.builder ("TestPlayer-1").build ();

    final PlayerPacket packet0 = PlayerPackets.from (player0);
    final PlayerPacket packet1 = PlayerPackets.from (player1);

    assertNotEquals (packet0, packet1);
  }

  @Test
  public void testManyDifferentPlayerPacketsAreNotEqual ()
  {
    final ImmutableMap.Builder <PlayerPacket, Player> mapBuilder = ImmutableMap.builder ();
    final int n = SAMPLE_SIZE_MANY;

    for (int i = 0; i < n; ++i)
    {
      final Player player = PlayerFactory.builder ("Player" + i).build ();
      final PlayerPacket packet = PlayerPackets.from (player);

      mapBuilder.put (packet, player);
    }

    // will fail if duplicates exist
    mapBuilder.build ();
  }

  @Test
  public void testManyDifferentPlayerPacketsAreNotEqualAndPseudoRandomNames ()
  {
    final ImmutableMap.Builder <PlayerPacket, Player> mapBuilder = ImmutableMap.builder ();
    final int n = SAMPLE_SIZE_MANY / 2;

    for (int i = 0; i < n; ++i)
    {
      final int strLen = 3 + i % 20;
      final Player player = PlayerFactory.builder (generateRandomLetterString (strLen) + i).build ();
      final PlayerPacket packet = PlayerPackets.from (player);

      mapBuilder.put (packet, player);
    }

    // will fail if duplicates exist
    mapBuilder.build ();
  }

  private static String generateRandomLetterString (final int length)
  {
    assertTrue (length > 0);

    final StringBuilder str = new StringBuilder (length);
    // NOTE: Can't use fg-tools Randomness because frequent calls seem to cause it to hang on an internal
    // method, according to profiler (generateSystemEntropySeed). Using java.util.Random instead.
    final Random rand = new Random ();
    for (int i = 0; i < length; ++i)
    {
      final int ind = rand.nextInt (LETTERS.length ());
      str.append (LETTERS.charAt (ind));
    }
    return str.toString ();
  }
}
