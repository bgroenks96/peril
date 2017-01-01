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

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectFortifyVectorDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class PlayerSelectFortifyVectorDeniedEvent
        extends AbstractPlayerDeniedEvent <PlayerSelectFortifyVectorRequestEvent, Reason>
{
  public enum Reason
  {
    SOURCE_COUNTRY_DOES_NOT_EXIST,
    TARGET_COUNTRY_DOES_NOT_EXIST,
    COUNTRIES_NOT_ADJACENT,
    NOT_OWNER_OF_SOURCE_COUNTRY,
    NOT_OWNER_OF_TARGET_COUNTRY,
    SOURCE_COUNTRY_ARMY_UNDERFLOW,
    TARGET_COUNTRY_ARMY_OVERFLOW,
    PLAYER_NOT_IN_TURN
  }

  public PlayerSelectFortifyVectorDeniedEvent (final PlayerPacket player,
                                               final PlayerSelectFortifyVectorRequestEvent deniedRequest,
                                               final Reason reason)
  {
    super (player, deniedRequest, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectFortifyVectorDeniedEvent ()
  {
  }
}
