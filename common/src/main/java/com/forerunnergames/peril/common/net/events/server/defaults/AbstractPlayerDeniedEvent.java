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
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerDeniedEvent <T extends PlayerRequestEvent, R> extends AbstractPlayerEvent
        implements PlayerDeniedEvent <T, R>
{
  private final T deniedEvent;
  private final R reason;

  public AbstractPlayerDeniedEvent (final PlayerPacket player, final T deniedEvent, final R reason)
  {
    super (player);

    Arguments.checkIsNotNull (deniedEvent, "deniedEvent");
    Arguments.checkIsNotNull (reason, "reason");

    this.deniedEvent = deniedEvent;
    this.reason = reason;
  }

  @Override
  public T getDeniedRequest ()
  {
    return deniedEvent;
  }

  @Override
  public R getReason ()
  {
    return reason;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerDeniedEvent ()
  {
    deniedEvent = null;
    reason = null;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DeniedRequest: {} | Reason: {}", super.toString (), deniedEvent, reason);
  }
}
