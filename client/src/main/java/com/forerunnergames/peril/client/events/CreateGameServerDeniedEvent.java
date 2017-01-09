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

package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameServerDeniedEvent implements LocalEvent
{
  private final CreateGameServerRequestEvent requestEvent;
  private final String reason;

  public CreateGameServerDeniedEvent (final CreateGameServerRequestEvent requestEvent, final String reason)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");
    Arguments.checkIsNotNull (reason, "reason");

    this.requestEvent = requestEvent;
    this.reason = reason;
  }

  public String getReason ()
  {
    return reason;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return requestEvent.getGameServerConfiguration ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Original request: {} | Reason for Denial: {}", getClass ().getSimpleName (),
                           requestEvent, reason);
  }
}
