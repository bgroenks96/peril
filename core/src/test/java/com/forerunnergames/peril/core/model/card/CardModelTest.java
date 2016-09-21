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
import static org.junit.Assert.fail;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests ONLY methods in CardModel that employ unique logic; methods that act as forward API calls to PlayerCardHandler
 * and/or CardDealer have their functionality tested by the respective test suites for those classes.
 */
public abstract class CardModelTest
{
  static final int DEFAULT_CARD_COUNT = 45;
  static final int DEFAULT_WILDCARD_COUNT = 2;
  private static final String TEST_PLAYER_NAME = "TestPlayer0";
  private final GameRules rules = new ClassicGameRules.Builder ().build ();
  private final ImmutableSet <Card> testDeck = generateTestCards ();
  private final PlayerModel playerModel = new DefaultPlayerModel (rules);

  public static ImmutableSet <Card> generateTestCards ()
  {
    return CardDealerTest.generateTestCards ();
  }

  public static ImmutableSet <Card> generateCards (final CardType type, final int count)
  {
    return CardDealerTest.generateCards (type, count);
  }

  @Before
  public void beforeTest ()
  {
    final PlayerFactory factory = new PlayerFactory ();
    factory.newPlayerWith (TEST_PLAYER_NAME);
    assertFalse (Result.anyStatusFailed (playerModel.requestToAdd (factory)));
  }

  @Before
  public void afterTest ()
  {
    playerModel.removeByName (TEST_PLAYER_NAME);
  }

  @Test (expected = IllegalStateException.class)
  public void testGiveCardFailsWhenHandFullReinforceTurnPhase ()
  {
    final int maxCards = rules.getMaxCardsInHand (TurnPhase.REINFORCE);
    final CardModel cardModel = createCardModel (rules, playerModel,
                                                 CardDealerTest.generateCards (CardType.TYPE1, maxCards + 1));
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    for (int i = 0; i < maxCards + 1; i++)
    {
      cardModel.giveCard (testPlayerId, TurnPhase.REINFORCE);
    }
  }

  @Test (expected = IllegalStateException.class)
  public void testGiveCardFailsWhenHandFullAttackTurnPhase ()
  {
    final int maxCards = rules.getMaxCardsInHand (TurnPhase.ATTACK);
    final CardModel cardModel = createCardModel (rules, playerModel,
                                                 CardDealerTest.generateCards (CardType.TYPE1, maxCards + 1));
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    for (int i = 0; i < maxCards + 1; i++)
    {
      cardModel.giveCard (testPlayerId, TurnPhase.ATTACK);
    }
  }

  @Test (expected = IllegalStateException.class)
  public void testGiveCardFailsWhenHandFullFortifyTurnPhase ()
  {
    final int maxCards = rules.getMaxCardsInHand (TurnPhase.FORTIFY);
    final CardModel cardModel = createCardModel (rules, playerModel,
                                                 CardDealerTest.generateCards (CardType.TYPE1, maxCards + 1));
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    for (int i = 0; i < maxCards + 1; i++)
    {
      cardModel.giveCard (testPlayerId, TurnPhase.FORTIFY);
    }
  }

  @Test
  public void testTradeInCards ()
  {
    // make deck size only enough to do one trade in; this is important so that we can test
    // whether or not CardModel is managing the deck correctly via hasCardsRemainingInDeck()
    final int deckSize = rules.getCardTradeInCount ();
    final ImmutableSet <Card> generatedCards = CardDealerTest.generateCards (CardType.TYPE3, deckSize);
    final CardDealer dealer = createCardDealer (generatedCards);
    final CardSet cardSet = new CardSet (rules, generatedCards);

    // just as a formality...
    assertTrue (cardSet.isMatch ());

    final CardSet.Match match = cardSet.match ();
    final CardModel cardModel = createCardModel (rules, playerModel, dealer, match.getCards ());
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    for (int i = 0; i < deckSize; i++)
    {
      cardModel.giveCard (testPlayerId, TurnPhase.REINFORCE);
    }

    final Result <PlayerTradeInCardsResponseDeniedEvent.Reason> result = cardModel
            .requestTradeInCards (testPlayerId, match, TurnPhase.REINFORCE);
    // use if/fail so failure reason can be printed;
    // assertTrue causes Result to throw IllegalStateException when successful
    if (result.failed ()) fail (result.getFailureReason ().toString ());

    // make sure cards were moved from hand back to the deck/discard-pile
    assertFalse (cardModel.areCardsInHand (testPlayerId, createCardSetFrom (match.getCards ())));
    for (final Card card : match.getCards ())
    {
      assertTrue (dealer.isInDiscardPile (card));
    }
  }

