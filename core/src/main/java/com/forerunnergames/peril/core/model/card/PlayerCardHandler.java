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

import com.forerunnergames.tools.common.id.Id;

interface PlayerCardHandler
{
  void addCardToHand (final Id playerId, final Card card);

  void removeCardsFromHand (final Id playerId, final CardSet cards);

  /**
   * Removes the player completely (from the perspective of PlayerCardHandler only).
   *
   * Note: {@link com.forerunnergames.peril.core.model.people.player.PlayerModel} will only remove the number of cards
   * from the player's hand, if the player still exists. It is not the responsibility of {@link PlayerCardHandler} to
   * completely remove a player from the game.
   *
   * @return Any cards in the player's hand, {@link CardSet} will be empty if there were no cards.
   */
  CardSet removePlayer (final Id playerId);

  CardSet getCardsInHand (final Id playerId);

  int countCardsInHand (final Id playerId);

  boolean isCardInHand (final Id playerId, final Card card);

  boolean areCardsInHand (final Id playerId, final CardSet cards);
}
