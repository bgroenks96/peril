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

package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.InformEvent;

/**
 * Represents an {@link InformEvent} for soliciting input from players.
 *
 * This is used for game states that are optional for the player. Thus, the player can choose to bypass that game state
 * by sending a "cancellation" request, or can choose to participate in that game state by sending an "action" request.
 *
 * It thus offers much greater flexibility than a {@link PlayerInputRequestEvent}, which allows only one type of
 * {@link ResponseRequestEvent} back from the client, and is used for non-optional game states.
 */
public interface PlayerInputInformEvent extends InformEvent, PlayerInputEvent
{
}
