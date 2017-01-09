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
import com.forerunnergames.tools.net.events.remote.origin.client.ClientEvent;

public final class AiCommunicationEvent implements LocalEvent
{
  private final ClientEvent message;
  private final String playerName;

  public AiCommunicationEvent (final ClientEvent message, final String playerName)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (playerName, "playerName");

    this.message = message;
    this.playerName = playerName;
  }

  public ClientEvent getMessage ()
  {
    return message;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Message: [{}] | PlayerName: [{}]", getClass ().getSimpleName (), message, playerName);
  }
}
