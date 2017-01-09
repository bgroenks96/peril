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

package com.forerunnergames.peril.ai.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class AiConnectionEvent implements LocalEvent
{
  private final String playerName;

  public AiConnectionEvent (final String playerName)
  {
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
    return Strings.format ("{}: PlayerName: [{}]", getClass ().getSimpleName (), playerName);
  }
}
