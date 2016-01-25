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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.CardType;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

/**
 * Runs all tests in CardDealerTest suite as well as implementation level tests for DefaultCardDealer.
 */
public class DefaultCardDealerTest extends CardDealerTest
{
  @Override
  protected CardDealer createCardDealer (final ImmutableSet <Card> cards)
  {
    return new DefaultCardDealer (cards);
  }

  @Test
  public void testTakeReshufflesDeckWhenEmpty ()
  {
    final Card testCard1 = CardFactory.create ("TestCard-1", CardType.TYPE1);
    final Card testCard2 = CardFactory.create ("TestCard-2", CardType.TYPE2);
    final CardDealer deckHandler = createCardDealer (ImmutableSet.of (testCard1, testCard2));
    final Card card1 = deckHandler.take ();
    final Card card2 = deckHandler.take ();
    deckHandler.discard (card1);
    deckHandler.discard (card2);
    assertFalse (deckHandler.isInDeck (card1));
    assertFalse (deckHandler.isInDeck (card2));
    final Card card3 = deckHandler.take ();
    assertTrue (deckHandler.isInDeck (card1) || deckHandler.isInDeck (card2));
    assertTrue (card3.equals (card1) || card3.equals (card2));
  }
}
