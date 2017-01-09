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

package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

/**
 * All implementations of PlayerArmiesChangedEvent should be constructed with an updated PlayerPacket (accessible via
 * {@link #getPerson()} that contains the number of armies in the player's hand <b>after</b> the delta is applied.
 */
public interface PlayerArmiesChangedEvent extends PlayerEvent, BroadcastNotificationEvent
{
  int getPlayerDeltaArmyCount ();
}
