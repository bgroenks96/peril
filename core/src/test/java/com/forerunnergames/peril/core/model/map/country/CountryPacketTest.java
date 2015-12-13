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
