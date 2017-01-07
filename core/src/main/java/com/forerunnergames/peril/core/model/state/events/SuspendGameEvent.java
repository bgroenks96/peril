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

package com.forerunnergames.peril.core.model.state.events;

import com.forerunnergames.peril.common.net.events.server.notify.broadcast.GameSuspendedEvent.Reason;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class SuspendGameEvent implements StateEvent
{
  private final Reason reason;
  private final long timeoutMillis;

  public SuspendGameEvent (final Reason reason)
  {
    this (reason, 0);
  }

  public SuspendGameEvent (final Reason reason, final long timeoutMillis)
  {
    this.reason = reason;
    this.timeoutMillis = timeoutMillis;
  }

  public Reason getReason ()
  {
    return reason;
  }

  public boolean hasTimeout ()
  {
    return timeoutMillis > 0;
  }

  public long getTimeoutMillis ()
  {
    return timeoutMillis;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Reason: [{}] | TimeoutMillis: [{}]", getClass ().getSimpleName (), reason,
                           hasTimeout () ? timeoutMillis : "None");
  }

  @RequiredForNetworkSerialization
  private SuspendGameEvent ()
  {
    reason = null;
    timeoutMillis = 0;
  }
}
