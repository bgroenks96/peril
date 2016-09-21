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

package com.forerunnergames.peril.common.net.packets.person;

import com.forerunnergames.peril.common.game.PlayerColor;

import java.util.Comparator;

public interface PlayerPacket extends PersonPacket
{
  Comparator <PlayerPacket> TURN_ORDER_COMPARATOR = new Comparator <PlayerPacket> ()
  {
    @Override
    public int compare (final PlayerPacket o1, final PlayerPacket o2)
    {
      if (o1.getTurnOrder () < o2.getTurnOrder ())
      {
        return -1;
      }
      else if (o1.getTurnOrder () > o2.getTurnOrder ())
      {
        return 1;
      }
      else
      {
        return 0;
      }
    }
  };

  PlayerColor getColor ();

  int getTurnOrder ();

  int getArmiesInHand ();

  int getCardsInHand ();

  boolean has (final PlayerColor color);

  boolean has (final int turnOrder);

  boolean hasArmiesInHand (final int armies);

  boolean hasCardsInHand (final int cards);

  boolean hasAtLeastNCardsInHand (final int cards);

  boolean hasAtMostNCardsInHand (final int cards);

  boolean doesNotHave (final PlayerColor color);

  boolean doesNotHave (final int turnOrder);
}
