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
