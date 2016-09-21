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

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

public abstract class AbstractRoundEvent implements BroadcastNotificationEvent
{
  private final int round;

  public AbstractRoundEvent (final int round)
  {
    Arguments.checkIsNotNegative (round, "round");

    this.round = round;
  }

  @RequiredForNetworkSerialization
  protected AbstractRoundEvent ()
  {
    round = 0;
  }

  public final int getRound ()
  {
    return round;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Round: [{}]", getClass ().getSimpleName (), round);
  }
}
