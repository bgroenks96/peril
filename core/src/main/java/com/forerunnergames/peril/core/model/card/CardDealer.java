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

interface CardDealer
{
  boolean canTake ();

  Card take ();

  void discard (final Card card);

  void discard (final CardSet cards);

  /**
   * Returns the specified cards to the live deck, afterward reshuffling the live deck only. The discard pile remains
   * unaffected. The cards must not already be in the live deck nor in the discard pile.
   */
  void returnToDeck (final CardSet cards);

  Card cardWith (final String name);

  boolean existsCardWith (final String name);

  boolean isInDeck (final Card card);

  boolean isInDiscardPile (final Card card);

  int getDeckCount ();

  int getDiscardCount ();
}
