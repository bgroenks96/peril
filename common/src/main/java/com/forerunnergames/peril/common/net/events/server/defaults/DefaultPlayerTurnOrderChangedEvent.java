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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayerTurnOrderChangedEvent extends AbstractPlayerEvent implements
        PlayerTurnOrderChangedEvent
{
  private final int oldTurnOrder;

  public DefaultPlayerTurnOrderChangedEvent (final PlayerPacket player, final int oldTurnOrder)
  {
    super (player);

    Arguments.checkIsNotNegative (oldTurnOrder, "oldTurnOrder");
    Preconditions.checkIsTrue (player.doesNotHave (oldTurnOrder), "Player: [{}] must not have old turn order: [{}].",
                               player, oldTurnOrder);

    this.oldTurnOrder = oldTurnOrder;
  }

  @Override
  public int getOldTurnOrder ()
  {
    return oldTurnOrder;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | OldTurnOrder: [{}]", super.toString (), oldTurnOrder);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerTurnOrderChangedEvent ()
  {
    oldTurnOrder = 0;
  }
}
