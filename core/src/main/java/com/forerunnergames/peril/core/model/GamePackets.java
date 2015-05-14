package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.packets.CountryPacket;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.defaults.DefaultCountryPacket;
import com.forerunnergames.peril.core.shared.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Collection;
import java.util.Map;

public final class GamePackets
{
  public static PlayerPacket fromPlayer (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return new DefaultPlayerPacket (player.getName (), player.getColor ().toString (), player.getTurnOrder ().asInt (),
            player.getArmiesInHand ());
  }

  public static CountryPacket fromCountry (final Country country)
  {
    Arguments.checkIsNotNull (country, "country");

    return new DefaultCountryPacket (country.getCountryName ().asString (), country.getArmyCount ());
  }

  public static ImmutableSet <PlayerPacket> fromPlayers (final Collection <Player> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    final Builder <PlayerPacket> packetSetBuilder = ImmutableSet.builder ();
    for (final Player player : players)
    {
      packetSetBuilder.add (fromPlayer (player));
    }
    return packetSetBuilder.build ();
  }

  public static ImmutableSet <CountryPacket> fromCountries (final Collection <Country> countries)
  {
    Arguments.checkIsNotNull (countries, "players");
    Arguments.checkHasNoNullElements (countries, "players");

    final Builder <CountryPacket> packetSetBuilder = ImmutableSet.builder ();
    for (final Country country : countries)
    {
      packetSetBuilder.add (fromCountry (country));
    }
    return packetSetBuilder.build ();
  }

  public static ImmutableMap <CountryPacket, PlayerPacket> fromPlayMap (final Map <Country, Player> playMap)
  {
    Arguments.checkIsNotNull (playMap, "players");
    Arguments.checkHasNoNullKeysOrValues (playMap, "playMap");

    final ImmutableMap.Builder <CountryPacket, PlayerPacket> playMapBuilder = ImmutableMap.builder ();
    for (final Country country : playMap.keySet ())
    {
      playMapBuilder.put (fromCountry (country), fromPlayer (playMap.get (country)));
    }
    return playMapBuilder.build ();
  }

  private GamePackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
