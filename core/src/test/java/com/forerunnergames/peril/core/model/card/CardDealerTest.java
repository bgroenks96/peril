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

package com.forerunnergames.peril.core.model.card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.CardType;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

public abstract class CardDealerTest
{
  private static final ImmutableSet <Card> defaultCards = generateTestCards ();

  static ImmutableSet <Card> generateCards (final CardType type, final int count)
  {
    final ImmutableSet.Builder <Card> cardSetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < count; i++)
    {
      cardSetBuilder.add (CardFactory.create ("TestCard-" + i, type));
    }
    return cardSetBuilder.build ();
  }

  public static ImmutableSet <Card> generateTestCards ()
  {
    final ImmutableSet.Builder <Card> cardSetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < CardModelTest.DEFAULT_CARD_COUNT; i++)
    {
      final int type = i % 3 + 1; // cycle over values 1, 2, and 3.
      cardSetBuilder.add (CardFactory.create ("TestCard-" + i, CardType.fromValue (type)));
    }
    for (int i = 0; i < CardModelTest.DEFAULT_WILDCARD_COUNT; i++)
    {
      cardSetBuilder.add (CardFactory.create ("Test-Wildcard-" + i, CardType.WILDCARD));
    }
    return cardSetBuilder.build ();
  }

  @Test
  public void testIsInDeckNotInDiscardPile ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard));
    assertTrue (deckHandler.isInDeck (testCard));
    assertFalse (deckHandler.isInDiscardPile (testCard));
  }

  @Test
  public void testIsNotInDeckWhenDeckIsEmpty ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.<Card> of ());
    assertFalse (deckHandler.isInDeck (testCard));
  }

  @Test
  public void testTakeFromDeck ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard));
    assertEquals (testCard, deckHandler.take ());
    assertFalse (deckHandler.isInDeck (testCard));
  }

  @Test (expected = IllegalStateException.class)
  public void testTakeFromDeckFailsWhenCardNotInDeck ()
  {
    final CardDealer deckHandler = createCardDealer (ImmutableSet.<Card> of ());
    deckHandler.take ();
  }

  @Test
  public void testDiscardNotInDeckIsInDiscardPile ()
  {
    final CardDealer deckHandler = createCardDealer (defaultCards);
    final Card card = deckHandler.take ();
    assertFalse (deckHandler.isInDeck (card));
    deckHandler.discard (card);
    assertFalse (deckHandler.isInDeck (card));
    assertTrue (deckHandler.isInDiscardPile (card));
  }

  @Test
  public void testCardWith ()
  {
    final Card testCard = CardFactory.create ("TestCard", CardType.TYPE1);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard));
    assertEquals (testCard, deckHandler.cardWith (testCard.getName ()));
  }

  @Test (expected = IllegalStateException.class)
  public void testCardWithFailsWhenNotInDeck ()
  {
    final CardDealer deckHandler = createCardDealer (ImmutableSet.<Card> of ());
    deckHandler.cardWith ("");
  }

  protected abstract CardDealer createCardDealer (final ImmutableSet <Card> cards);
}
