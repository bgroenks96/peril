package com.forerunnergames.peril.core.model;

import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.packets.defaults.DefaultCountryPacket;
import com.forerunnergames.peril.core.shared.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import java.util.Collection;
import java.util.Map;

public final class Packets
{
  public static PlayerPacket from (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return new DefaultPlayerPacket (idOf (player).value (), nameOf (player), player.getColor ().toString (), player
            .getTurnOrder ().asInt (), player.getArmiesInHand ());
  }

  public static CountryPacket from (final Country country)
  {
    Arguments.checkIsNotNull (country, "country");

    return new DefaultCountryPacket (idOf (country).value (), nameOf (country), country.getArmyCount ());
  }

  public static ImmutableSet <PlayerPacket> fromPlayers (final Collection <Player> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    final Builder <PlayerPacket> packetSetBuilder = ImmutableSet.builder ();
    for (final Player player : players)
    {
      packetSetBuilder.add (from (player));
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
      packetSetBuilder.add (from (country));
    }
    return packetSetBuilder.build ();
  }

  public static ImmutableMap <CountryPacket, PlayerPacket> fromPlayMap (final Map <Country, Player> playMap)
  {
    Arguments.checkIsNotNull (playMap, "players");
    Arguments.checkHasNoNullKeysOrValues (playMap, "playMap");

    final ImmutableMap.Builder <CountryPacket, PlayerPacket> playMapBuilder = ImmutableMap.builder ();
    for (final Map.Entry <Country, Player> countryPlayerEntry : playMap.entrySet ())
    {
      playMapBuilder.put (from (countryPlayerEntry.getKey ()), from (countryPlayerEntry.getValue ()));
    }
    return playMapBuilder.build ();
  }

  public static boolean playerMatchesPacket (final Player player, final PlayerPacket playerPacket)
  {
    return from (player).is (playerPacket);
  }

  public static boolean countryMatchesPacket (final Country country, final CountryPacket countryPacket)
  {
    return from (country).is (countryPacket);
  }

  private Packets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
