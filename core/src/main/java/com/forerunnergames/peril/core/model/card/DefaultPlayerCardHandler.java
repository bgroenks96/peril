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

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

final class DefaultPlayerCardHandler implements PlayerCardHandler
{
  private final BiMap <Id, CardSet> playerHands = HashBiMap.create ();
  private final PlayerModel playerModel;
  private final GameRules rules;

  DefaultPlayerCardHandler (final PlayerModel playerModel, final GameRules rules)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (rules, "rules");

    this.playerModel = playerModel;
    this.rules = rules;
  }

  @Override
  public void addCardToHand (final Id playerId, final Card card)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    add (playerId, card);
  }

  @Override
  public void removeCardsFromHand (final Id playerId, final CardSet cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (cards, "cards");

    remove (playerId, cards);
  }

  @Override
  public CardSet removePlayer (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final CardSet cards = getCardsInHand (playerId);
    remove (playerId, cards);
    playerHands.remove (playerId);

    return cards;
  }

  @Override
  public CardSet getCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    final Optional <CardSet> cards = get (playerId);
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit type cast
    if (!cards.isPresent ()) return newCardSet (ImmutableSet.<Card> of ());
    return cards.get ();
  }

  @Override
  public int countCardsInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerModel.getCardsInHand (playerId);
  }

  @Override
  public boolean isCardInHand (final Id playerId, final Card card)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (card, "card");

    return hasCardInHand (playerId, card);
  }

  @Override
  public boolean areCardsInHand (final Id playerId, final CardSet cards)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (cards, "cards");

    return hasCardsInHand (playerId, cards);
  }

  private void add (final Id playerId, final Card card)
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit type cast
    final CardSet hand = playerHands.containsKey (playerId) ? playerHands.get (playerId)
            : newCardSet (ImmutableSet.<Card> of ());
    final CardSet newHand = hand.merge (new CardSet (rules, card));
    playerHands.forcePut (playerId, newHand);
    playerModel.addCardToHandOf (playerId);
  }

  private void remove (final Id playerId, final CardSet cards)
  {
    // Nothing to remove.
    // This could also indicate the player is not registered yet, as registration only occurs when a card is first
    // added to the player's hand, and an empty card set can be obtained when attempting to get cards in hand for a
    // an unregistered player. Either way, we don't want to proceed.
    if (cards.isEmpty ()) return;

    assert playerHands.containsKey (playerId);
    assert hasCardsInHand (playerId, cards);

    final CardSet hand = playerHands.get (playerId);
    final CardSet newHand = hand.difference (cards);
    playerHands.forcePut (playerId, newHand);

    // When removing a player, we don't know if the player still exists in PlayerModel.
    // If the player has already been removed, we don't care about removing the number of cards from their hand.
    // Contrast this with #add, where we should always fail when adding a number cards to the hand of a non-existent
    // player in PlayerModel.
    if (playerModel.existsPlayerWith (playerId)) playerModel.removeCardsFromHandOf (playerId, cards.size ());
  }

  private Optional <CardSet> get (final Id playerId)
  {
    return Optional.fromNullable (playerHands.get (playerId));
  }

  private boolean hasCardInHand (final Id playerId, final Card card)
  {
    final CardSet hand = playerHands.get (playerId);
    return hand != null && hand.contains (card);
  }

  private boolean hasCardsInHand (final Id playerId, final CardSet cards)
  {
    final CardSet hand = playerHands.get (playerId);
    return hand != null && hand.containsAll (cards);
  }

  private CardSet newCardSet (final ImmutableSet <Card> cards)
  {
    return new CardSet (rules, cards);
  }
}
