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

package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerTradeInCardsResponseDeniedEvent extends AbstractPlayerDeniedEvent <Reason>
        implements PlayerResponseDeniedEvent <Reason>
{
  public enum Reason
  {
    TRADE_IN_NOT_ALLOWED,
    CARDS_NOT_IN_HAND,
    TOO_MANY_CARDS_IN_HAND,
    INVALID_CARD_SET
  }

  public PlayerTradeInCardsResponseDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (player, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsResponseDeniedEvent ()
  {
  }
}
