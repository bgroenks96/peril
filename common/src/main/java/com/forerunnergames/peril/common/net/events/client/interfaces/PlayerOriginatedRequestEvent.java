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

package com.forerunnergames.peril.common.net.events.client.interfaces;

import com.forerunnergames.tools.net.events.remote.origin.client.InformRequestEvent;

/**
 * Represents request events sent by a client to the server after successfully joining the game as a player. Should be
 * answered by a {@link com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent} or
 * {@link com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent}
 *
 * Note: This interface should NOT be used for any event implementing {@link InformRequestEvent} NOR
 * {@link com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent}, but only for unsolicited
 * requests from a player.
 */
public interface PlayerOriginatedRequestEvent extends PlayerRequestEvent
{
}
