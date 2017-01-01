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

import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent.Reason;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class SpectatorJoinGameDeniedEvent extends AbstractDeniedEvent <SpectatorJoinGameRequestEvent, Reason>
{
  private final String spectatorName;
  private final int spectatorLimit;

  public enum Reason
  {
    GAME_IS_FULL,
    SPECTATING_DISABLED,
    INVALID_NAME,
    DUPLICATE_PLAYER_NAME,
    DUPLICATE_SPECTATOR_NAME
  }

  public SpectatorJoinGameDeniedEvent (final String deniedName,
                                       final int spectatorLimit,
                                       final SpectatorJoinGameRequestEvent deniedRequest,
                                       final Reason reason)
  {
    super (deniedRequest, reason);

    Arguments.checkIsNotNull (deniedName, "deniedName");
    Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");

    spectatorName = deniedName;
    this.spectatorLimit = spectatorLimit;
  }

  public String getSpectatorName ()
  {
    return spectatorName;
  }

  public int getSpectatorLimit ()
  {
    return spectatorLimit;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SpectatorName: [{}] | SpectatorLimit: [{}]", super.toString (), spectatorName,
                           spectatorLimit);
  }

  @RequiredForNetworkSerialization
  private SpectatorJoinGameDeniedEvent ()
  {
    spectatorName = null;
    spectatorLimit = 0;
  }
}
