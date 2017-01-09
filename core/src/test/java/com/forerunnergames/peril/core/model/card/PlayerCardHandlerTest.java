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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;

public abstract class PlayerCardHandlerTest
{
  private static final String TEST_PLAYER_NAME = "TestPlayer0";
  private final GameRules defaultRules = ClassicGameRules.defaults ();
  private final PlayerModel playerModel = new DefaultPlayerModel (defaultRules);

  @Before
  public void beforeTest ()
  {
    final PlayerFactory factory = new PlayerFactory ();
    factory.newHumanPlayerWith (TEST_PLAYER_NAME);
    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (factory)));
  }

  @Before
  public void afterTest ()
  {
    playerModel.removeByName (TEST_PLAYER_NAME);
  }

  @Test
  public void testAddCardToHandOfPlayer ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (playerModel, defaultRules);
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    final Card card = CardFactory.create ("TestCard", CardType.TYPE1);
    cardHandler.addCardToHand (testPlayerId, card);
    assertTrue (cardHandler.isCardInHand (testPlayerId, card));
  }

  @Test
  public void testRemoveAllCardsFromHandOfPlayer ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (playerModel, defaultRules);
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    final ImmutableSet.Builder <Card> cards = ImmutableSet.builder ();
    for (int i = 0; i < 3; i++)
    {
      final Card card = CardFactory.create ("TestCard-" + i, CardType.TYPE2);
      cardHandler.addCardToHand (testPlayerId, card);
      cards.add (card);
    }
    final CardSet toRemove = createCardSetFrom (cards.build ());
    cardHandler.removeCardsFromHand (testPlayerId, toRemove);
    assertTrue (cardHandler.getCardsInHand (testPlayerId).size () == 0);
  }

  @Test
  public void testRemoveSomeCardsFromHandOfPlayer ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (playerModel, defaultRules);
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    final ImmutableSet.Builder <Card> cardsToRetain = ImmutableSet.builder ();
    final ImmutableSet.Builder <Card> cardsToRemove = ImmutableSet.builder ();
    for (int i = 0; i < 5; i++)
    {
      final Card card = CardFactory.create ("TestCard-" + i, CardType.TYPE3);
      cardHandler.addCardToHand (testPlayerId, card);
      if (i < 2)
      {
        cardsToRetain.add (card);
      }
      else
      {
        cardsToRemove.add (card);
      }
    }

    final CardSet retainSet = createCardSetFrom (cardsToRetain.build ());
    final CardSet removeSet = createCardSetFrom (cardsToRemove.build ());
    cardHandler.removeCardsFromHand (testPlayerId, removeSet);
    assertFalse (cardHandler.getCardsInHand (testPlayerId).containsAny (removeSet));
    assertTrue (cardHandler.getCardsInHand (testPlayerId).containsAll (retainSet));
  }

  @Test
  public void testIsCardInHand ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (playerModel, defaultRules);
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    final Card card = CardFactory.create ("TestCard", CardType.TYPE1);
    cardHandler.addCardToHand (testPlayerId, card);
    assertTrue (cardHandler.isCardInHand (testPlayerId, card));
  }

  @Test
  public void testIsCardNotInHand ()
  {
    final Card card = CardFactory.create ("TestCard", CardType.random ());
    final PlayerCardHandler cardHandler = createPlayerCardHandler (playerModel, defaultRules);
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    assertFalse (cardHandler.isCardInHand (testPlayerId, card));
  }

  @Test
  public void testAreCardsInHand ()
  {
    final PlayerCardHandler cardHandler = createPlayerCardHandler (playerModel, defaultRules);
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    final Card card1 = CardFactory.create ("TestCard-1", CardType.TYPE1);
    final Card card2 = CardFactory.create ("TestCard-2", CardType.TYPE2);
    cardHandler.addCardToHand (testPlayerId, card1);
    cardHandler.addCardToHand (testPlayerId, card2);
    assertTrue (cardHandler.areCardsInHand (testPlayerId, createCardSetFrom (ImmutableSet.of (card1, card2))));
  }

  @Test
  public void testAreCardsNotInHand ()
  {
    final ImmutableSet <Card> cards = CardDealerTest.generateCards (3, CardType.random ());
    final PlayerCardHandler cardHandler = createPlayerCardHandler (playerModel, defaultRules);
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    assertFalse (cardHandler.areCardsInHand (testPlayerId, createCardSetFrom (cards)));
  }

  protected abstract PlayerCardHandler createPlayerCardHandler (final PlayerModel playerModel, final GameRules rules);

  private CardSet createCardSetFrom (final ImmutableSet <Card> cards)
  {
    final GameRules rules = ClassicGameRules.defaults ();
    return new CardSet (rules, cards);
  }
}
