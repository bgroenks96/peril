package com.forerunnergames.peril.integration.core.func;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.core.StateMachineTest;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class InitialGamePhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private static final int DEFAULT_TEST_TIMEOUT = 45000;
  private final String sessionName;
  private DedicatedGameSession session;
  private StateMachineTest stateMachineTest;

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
    stateMachineTest = new StateMachineTest (session.getStateMachine (), log);
  }

  @Test (timeOut = DEFAULT_TEST_TIMEOUT)
  public void testAllClientsJoinServer ()
  {
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    controller.connectAllClientsToGameServer ();
    final TestClientPool clientPool = session.getTestClientPool ();
    clientPool.waitForAllClientsToReceive (JoinGameServerSuccessEvent.class);
    assertFalse (stateMachineTest.checkError ().isPresent ());
    stateMachineTest.checkCurrentStateIs ("WaitForGameToBegin");
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
        if (!event.isPresent ()) return;
        client.setPlayer (event.get ().getPlayer ());
      }
    };

    controller.sendForAllClientsJoinGameRequest ();
    final ImmutableSet <TestClient> failed = clientPool.waitForAllClientsToReceive (PlayerJoinGameSuccessEvent.class,
                                                                                    playerJoinGameCallback);
    for (final TestClient client : failed)
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertTrue (failed.isEmpty ());
    stateMachineTest.entered ("PlayingGame").after ("WaitForGameToBegin");
  }

  @Test (dependsOnMethods = "testAllClientsJoinGame", timeOut = DEFAULT_TEST_TIMEOUT)
  public void testDeterminePlayerTurnOrder ()
  {
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final TestClientPool clientPool = session.getTestClientPool ();
    final AtomicInteger verifyCount = new AtomicInteger ();
    final ClientEventCallback <DeterminePlayerTurnOrderCompleteEvent> determineTurnOrderCallback = new ClientEventCallback <DeterminePlayerTurnOrderCompleteEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <DeterminePlayerTurnOrderCompleteEvent> event,
                                   final TestClient client)
      {
        if (!event.isPresent ()) return;
        if (event.get ().getOrderedPlayers ().contains (client.getPlayer ())) verifyCount.incrementAndGet ();
      }
    };
    controller.sendForAllClientsJoinGameRequest ();
    final ImmutableSet <TestClient> failed = clientPool
            .waitForAllClientsToReceive (DeterminePlayerTurnOrderCompleteEvent.class, determineTurnOrderCallback);
    for (final TestClient client : failed)
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertTrue (failed.isEmpty ());
    assertEquals (verifyCount.get (), clientPool.count ());
    // stateMachineTest.entered ("DeterminePlayerTurnOrder").after ("PlayingGame");
  }
}
