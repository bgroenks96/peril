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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractCountryStateChangeDeniedEvent extends AbstractDeniedEvent <Reason>
        implements PlayerDeniedEvent <Reason>
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
    REINFORCEMENT_NOT_ALLOWED,
    COUNTRY_ALREADY_OWNED,
    COUNTRY_DISABLED;
  }

  protected AbstractCountryStateChangeDeniedEvent (final Reason reason)
  {
    super (reason);
  }

  @RequiredForNetworkSerialization
  protected AbstractCountryStateChangeDeniedEvent ()
  {
  }
}
