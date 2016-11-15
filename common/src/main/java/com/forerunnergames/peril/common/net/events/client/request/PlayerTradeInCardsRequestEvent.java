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

package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerTradeInCardsRequestEvent implements PlayerRequestEvent
{
  private final CardSetPacket tradeIn;

  public PlayerTradeInCardsRequestEvent (final CardSetPacket tradeIn)
  {
    Arguments.checkIsNotNull (tradeIn, "tradeIn");

    this.tradeIn = tradeIn;
  }

  public CardSetPacket getTradeIn ()
  {
    return tradeIn;
  }

  public int getTradeInCardCount ()
  {
    return tradeIn.getCardCount ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: TradeIn: [{}]", getClass ().getSimpleName (), tradeIn);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsRequestEvent ()
  {
    tradeIn = null;
  }
}
