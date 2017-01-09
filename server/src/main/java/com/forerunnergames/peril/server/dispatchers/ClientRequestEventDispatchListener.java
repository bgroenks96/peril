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

package com.forerunnergames.peril.server.dispatchers;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerAnswerEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerOriginatedRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerQuitGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

public interface ClientRequestEventDispatchListener
{
  void handleEvent (final HumanJoinGameServerRequestEvent event, final RemoteClient client);

  void handleEvent (final AiJoinGameServerRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerJoinGameRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerQuitGameRequestEvent event, final RemoteClient client);

  void handleEvent (final SpectatorJoinGameRequestEvent event, final RemoteClient client);

  void handleEvent (final ChatMessageRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerOriginatedRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerAnswerEvent <?> event, final RemoteClient client);
}
