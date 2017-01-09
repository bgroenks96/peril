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

package com.forerunnergames.peril.core.model.io;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.io.BiMapDataLoader;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.io.CardModelDataLoader;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;

import org.junit.BeforeClass;

public class CardModelDataLoaderTest extends DataLoaderTest <ImmutableBiMap <Id, Card>>
{
  private static final String TEST_CARDS_FILENAME = "test-cards.txt";
  private static final int EXPECTED_CARD_COUNT_FROM_FILE = 7;
  private static final int[] EXPECTED_CARD_TYPES_FROM_FILE = { CardType.VALUE3, CardType.VALUE3, CardType.VALUE2,
          CardType.VALUE1, CardType.VALUE1, CardType.VALUE2, CardType.VALUE_WILDCARD };
  private static ImmutableSet <Card> mockCards;

  @Override
  protected BiMapDataLoader <Id, Card> createDataLoader ()
  {
    return new CardModelDataLoader (new InternalStreamParserFactory ());
  }

  @Override
  protected boolean verifyData (final ImmutableBiMap <Id, Card> cardData)
  {
    if (mockCards.size () != cardData.size ()) return false;

    for (final Card actualCard : cardData.values ())
    {
      boolean isMatch = false;
      for (final Card mockCard : mockCards)
      {
        if (!mockCard.getName ().equals (actualCard.getName ())) continue;
        if (!mockCard.getType ().equals (actualCard.getType ())) continue;
        isMatch = true;
        break;
      }
      if (isMatch) return true;
    }

    return false;
  }

  @Override
  protected String getTestDataFileName ()
  {
    return TEST_CARDS_FILENAME;
  }

  @BeforeClass
  public static void setupClass ()
  {
    final ImmutableSet.Builder <Card> cardSetBuilder = ImmutableSet.builder ();
    for (int i = 1; i <= EXPECTED_CARD_COUNT_FROM_FILE; ++i)
    {
      final Card expectedCard = mock (Card.class);
      final String expectedCardName = "Test-Card-" + i;

      when (expectedCard.getName ()).thenReturn (expectedCardName);
      when (expectedCard.getType ()).thenReturn (CardType.fromValue (EXPECTED_CARD_TYPES_FROM_FILE [i - 1]));

      cardSetBuilder.add (expectedCard);
    }
    mockCards = cardSetBuilder.build ();
  }
}
