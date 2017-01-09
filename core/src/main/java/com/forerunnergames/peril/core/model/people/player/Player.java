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

package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.core.model.people.person.Person;

import java.util.Comparator;

interface Player extends Person
{
  Comparator <Player> TURN_ORDER_COMPARATOR = new Comparator <Player> ()
  {
    @Override
    public int compare (final Player o1, final Player o2)
    {
      if (o1.getTurnOrderPosition () < o2.getTurnOrderPosition ())
      {
        return -1;
      }
      else if (o1.getTurnOrderPosition () > o2.getTurnOrderPosition ())
      {
        return 1;
      }
      else
      {
        return 0;
      }
    }
  };

  /**
   * Creates an exact copy of this Player with a new name.
   */
  Player withNewName (final String newName);

  void addArmiesToHand (final int armies);

  void addArmyToHand ();

  void addCardsToHand (final int cards);

  void addCardToHand ();

  boolean doesNotHave (final PlayerColor color);

  boolean doesNotHave (final PlayerTurnOrder turnOrder);

  int getArmiesInHand ();

  int getCardsInHand ();

  PlayerColor getColor ();

  void setColor (final PlayerColor color);

  PlayerTurnOrder getTurnOrder ();

  void setTurnOrder (final PlayerTurnOrder turnOrder);

  /**
   * Position 1 is PlayerTurnOrder.FIRST, position 2 is PlayerTurnOrder.SECOND, etc. Position 0 is invalid & cannot be
   * set.
   */
  int getTurnOrderPosition ();

  /**
   * Position 1 is PlayerTurnOrder.FIRST, position 2 is PlayerTurnOrder.SECOND, etc. Position 0 is invalid & cannot be
   * set.
   */
  void setTurnOrderByPosition (final int position);

  boolean has (final PlayerColor color);

  boolean has (final PlayerTurnOrder turnOrder);

  boolean hasArmiesInHand (final int armies);

  boolean hasAtLeastNArmiesInHand (final int armies);

  boolean hasAtMostNArmiesInHand (final int armies);

  boolean hasCardsInHand (final int cards);

  boolean hasAtLeastNCardsInHand (final int cards);

  boolean hasAtMostNCardsInHand (final int cards);

  void removeArmyFromHand ();

  void removeCardFromHand ();

  void removeArmiesFromHand (final int armies);

  void removeCardsFromHand (final int cards);

  void removeAllArmiesFromHand ();

  void removeAllCardsFromHand ();
}
