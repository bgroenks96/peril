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

package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.core.model.people.person.AbstractPerson;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.math.IntMath;

final class DefaultPlayer extends AbstractPerson implements Player
{
  private PlayerColor color;
  private PlayerTurnOrder turnOrder;
  private int armiesInHand;
  private int cardsInHand;

  DefaultPlayer (final String name,
                 final Id id,
                 final PersonSentience sentience,
                 final PlayerColor color,
                 final PlayerTurnOrder turnOrder)
  {
    super (name, id, sentience);

    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.color = color;
    this.turnOrder = turnOrder;
  }

  @Override
  public Player withNewName (final String newName)
  {
    return new DefaultPlayer (newName, this.getId (), this.getSentience (), this.getColor (), this.getTurnOrder ());
  }

  @Override
  public void addArmiesToHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    armiesInHand = IntMath.checkedAdd (armiesInHand, armies);
  }

  @Override
  public void addArmyToHand ()
  {
    addArmiesToHand (1);
  }

  @Override
  public void addCardsToHand (final int cards)
  {
    Arguments.checkIsNotNegative (cards, "cards");

    cardsInHand = IntMath.checkedAdd (cardsInHand, cards);
  }

  @Override
  public void addCardToHand ()
  {
    addCardsToHand (1);
  }

  @Override
  public boolean doesNotHave (final PlayerColor color)
  {
    return !has (color);
  }

  @Override
  public boolean doesNotHave (final PlayerTurnOrder turnOrder)
  {
    return !has (turnOrder);
  }

  @Override
  public int getArmiesInHand ()
  {
    return armiesInHand;
  }

  @Override
  public int getCardsInHand ()
  {
    return cardsInHand;
  }

  @Override
  public PlayerColor getColor ()
  {
    return color;
  }

  @Override
  public void setColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    this.color = color;
  }

  @Override
  public PlayerTurnOrder getTurnOrder ()
  {
    return turnOrder;
  }

  @Override
  public void setTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.turnOrder = turnOrder;
  }

  @Override
  public int getTurnOrderPosition ()
  {
    return turnOrder.getPosition ();
  }

  @Override
  public void setTurnOrderByPosition (final int position)
  {
    Arguments.checkIsNotNegative (position, "position");

    turnOrder = PlayerTurnOrder.getNthValidTurnOrder (position);
  }

  @Override
  public boolean has (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color == color;
  }

  @Override
  public boolean has (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return this.turnOrder == turnOrder;
  }

  @Override
  public boolean hasArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return armiesInHand == armies;
  }

  @Override
  public boolean hasAtLeastNArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return armiesInHand >= armies;
  }

  @Override
  public boolean hasAtMostNArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return armiesInHand <= armies;
  }

  @Override
  public boolean hasCardsInHand (final int cards)
  {
    Arguments.checkIsNotNegative (cards, "cards");

    return cardsInHand == cards;
  }

  @Override
  public boolean hasAtLeastNCardsInHand (final int cards)
  {
    Arguments.checkIsNotNegative (cards, "cards");

    return cardsInHand >= cards;
  }

  @Override
  public boolean hasAtMostNCardsInHand (final int cards)
  {
    Arguments.checkIsNotNegative (cards, "cards");

    return cardsInHand <= cards;
  }

  @Override
  public void removeArmyFromHand ()
  {
    removeArmiesFromHand (1);
  }

  @Override
  public void removeCardFromHand ()
  {
    removeCardsFromHand (1);
  }

  @Override
  public void removeArmiesFromHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (armies <= armiesInHand, "Cannot remove more armies [" + armies
            + "] than are currently in hand [" + armiesInHand + "].");

    armiesInHand = IntMath.checkedSubtract (armiesInHand, armies);
  }

  @Override
  public void removeCardsFromHand (final int cards)
  {
    Arguments.checkIsNotNegative (cards, "cards");
    Preconditions
            .checkIsTrue (cards <= cardsInHand,
                          "Cannot remove more cards [" + cards + "] than are currently in hand [" + cardsInHand + "].");

    cardsInHand = IntMath.checkedSubtract (cardsInHand, cards);
  }

  @Override
  public void removeAllArmiesFromHand ()
  {
    armiesInHand = 0;
  }

  @Override
  public void removeAllCardsFromHand ()
  {
    cardsInHand = 0;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Color: {} | TurnOrder: {} | ArmiesInHand: {} | CardsInHand: {}", super.toString (),
                           color, turnOrder, armiesInHand, cardsInHand);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayer ()
  {
  }
}
