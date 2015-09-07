package com.forerunnergames.peril.integration.core.func;

import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;

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
    session.getTestClientPool ().sendAll (new JoinGameServerRequestEvent (
            new DefaultServerConfiguration ("localhost", session.getServerPort ())));
  }

  void sendForAllClientsJoinGameRequest ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    for (int i = 0; i < clientPool.count (); i++)
    {
      clientPool.send (i, new PlayerJoinGameRequestEvent (Strings.format ("TestPlayer-{}", i)));
    }
  }
}
