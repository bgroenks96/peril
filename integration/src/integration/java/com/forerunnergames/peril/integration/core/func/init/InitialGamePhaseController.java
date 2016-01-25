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

package com.forerunnergames.peril.integration.core.func.init;

import com.forerunnergames.peril.common.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.tools.common.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InitialGamePhaseController
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseController.class);
  private final DedicatedGameSession session;

  InitialGamePhaseController (final DedicatedGameSession session)
  {
    this.session = session;
  }

  void connectAllClientsToGameServer ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    log.trace ("Waiting for clients to connect...");
    clientPool.waitForAllClients ();
    session.getTestClientPool ().sendAll (new JoinGameServerRequestEvent ());
  }

  void sendForAllClientsJoinGameRequest ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    for (int i = 0; i < clientPool.count (); i++)
    {
      clientPool.send (i, new PlayerJoinGameRequestEvent (Strings.format ("TestPlayer{}", i)));
    }
  }
}
