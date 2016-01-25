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
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerTradeInCardsResponseSuccessEvent extends AbstractPlayerArmiesChangedEvent
        implements PlayerResponseSuccessEvent
{
  private final CardSetPacket tradeIn;
  private final int tradeInBonus;

  public PlayerTradeInCardsResponseSuccessEvent (final PlayerPacket player,
                                                 final CardSetPacket tradeIn,
                                                 final int tradeInBonus)
  {
    super (player, tradeInBonus);

    this.tradeIn = tradeIn;
    this.tradeInBonus = tradeInBonus;
  }

  public CardSetPacket getTradeIn ()
  {
    return tradeIn;
  }

  public int getTradeInBonus ()
  {
    return tradeInBonus;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | TradeIn: [{}] | TradeInBonus: [{}]", super.toString (), tradeIn, tradeInBonus);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsResponseSuccessEvent ()
  {
    tradeIn = null;
    tradeInBonus = 0;
  }
}
