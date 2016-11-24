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

package com.forerunnergames.peril.common.net.events.client.interfaces;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

/**
 * Represents request events sent by a client to the server after successfully joining the game as a player, that are
 * answers to {@link PlayerInformEvent}. They are also questions that should be answered by the server with a
 * {@link com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent} or
 * {@link com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent}
 *
 * @see PlayerInformEvent for a more detailed explanation.
 */
public interface InformRequestEvent extends ClientRequestEvent
{
  Class <? extends PlayerInformEvent> getInformType ();
}
