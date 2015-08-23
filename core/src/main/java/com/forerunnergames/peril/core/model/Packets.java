package com.forerunnergames.peril.core.model;

import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCardPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCardSetPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCountryPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
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

    return new DefaultPlayerPacket (idOf (player).value (), nameOf (player), player.getColor ().toString (),
            player.getTurnOrder ().asInt (), player.getArmiesInHand ());
  }

  public static CountryPacket from (final Country country)
  {
    Arguments.checkIsNotNull (country, "country");

    return new DefaultCountryPacket (idOf (country).value (), nameOf (country), country.getArmyCount ());
  }

  public static CardPacket from (final Card card)
  {
    Arguments.checkIsNotNull (card, "card");

    return new DefaultCardPacket (card.getName (), card.getType ().getTypeValue (), card.getId ().value ());
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
    Arguments.checkIsNotNull (countries, "countries");

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
    Arguments.checkIsNotNull (playMap, "playMap");

    Arguments.checkIsNotNull (playMap, "players");
    Arguments.checkHasNoNullKeysOrValues (playMap, "playMap");

    final ImmutableMap.Builder <CountryPacket, PlayerPacket> playMapBuilder = ImmutableMap.builder ();
    for (final Map.Entry <Country, Player> countryPlayerEntry : playMap.entrySet ())
    {
      playMapBuilder.put (from (countryPlayerEntry.getKey ()), from (countryPlayerEntry.getValue ()));
    }
    return playMapBuilder.build ();
  }

  public static CardSetPacket fromCards (final Collection <Card> cards)
  {
    final ImmutableSet.Builder <CardPacket> cardSetBuilder = ImmutableSet.builder ();
    for (final Card card : cards)
    {
      cardSetBuilder.add (from (card));
    }
    return new DefaultCardSetPacket (cardSetBuilder.build ());
  }

  public static ImmutableSet <CardSetPacket> fromCardMatchSet (final Collection <CardSet.Match> matches)
  {
    final ImmutableSet.Builder <CardSetPacket> cardSetBuilder = ImmutableSet.builder ();
    for (final CardSet.Match match : matches)
    {
      cardSetBuilder.add (fromCards (match.getCards ()));
    }
    return cardSetBuilder.build ();
  }

  public static boolean playerMatchesPacket (final Player player, final PlayerPacket playerPacket)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (playerPacket, "playerPacket");

    return from (player).is (playerPacket);
  }

  public static boolean countryMatchesPacket (final Country country, final CountryPacket countryPacket)
  {
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkIsNotNull (countryPacket, "countryPacket");

    return from (country).is (countryPacket);
  }

  private Packets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
