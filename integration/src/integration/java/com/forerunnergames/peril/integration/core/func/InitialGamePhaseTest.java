package com.forerunnergames.peril.integration.core.func;

import static org.testng.Assert.fail;

import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.integration.TestSessionProvider;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class InitialGamePhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);

  private final String sessionName;
  private DedicatedGameSession session;

  public InitialGamePhaseTest (final String sessionName)
  {
    Arguments.checkIsNotNull (sessionName, "sessionName");

    this.sessionName = sessionName;
  }

  @BeforeClass
  public void loadSession ()
  {
    log.trace ("Initializing {} with session {}.", getClass ().getSimpleName (), sessionName);

    session = (DedicatedGameSession) TestSessionProvider.get (sessionName);
  }

  @Test
  public void testAllClientsJoinGameServer ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    log.trace ("Waiting for clients to connect...");
    // clientPool.waitForAllClients ();
    session.getTestClientPool ().sendAll (new JoinGameServerRequestEvent (
            new DefaultServerConfiguration ("localhost", session.getServerPort ())));
    clientPool.waitForAllClientsToReceive (JoinGameServerSuccessEvent.class);
  }

  @Test (dependsOnMethods = { "testAllClientsJoinGameServer" })
  public void testAllClientsJoinAsPlayers ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final ClientEventCallback <PlayerJoinGameSuccessEvent> playerJoinGameCallback = new ClientEventCallback <PlayerJoinGameSuccessEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <PlayerJoinGameSuccessEvent> event, final TestClient client)
      {
        if (!event.isPresent ()) fail (Strings.format ("Event not received by {}.", client));
        client.setPlayer (event.get ().getPlayer ());
      }
    };
    for (int i = 0; i < clientPool.count (); i++)
    {
      clientPool.send (i, new PlayerJoinGameRequestEvent (Strings.format ("TestPlayer-{}", i)));
    }
    clientPool.waitForAllClientsToReceive (PlayerJoinGameSuccessEvent.class, playerJoinGameCallback);
  }
}
