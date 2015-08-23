package com.forerunnergames.peril.integration.core.func;

import static org.testng.Assert.fail;

import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class InitialGamePhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private static final int DEFAULT_TEST_TIMEOUT = 30000;
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

    session = (DedicatedGameSession) TestSessions.get (sessionName);
  }

  @Test (timeOut = DEFAULT_TEST_TIMEOUT)
  public void testAllClientsJoinServer ()
  {
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    controller.connectAllClientsToGameServer ();
    final TestClientPool clientPool = session.getTestClientPool ();
    clientPool.waitForAllClientsToReceive (JoinGameServerSuccessEvent.class);
  }

  @Test (dependsOnMethods = "testAllClientsJoinServer", timeOut = DEFAULT_TEST_TIMEOUT)
  public void testAllClientsJoinGame ()
  {
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
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

    controller.sendForAllClientsJoinGameRequest ();
    clientPool.waitForAllClientsToReceive (PlayerJoinGameSuccessEvent.class, playerJoinGameCallback);
  }
}
