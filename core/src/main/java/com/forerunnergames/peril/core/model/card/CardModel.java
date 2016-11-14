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
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface CardModel
{
  boolean canGiveCard (final Id playerId, final TurnPhase turnPhase);

  Card giveCard (final Id playerId, final TurnPhase turnPhase);

  CardSet getCardsInHand (final Id playerId);

  int countCardsInHand (final Id playerId);

  boolean isCardInHand (final Id playerId, final Card card);

  Card cardWith (final String name);

  boolean existsCardWith (final String name);

  ImmutableSet <CardSet.Match> computeMatchesFor (final Id playerId);

  Result <PlayerTradeInCardsResponseDeniedEvent.Reason> requestTradeInCards (final Id playerId,
                                                                             final CardSet.Match tradeInCards,
                                                                             final TurnPhase turnPhase);

  int getNextTradeInBonus ();

  boolean areCardsInHand (final Id playerId, final CardSet cards);

  int getDeckCount ();

  int getDiscardCount ();
}
