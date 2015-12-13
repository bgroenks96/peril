package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCardPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCardSetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public final class CardPackets
{
  public static CardPacket from (final Card card)
  {
    Arguments.checkIsNotNull (card, "card");

    return new DefaultCardPacket (card.getName (), card.getType ().getTypeValue (), card.getId ().value ());
  }

  public static CardSetPacket fromCards (final Collection <Card> cards)
  {
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkHasNoNullElements (cards, "cards");

    final ImmutableSet.Builder <CardPacket> cardSetBuilder = ImmutableSet.builder ();
    for (final Card card : cards)
    {
      cardSetBuilder.add (from (card));
    }
    return new DefaultCardSetPacket (cardSetBuilder.build ());
  }

  public static ImmutableSet <CardSetPacket> fromCardMatchSet (final Collection <CardSet.Match> matches)
  {
    Arguments.checkIsNotNull (matches, "matches");
    Arguments.checkHasNoNullElements (matches, "matches");

    final ImmutableSet.Builder <CardSetPacket> cardSetBuilder = ImmutableSet.builder ();
    for (final CardSet.Match match : matches)
    {
      cardSetBuilder.add (fromCards (match.getCards ()));
    }
    return cardSetBuilder.build ();
  }

  public static ImmutableSet <Card> toCardSet (final Collection <CardPacket> cardPackets, final CardModel cardModel)
  {
    Arguments.checkIsNotNull (cardPackets, "cardPackets");
    Arguments.checkIsNotNull (cardModel, "cardModel");
    Arguments.checkHasNoNullElements (cardPackets, "cardPackets");

    final ImmutableSet.Builder <Card> cards = ImmutableSet.builder ();
    for (final CardPacket cardPacket : cardPackets)
    {
      cards.add (cardModel.cardWith (cardPacket.getName ()));
    }
    return cards.build ();
  }

  private CardPackets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
