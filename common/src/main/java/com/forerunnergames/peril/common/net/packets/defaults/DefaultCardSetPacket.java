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

import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

public class DefaultCardSetPacket implements CardSetPacket
{
  private final ImmutableSet <CardPacket> cards;

  public DefaultCardSetPacket (final ImmutableSet <CardPacket> cards)
  {
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkHasNoNullElements (cards, "cards");

    this.cards = cards;
  }

  @Override
  public ImmutableSet <CardPacket> getCards ()
  {
    return cards;
  }

  @Override
  public int getCardCount ()
  {
    return cards.size ();
  }

  @Override
  public boolean matches (final CardSetPacket cardSet)
  {
    return cards.equals (cardSet.getCards ());
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Cards: [{}]", getClass ().getSimpleName (), cards);
  }
}
