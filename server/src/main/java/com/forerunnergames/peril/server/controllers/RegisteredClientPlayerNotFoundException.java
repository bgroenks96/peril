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

package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

/**
 * Thrown to indicate that the player registered to a client no longer exists. This is an exceptional case (and likely
 * the result of a bug or state violation in core) so it needs to be handled by server appropriately.
 */
final class RegisteredClientPlayerNotFoundException extends Exception
{
  final String message;

  RegisteredClientPlayerNotFoundException (final String playerName, final RemoteClient client)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (client, "client");

    message = Strings.format ("Player [{}] not found for client [{}].", playerName, client);
  }
}