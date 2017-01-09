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

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsDeniedEvent;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface CardModel
{
  boolean canGiveCard (final Id playerId, final TurnPhase turnPhase);

  Card giveCard (final Id playerId, final TurnPhase turnPhase);

  CardSet getCardsInHand (final Id playerId);

  /**
   * Removes the player completely (from the perspective of {@link CardModel} only) and returns any cards from the
   * player's hand to the live deck, reshuffling the live deck only. The discard pile remains unaffected.
   *
   * Note: {@link com.forerunnergames.peril.core.model.people.player.PlayerModel} will only remove the number of cards
   * from the player's hand, if the player still exists. It is not the responsibility of {@link CardModel} to completely
   * remove a player from the game.
   *
   * @return Any cards in the player's hand, {@link CardSet} will be empty if there were no cards.
   */
  CardSet removePlayer (final Id playerId);

  int countCardsInHand (final Id playerId);

  boolean isCardInHand (final Id playerId, final Card card);

  Card cardWith (final String name);

  boolean existsCardWith (final String name);

  ImmutableSet <CardSet.Match> computeMatchesFor (final Id playerId);

  Result <PlayerTradeInCardsDeniedEvent.Reason> requestTradeInCards (final Id playerId,
                                                                             final CardSet.Match tradeInCards,
                                                                             final TurnPhase turnPhase);

  int getNextTradeInBonus ();

  boolean areCardsInHand (final Id playerId, final CardSet cards);

  int getDeckCount ();

  int getDiscardCount ();
}
