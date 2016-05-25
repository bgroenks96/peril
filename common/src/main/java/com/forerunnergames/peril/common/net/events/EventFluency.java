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

package com.forerunnergames.peril.common.net.events;

import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class EventFluency
{
  public static String playerNameFrom (final PlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static String playerNameFrom (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static String playerNameFrom (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static <T> T reasonFrom (final DeniedEvent <T> event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getReason ();
  }

  private EventFluency ()
  {
    Classes.instantiationNotAllowed ();
  }
}
