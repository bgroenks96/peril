/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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
