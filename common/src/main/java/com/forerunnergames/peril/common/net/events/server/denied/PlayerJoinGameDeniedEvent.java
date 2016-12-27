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

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerJoinGameDeniedEvent extends AbstractDeniedEvent <PlayerJoinGameDeniedEvent.Reason>
{
  private final String playerName;

  public enum Reason
  {
    GAME_IS_FULL,
    DUPLICATE_NAME,
    DUPLICATE_COLOR,
    DUPLICATE_TURN_ORDER,
    INVALID_NAME,
    INVALID_ADDRESS,
    INVALID_ID,
    NAME_MISMATCH
  }

  public PlayerJoinGameDeniedEvent (final String playerName, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (playerName, "playerName");

    this.playerName = playerName;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | PlayerName: {}", super.toString (), playerName);
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameDeniedEvent ()
  {
    playerName = null;
  }
}
