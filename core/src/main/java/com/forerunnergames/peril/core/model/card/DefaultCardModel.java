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

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent.Reason;
import com.forerunnergames.peril.core.model.card.CardSet.Match;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public final class DefaultCardModel implements CardModel
{
  private final PlayerCardHandler playerCardHandler;
  private final CardDealer cardDealer;
  private final GameRules rules;
  private int tradeInCount = 0;

  public DefaultCardModel (final GameRules rules, final PlayerModel playerModel, final ImmutableSet <Card> cardDeck)
  {
    this (rules, new DefaultPlayerCardHandler (playerModel, rules), new DefaultCardDealer (cardDeck));
  }

  DefaultCardModel (final GameRules rules, final PlayerCardHandler playerCardHandler, final CardDealer cardDealer)
  {
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (playerCardHandler, "playerCardHandler");
    Arguments.checkIsNotNull (cardDealer, "cardDealer");

    this.rules = rules;
    this.playerCardHandler = playerCardHandler;
    this.cardDealer = cardDealer;
  }

  @Override
  public Card giveCard (final Id playerId, final TurnPhase turnPhase)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (turnPhase, "turnPhase");
    final int maxCardsInHand = rules.getMaxCardsInHand (turnPhase);
    Preconditions.checkIsTrue (playerCardHandler.countCardsInHand (playerId) < maxCardsInHand,
                               Strings.format ("Player [{}] has reached maximum cards in hand [{}] for [{}]", playerId,
                                               maxCardsInHand, turnPhase));

    final Card card = cardDealer.take ();
    playerCardHandler.addCardToHand (playerId, card);
    return card;
  }

  @Override
  public CardSet getCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerCardHandler.getCardsInHand (playerId);
  }

  @Override
  public int countCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerCardHandler.countCardsInHand (playerId);
  }

  // --- delegation methods --- //

  @Override
  public boolean isCardInHand (final Id playerId, final Card card)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (card, "card");

    return playerCardHandler.isCardInHand (playerId, card);
  }

  @Override
  public Card cardWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return cardDealer.cardWith (name);
  }

  @Override
  public boolean existsCardWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return cardDealer.existsCardWith (name);
  }

  @Override
  public ImmutableSet <Match> computeMatchesFor (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final CardSet playerHand = playerCardHandler.getCardsInHand (playerId);
    if (playerHand.size () < rules.getCardTradeInCount ()) return ImmutableSet.of ();
    final ImmutableSet <CardSet> powerSet = playerHand.powerSet ();
    final ImmutableSet.Builder <Match> matches = ImmutableSet.builder ();
    for (final CardSet cardSet : powerSet)
    {
      if (cardSet.size () != rules.getCardTradeInCount ()) continue;
      if (cardSet.isMatch ()) matches.add (cardSet.match ());
    }
    return matches.build ();
  }

  @Override
  public Result <PlayerTradeInCardsResponseDeniedEvent.Reason> requestTradeInCards (final Id playerId,
                                                                                    final Match tradeInCards,
                                                                                    final TurnPhase turnPhase)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (tradeInCards, "tradeInCards");
    Arguments.checkIsNotNull (turnPhase, "turnPhase");

    final int numCards = playerCardHandler.countCardsInHand (playerId);
    final boolean isOptionalTradeInNotAllowed = turnPhase != TurnPhase.REINFORCE
            && numCards < rules.getMinCardsInHandForTradeInReinforcePhase ();
    final boolean isRequiredTradeInNotAllowed = numCards >= rules.getMinCardsInHandToRequireTradeIn (turnPhase);
    if (isOptionalTradeInNotAllowed && isRequiredTradeInNotAllowed)
    {
      return Result.failure (Reason.TRADE_IN_NOT_ALLOWED);
    }

    final CardSet playerHand = playerCardHandler.getCardsInHand (playerId);
    final CardSet matchSet = tradeInCards.getCardSet ();
    if (turnPhase == TurnPhase.REINFORCE && numCards - matchSet.size () > rules.getMaxCardsInHand (TurnPhase.REINFORCE))
    {
      return Result.failure (Reason.TOO_MANY_CARDS_IN_HAND);
    }
    if (!playerHand.containsAll (matchSet)) return Result.failure (Reason.CARDS_NOT_IN_HAND);
    playerCardHandler.removeCardsFromHand (playerId, matchSet);
    cardDealer.discard (matchSet);
    tradeInCount++;
    return Result.success ();
  }

  @Override
  public int getNextTradeInBonus ()
  {
    return rules.calculateTradeInBonusReinforcements (tradeInCount);
  }

  @Override
  public boolean areCardsInHand (final Id playerId, final CardSet cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (cards, "cards");

    return playerCardHandler.areCardsInHand (playerId, cards);
  }
}