  @Test
  public void testNextTradeInBonusInitial ()
  {
    final CardModel cardModel = createCardModel (rules, playerModel, testDeck);
    assertEquals (rules.calculateTradeInBonusReinforcements (0), cardModel.getNextTradeInBonus ());
  }

  @Test
  public void testNextTradeInBonusGlobalCountIs2 ()
  {
    final CardModel cardModel = createCardModel (rules, playerModel, CardDealerTest.generateCards (CardType.TYPE1, 6));
    final int tradeInCount = 2;
    for (int i = 0; i < tradeInCount; i++)
    {
      final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
      for (int k = 0; k < 3; k++)
      {
        cardModel.giveCard (testPlayerId, TurnPhase.REINFORCE);
      }
      final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (testPlayerId);
      // there will only be one match; turn set into list and fetch it
      cardModel.requestTradeInCards (testPlayerId, matches.asList ().get (0), TurnPhase.REINFORCE);
    }
    assertEquals (rules.calculateTradeInBonusReinforcements (2), cardModel.getNextTradeInBonus ());
  }

  @Test
  public void testNextTradeInBonusGlobalCountIs7 ()
  {
    final CardModel cardModel = createCardModel (rules, playerModel, CardDealerTest.generateCards (CardType.TYPE1, 21));
    final int tradeInCount = 7;
    for (int i = 0; i < tradeInCount; i++)
    {
      final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
      for (int k = 0; k < 3; k++)
      {
        cardModel.giveCard (testPlayerId, TurnPhase.REINFORCE);
      }
      final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (testPlayerId);
      // there will only be one match; turn set into list and fetch it
      cardModel.requestTradeInCards (testPlayerId, matches.asList ().get (0), TurnPhase.REINFORCE);
    }
    assertEquals (rules.calculateTradeInBonusReinforcements (7), cardModel.getNextTradeInBonus ());
  }

  @Test
  public void testComputeMatchesNoMatch ()
  {
    final int deckSize = rules.getCardTradeInCount ();
    // all wildcards; shouldn't match (for ClassicGameRules at leasts)
    final CardModel cardModel = createCardModel (rules, playerModel,
                                                 CardDealerTest.generateCards (CardType.WILDCARD, deckSize));
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    for (int i = 0; i < rules.getCardTradeInCount (); i++)
    {
      cardModel.giveCard (testPlayerId, TurnPhase.REINFORCE);
    }
    final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (testPlayerId);
    // assert no matches
    assertEquals (0, matches.size ());
  }

  @Test
  public void testComputeMatchesOneMatch ()
  {
    final int deckSize = rules.getCardTradeInCount ();
    final CardModel cardModel = createCardModel (rules, playerModel,
                                                 CardDealerTest.generateCards (CardType.TYPE2, deckSize));
    final Id testPlayerId = playerModel.idOf (TEST_PLAYER_NAME);
    final ImmutableSet.Builder <Card> builder = ImmutableSet.builder ();
    for (int i = 0; i < rules.getCardTradeInCount (); i++)
    {
      builder.add (cardModel.giveCard (testPlayerId, TurnPhase.REINFORCE));
    }
    final ImmutableSet <Card> expected = builder.build ();
    final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (testPlayerId);
    // make sure there isn't somehow more than one match...
    assertEquals (1, matches.size ());
    // assert match equals expected
    assertEquals (expected, matches.asList ().get (0).getCards ());
  }

  protected abstract CardModel createCardModel (final GameRules rules,
                                                final PlayerModel playerModel,
                                                final ImmutableSet <Card> cards);

  protected abstract CardModel createCardModel (final GameRules rules,
                                                final PlayerModel playerModel,
                                                final CardDealer dealer,
                                                final ImmutableSet <Card> cards);

  protected abstract CardDealer createCardDealer (final ImmutableSet <Card> cardDeck);

  // TODO add tests for computing more than one match (?)

  private CardSet createCardSetFrom (final ImmutableSet <Card> cards)
  {
    final GameRules defaultRules = new ClassicGameRules.Builder ().build ();
    return new CardSet (defaultRules, cards);
  }
}
