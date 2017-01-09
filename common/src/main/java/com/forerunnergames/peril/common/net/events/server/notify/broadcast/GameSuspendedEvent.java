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

package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractGamePhaseNotificationEvent;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class GameSuspendedEvent extends AbstractGamePhaseNotificationEvent
{
  public enum Reason
  {
    PLAYER_UNAVAILABLE,
    REQUESTED_BY_HOST
  }

  private final Reason reason;

  public GameSuspendedEvent (final Reason reason)
  {
    super (GamePhase.SUSPENDED);

    this.reason = reason;
  }

  public Reason getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Reason: {}", super.toString (), reason);
  }

  @RequiredForNetworkSerialization
  private GameSuspendedEvent ()
  {
    reason = null;
  }
}
