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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerTradeInCardsResponseSuccessEvent extends AbstractPlayerArmiesChangedEvent
        implements PlayerResponseSuccessEvent
{
  private final CardSetPacket tradeIn;
  private final int tradeInBonus;
  private final int nextTradeInBonus;

  public PlayerTradeInCardsResponseSuccessEvent (final PlayerPacket player,
                                                 final CardSetPacket tradeIn,
                                                 final int tradeInBonus,
                                                 final int nextTradeInBonus)
  {
    super (player, tradeInBonus);

    Arguments.checkIsNotNull (tradeIn, "tradeIn");
    Arguments.checkIsNotNegative (tradeInBonus, "tradeInBonus");
    Arguments.checkIsNotNegative (nextTradeInBonus, "nextTradeInBonus");

    this.tradeIn = tradeIn;
    this.tradeInBonus = tradeInBonus;
    this.nextTradeInBonus = nextTradeInBonus;
  }

  public ImmutableSet <CardPacket> getTradeInCards ()
  {
    return tradeIn.getCards ();
  }

  public int getTradeInBonus ()
  {
    return tradeInBonus;
  }

  public int getNextTradeInBonus ()
  {
    return nextTradeInBonus;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | TradeIn: [{}] | TradeInBonus: [{}] | NextTradeInBonus: [{}]", super.toString (),
                           tradeIn, tradeInBonus, nextTradeInBonus);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsResponseSuccessEvent ()
  {
    tradeIn = null;
    tradeInBonus = 0;
    nextTradeInBonus = 0;
  }
}
