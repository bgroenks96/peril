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

package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerFortifyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerFortifyCountryDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerFortifyCountryDeniedEvent
        extends AbstractPlayerDeniedEvent <PlayerFortifyCountryRequestEvent, Reason>
{
  public enum Reason
  {
    FORTIFY_DELTA_ARMY_COUNT_OVERFLOW,
    FORTIFY_DELTA_ARMY_COUNT_UNDERFLOW,
    PLAYER_NOT_IN_TURN
  }

  public PlayerFortifyCountryDeniedEvent (final PlayerPacket player,
                                          final PlayerFortifyCountryRequestEvent deniedRequest,
                                          final Reason reason)
  {
    super (player, deniedRequest, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryDeniedEvent ()
  {
  }
}
