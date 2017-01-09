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

package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.packets.person.AbstractPersonPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public final class DefaultPlayerPacket extends AbstractPersonPacket implements PlayerPacket
{
  private final PlayerColor color;
  private final int turnOrder;
  private final int armiesInHand;
  private final int cardsInHand;

  public DefaultPlayerPacket (final UUID playerId,
                              final String name,
                              final PersonSentience sentience,
                              final PlayerColor color,
                              final int turnOrder,
                              final int armiesInHand,
                              final int cardsInHand)
  {
    super (name, playerId, sentience);

    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");
    Arguments.checkIsNotNegative (armiesInHand, "armiesInHand");
    Arguments.checkIsNotNegative (cardsInHand, "cardsInHand");

    this.color = color;
    this.turnOrder = turnOrder;
    this.armiesInHand = armiesInHand;
    this.cardsInHand = cardsInHand;
  }

  @Override
  public PlayerColor getColor ()
  {
    return color;
  }

  @Override
  public int getTurnOrder ()
  {
    return turnOrder;
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
  public boolean has (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color == color;
  }

  @Override
  public boolean has (final int turnOrder)
  {
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");

    return this.turnOrder == turnOrder;
  }

  @Override
  public boolean hasArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armiesInHand");

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
  public boolean doesNotHave (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color != color;
  }

  @Override
  public boolean doesNotHave (final int turnOrder)
  {
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");

    return this.turnOrder != turnOrder;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Color: {} | TurnOrder: {} | ArmiesInHand: {} | CardsInHand: {}", super.toString (),
                           color, turnOrder, armiesInHand, cardsInHand);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerPacket ()
  {
    color = null;
    turnOrder = 0;
    armiesInHand = 0;
    cardsInHand = 0;
  }
}
