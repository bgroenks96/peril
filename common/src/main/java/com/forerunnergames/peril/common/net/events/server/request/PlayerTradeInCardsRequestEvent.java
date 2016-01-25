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

package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerTradeInCardsRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final int nextCardTradeInBonus;
  private final ImmutableSet <CardSetPacket> tradeInMatches;
  private final boolean tradeInRequired;

  public PlayerTradeInCardsRequestEvent (final PlayerPacket player,
                                         final int nextCardTradeInBonus,
                                         final ImmutableSet <CardSetPacket> tradeInMatches,
                                         final boolean tradeInRequired)
  {
    super (player);

    Arguments.checkIsNotNegative (nextCardTradeInBonus, "nextCardTradeInBonus");
    Arguments.checkIsNotNull (tradeInMatches, "tradeInMatches");
    Arguments.checkHasNoNullElements (tradeInMatches, "tradeInMatches");

    this.nextCardTradeInBonus = nextCardTradeInBonus;
    this.tradeInMatches = tradeInMatches;
    this.tradeInRequired = tradeInRequired;
  }

  public int getNextCardTradeInBonus ()
  {
    return nextCardTradeInBonus;
  }

  public ImmutableSet <CardSetPacket> getMatches ()
  {
    return tradeInMatches;
  }

  public boolean isTradeInRequired ()
  {
    return tradeInRequired;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | NextCardTradeInBonus: {} | TradeInMatches: [{}] | TradeInRequired: {}",
                           super.toString (), nextCardTradeInBonus, tradeInMatches, tradeInRequired);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsRequestEvent ()
  {
    nextCardTradeInBonus = 0;
    tradeInMatches = null;
    tradeInRequired = false;
  }
}
