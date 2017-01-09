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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerChangeCountryDeniedEvent <T extends PlayerRequestEvent>
        extends AbstractPlayerDeniedEvent <T, Reason>
{
  public enum Reason
  {
    COUNTRY_ARMY_COUNT_OVERFLOW,
    COUNTRY_ARMY_COUNT_UNDERFLOW,
    DELTA_ARMY_COUNT_UNDERFLOW,
    DELTA_ARMY_COUNT_OVERFLOW,
    NOT_OWNER_OF_COUNTRY,
    COUNTRY_UNAVAILABLE,
    COUNTRY_DOES_NOT_EXIST,
    INSUFFICIENT_ARMIES_IN_HAND,
    INSUFFICIENT_REINFORCEMENTS_PLACED,
    REINFORCEMENT_NOT_ALLOWED,
    COUNTRY_ALREADY_OWNED,
    TRADE_IN_REQUIRED,
    COUNTRY_DISABLED
  }

  protected AbstractPlayerChangeCountryDeniedEvent (final PlayerPacket player,
                                                    final T deniedRequest,
                                                    final Reason reason)
  {
    super (player, deniedRequest, reason);
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerChangeCountryDeniedEvent ()
  {
  }
}
