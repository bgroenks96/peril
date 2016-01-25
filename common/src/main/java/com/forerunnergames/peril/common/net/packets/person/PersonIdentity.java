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

package com.forerunnergames.peril.common.net.packets.person;

// @formatter:off
/**
 * The server sets this for each client, so that the client can know whether a player is owned / controlled by the local
 * machine / user, or belongs to someone else (remote machine / user).
 *
 * It has many uses, such as being able to refer to a specific player as "you" instead of by name, or giving the user
 * extra permissions for their own player.
 *
 * Usually, it is initialized to {@code UNKNOWN}, until the server is able to determine which client is associated with
 * which person.
 */
// @formatter:on
public enum PersonIdentity
{
  SELF,
  NON_SELF,
  UNKNOWN
}
