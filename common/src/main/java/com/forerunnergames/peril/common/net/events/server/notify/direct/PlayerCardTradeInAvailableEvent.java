/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputInformEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public class PlayerCardTradeInAvailableEvent extends AbstractPlayerEvent implements PlayerInputInformEvent
{
  private final int nextTradeInBonus;
  private final ImmutableSet <CardSetPacket> tradeInMatches;
  private final boolean tradeInRequired;

  public PlayerCardTradeInAvailableEvent (final PlayerPacket player,
                                          final int nextTradeInBonus,
                                          final ImmutableSet <CardSetPacket> tradeInMatches,
                                          final boolean tradeInRequired)
  {
    super (player);

    Arguments.checkIsNotNegative (nextTradeInBonus, "nextTradeInBonus");
    Arguments.checkIsNotNull (tradeInMatches, "tradeInMatches");
    Arguments.checkHasNoNullElements (tradeInMatches, "tradeInMatches");

    this.nextTradeInBonus = nextTradeInBonus;
    this.tradeInMatches = tradeInMatches;
    this.tradeInRequired = tradeInRequired;
  }

  public int getNextTradeInBonus ()
  {
    return nextTradeInBonus;
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
    return Strings.format ("{} | NextTradeInBonus: {} | TradeInMatches: [{}] | TradeInRequired: {}", super.toString (),
                           nextTradeInBonus, tradeInMatches, tradeInRequired);
  }

  @RequiredForNetworkSerialization
  private PlayerCardTradeInAvailableEvent ()
  {
    nextTradeInBonus = 0;
    tradeInMatches = null;
    tradeInRequired = false;
  }
}
