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

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

import javax.annotation.Nullable;

public interface CountryOwnerChangedEvent extends CountryEvent, BroadcastNotificationEvent
{
  boolean hasPreviousOwner ();

  @Nullable
  PlayerPacket getPreviousOwner ();

  /**
   * @return Empty string if previous owner does not exist, i.e., {@link #hasPreviousOwner()} would return false,
   *         otherwise the player name of the previous owner.
   */
  String getPreviousOwnerName ();

  /**
   * @return {@link PlayerColor#UNKNOWN} if previous owner does not exist, i.e., {@link #hasPreviousOwner()} would
   *         return false, otherwise the {@link PlayerColor} of the previous owner.
   */
  PlayerColor getPreviousOwnerColor ();

  boolean hasNewOwner ();

  @Nullable
  PlayerPacket getNewOwner ();

  /**
   * @return {@link PlayerColor#UNKNOWN} if new owner does not exist, i.e., {@link #hasNewOwner()} would return false,
   *         otherwise the {@link PlayerColor} of the new owner.
   */
  PlayerColor getNewOwnerColor ();

  /**
   * @return Empty string if new owner does not exist, i.e., {@link #hasNewOwner()} would return false, otherwise the
   *         player name of the new owner.
   */
  String getNewOwnerName ();
}
