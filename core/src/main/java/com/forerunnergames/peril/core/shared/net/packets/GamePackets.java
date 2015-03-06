package com.forerunnergames.peril.core.shared.net.packets;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.packets.defaults.DefaultCountryPacket;
import com.forerunnergames.peril.core.shared.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Collection;
import java.util.Map;

public final class GamePackets
{
  public static ImmutableSet <PlayerPacket> fromPlayers (final Collection <Player> players)
  {
    final Builder <PlayerPacket> packetSetBuilder = ImmutableSet.builder ();
    for (final Player player : players)
    {
      packetSetBuilder.add (new DefaultPlayerPacket (player));
    }
    return packetSetBuilder.build ();
  }

  public static ImmutableSet <CountryPacket> fromCountries (final Collection <Country> countries)
  {
    final Builder <CountryPacket> packetSetBuilder = ImmutableSet.builder ();
    for (final Country country : countries)
    {
      packetSetBuilder.add (new DefaultCountryPacket (country));
    }
    return packetSetBuilder.build ();
  }

  public static ImmutableMap <CountryPacket, PlayerPacket> fromPlayMap (final Map <Country, Player> playMap)
  {
    final ImmutableMap.Builder <CountryPacket, PlayerPacket> playMapBuilder = ImmutableMap.builder ();
    for (final Country country : playMap.keySet ())
    {
      playMapBuilder.put (new DefaultCountryPacket (country), new DefaultPlayerPacket (playMap.get (country)));
    }
    return playMapBuilder.build ();
  }

  private GamePackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
