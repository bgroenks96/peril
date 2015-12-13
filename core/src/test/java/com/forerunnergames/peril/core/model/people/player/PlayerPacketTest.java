package com.forerunnergames.peril.core.model.people.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

import com.google.common.collect.ImmutableMap;

import java.util.Random;

import org.junit.Test;

public class PlayerPacketTest
{
  private static final int SAMPLE_SIZE_MANY = 1000000;
  private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

  @Test
  public void testTwoCommonPlayerPlayerPacketsAreEqual ()
  {
    final Player player = PlayerFactory.create ("TestPlayer-1");

    final PlayerPacket packet0 = PlayerPackets.from (player);
    final PlayerPacket packet1 = PlayerPackets.from (player);

    assertEquals (packet0, packet1);
  }

  @Test
  public void testTwoDifferentPlayerPlayerPacketsAreNotEqual ()
  {
    final Player player0 = PlayerFactory.builder ("TestPlayer-0").build ();
    final Player player1 = PlayerFactory.builder ("TestPlayer-1").build ();

    final PlayerPacket packet0 = PlayerPackets.from (player0);
    final PlayerPacket packet1 = PlayerPackets.from (player1);

    assertNotEquals (packet0, packet1);
  }

  @Test
  public void testManyDifferentPlayerPlayerPacketsAreNotEqual ()
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
