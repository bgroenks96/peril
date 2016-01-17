package com.forerunnergames.peril.integration.core.func.init;

import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.INITIAL_GAME_PHASE_TEST_GROUP_NAME;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.TestUtil;
import com.forerunnergames.peril.integration.core.StateMachineTest;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class InitialGamePhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private final String sessionName;
  private DedicatedGameSession session;
  private StateMachineTest stateMachineTest;

  InitialGamePhaseTest (final String sessionName)
  {
    Arguments.checkIsNotNull (sessionName, "sessionName");

    this.sessionName = sessionName;
  }

  @BeforeClass (alwaysRun = true)
  public void initialize ()
  {
    session = (DedicatedGameSession) TestSessions.get (sessionName);
    stateMachineTest = new StateMachineTest (session.getStateMachine (), log);
  }

  /*
   * NOTE: Declared dependent method names in test annotations must be updated if any test method names
   * are refactored.
   */

  @Test (groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
  public void testAllClientsJoinServer ()
  {
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    controller.connectAllClientsToGameServer ();
    final TestClientPool clientPool = session.getTestClientPool ();
    clientPool.waitForAllClientsToReceive (JoinGameServerSuccessEvent.class);
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testAllClientsJoinServer", groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
  public void testAllClientsJoinGame ()
  {
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final TestClientPool clientPool = session.getTestClientPool ();
    final ClientEventCallback <PlayerJoinGameSuccessEvent> playerJoinGameCallback = new ClientEventCallback <PlayerJoinGameSuccessEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <PlayerJoinGameSuccessEvent> event, final TestClient client)
      {
        Arguments.checkIsNotNull (event, "event");
        Arguments.checkIsNotNull (client, "client");

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
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testAllClientsJoinGame", groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
  public void testDeterminePlayerTurnOrder ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final AtomicInteger verifyCount = new AtomicInteger ();
    final ClientEventCallback <DeterminePlayerTurnOrderCompleteEvent> determineTurnOrderCallback = new ClientEventCallback <DeterminePlayerTurnOrderCompleteEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <DeterminePlayerTurnOrderCompleteEvent> event,
                                   final TestClient client)
      {
        Arguments.checkIsNotNull (event, "event");
        Arguments.checkIsNotNull (client, "client");

        if (!event.isPresent ()) return;
        if (event.get ().getPlayersSortedByTurnOrder ().contains (client.getPlayer ())) verifyCount.incrementAndGet ();
      }
    };
    final ImmutableSet <TestClient> failed = clientPool
            .waitForAllClientsToReceive (DeterminePlayerTurnOrderCompleteEvent.class, determineTurnOrderCallback);
    for (final TestClient client : failed)
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertTrue (failed.isEmpty ());
    assertEquals (verifyCount.get (), clientPool.count ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testDeterminePlayerTurnOrder", groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
  public void testDistributeInitialArmies ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final AtomicInteger verifyCount = new AtomicInteger ();
    final ClientEventCallback <DistributeInitialArmiesCompleteEvent> callback = new ClientEventCallback <DistributeInitialArmiesCompleteEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <DistributeInitialArmiesCompleteEvent> event, final TestClient client)
      {
        Arguments.checkIsNotNull (event, "event");
        Arguments.checkIsNotNull (client, "client");

        if (!event.isPresent ()) return;
        final ImmutableSet <PlayerPacket> players = event.get ().getPlayers ();
        if (players.contains (client.getPlayer ())) verifyCount.incrementAndGet ();
        // update stored PlayerPacket
        for (final PlayerPacket player : players)
        {
          if (player.equals (client.getPlayer ())) client.setPlayer (player);
        }
      }
    };
    final ImmutableSet <TestClient> failed = clientPool
            .waitForAllClientsToReceive (DistributeInitialArmiesCompleteEvent.class, callback);
    for (final TestClient client : failed)
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertTrue (failed.isEmpty ());
    assertEquals (verifyCount.get (), clientPool.count ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testDistributeInitialArmies", groups = { MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME })
  public void testManualCountryAssignmentNoServerResponseToIllegalClientRequest ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    Optional <TestClient> clientNotInTurn = Optional.absent ();
    for (final TestClient client : clientPool)
    {
      if (client.getPlayer ().getTurnOrder () != PlayerTurnOrder.FIRST.asInt ())
      {
        clientNotInTurn = Optional.of (client);
        break;
      }
    }
    assertTrue (clientNotInTurn.isPresent ());
    final TestClient client = clientNotInTurn.get ();
    client.sendEvent (new PlayerClaimCountryResponseRequestEvent (""));
    client.assertNoEventsReceived (5000); // wait for five seconds
  }

  @Test (dependsOnMethods = "testDistributeInitialArmies", groups = { MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME })
  public void testManualCountryAssignmentReceivesDeniedEventOnInvalidRequest ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final ImmutableSortedSet <TestClient> sortedClients = TestUtil.sortClientsByPlayerTurnOrder (clientPool);
    final TestClient firstClient = sortedClients.first ();
    final Optional <PlayerClaimCountryRequestEvent> requestEvent = firstClient
            .waitForEventCommunication (PlayerClaimCountryRequestEvent.class);
    assertTrue (requestEvent.isPresent ());
    firstClient.sendEvent (new PlayerClaimCountryResponseRequestEvent ("not_a_country"));
    final Optional <PlayerClaimCountryResponseDeniedEvent> deniedEvent = firstClient
            .waitForEventCommunication (PlayerClaimCountryResponseDeniedEvent.class);
    assertTrue (deniedEvent.isPresent ());
    assertEquals (PlayerClaimCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST, deniedEvent.get ().getReason ());
  }

  @Test (dependsOnMethods = { "testManualCountryAssignmentReceivesDeniedEventOnInvalidRequest",
                              "testManualCountryAssignmentNoServerResponseToIllegalClientRequest" },
         groups = { MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME })
  public void testManualCountryAssignmentForAllClientsInOrder ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final ImmutableSortedSet <TestClient> sortedClients = TestUtil.sortClientsByPlayerTurnOrder (clientPool);
    int remainingCountryCount = Integer.MAX_VALUE;
    while (remainingCountryCount > 0)
    {
      for (final TestClient client : sortedClients)
      {
        final Optional <PlayerClaimCountryRequestEvent> event;
        event = client.waitForEventCommunication (PlayerClaimCountryRequestEvent.class);
        assertTrue (event.isPresent ());
        final ImmutableSet <CountryPacket> availableCountries = event.get ().getUnclaimedCountries ();
        assertFalse (availableCountries.isEmpty ());
        final CountryPacket someCountry = Randomness.getRandomElementFrom (availableCountries);
        client.sendEvent (new PlayerClaimCountryResponseRequestEvent (someCountry.getName ()));
        assertTrue (client.waitForEventCommunication (PlayerClaimCountryResponseSuccessEvent.class).isPresent ());
        remainingCountryCount = availableCountries.size () - 1;
      }
    }
    verifyPlayerCountryAssignmentComplete ();
  }

  @Test (dependsOnMethods = "testDistributeInitialArmies", groups = { RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME })
  public void testRandomCountryAssignment ()
  {
    verifyPlayerCountryAssignmentComplete ();
  }

  private void verifyPlayerCountryAssignmentComplete ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final AtomicInteger verifyCount = new AtomicInteger ();
    final ClientEventCallback <PlayerCountryAssignmentCompleteEvent> callback = new ClientEventCallback <PlayerCountryAssignmentCompleteEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <PlayerCountryAssignmentCompleteEvent> eventWrapper,
                                   final TestClient client)
      {
        Arguments.checkIsNotNull (eventWrapper, "eventWrapper");
        Arguments.checkIsNotNull (client, "client");

        if (!eventWrapper.isPresent ()) return;
        final PlayerCountryAssignmentCompleteEvent event = eventWrapper.get ();
        final ImmutableSet <PlayerPacket> players = event.getPlayers ();
        if (players.contains (client.getPlayer ())) verifyCount.incrementAndGet ();
        final ImmutableSet.Builder <CountryPacket> playerCountries = ImmutableSet.builder ();
        for (final CountryPacket country : event.getCountries ())
        {
          if (event.getOwner (country).is (client.getPlayer ())) playerCountries.add (country);
        }
        log.debug ("Player [{}] assigned countries [{}].", client.getPlayer (), playerCountries.build ());
      }
    };
    final ImmutableSet <TestClient> failed = clientPool
            .waitForAllClientsToReceive (PlayerCountryAssignmentCompleteEvent.class, callback);
    for (final TestClient client : failed)
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertTrue (failed.isEmpty ());
    assertEquals (verifyCount.get (), clientPool.count ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }
}
