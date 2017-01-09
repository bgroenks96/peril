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

package com.forerunnergames.peril.integration.core.func.init;

import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.INITIAL_GAME_PHASE_TEST_GROUP_NAME;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.integration.TestMonitor;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.TestSessions.TestSession;
import com.forerunnergames.peril.integration.TestUtil;
import com.forerunnergames.peril.integration.core.StateMachineMonitor;
import com.forerunnergames.peril.integration.core.func.ActionResult;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.peril.integration.server.TestServerApplicationFactory;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test (groups = "func")
public final class InitialGamePhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private static final String SINGLETON_PROVIDER = "SingletonDataProvider";
  private final Set <TestSession> sessions = Sets.newConcurrentHashSet ();

  @DataProvider (name = SINGLETON_PROVIDER)
  public Object[][] generateSessionName (final Method method)
  {
    final String fullMethodName = Strings.format ("{}_{}", getClass ().getSimpleName (), method.getName ());
    return new Object [] [] {
            { TestSessions.createUniqueNameFrom (fullMethodName), LoggerFactory.getLogger (fullMethodName) } };
  }

  @AfterClass
  public void tearDown ()
  {
    for (final TestSession session : sessions)
    {
      TestSessions.end (session);
    }

    sessions.clear ();
  }

  @Test (groups = INITIAL_GAME_PHASE_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testAllClientsJoinServer (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.RANDOM);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = INITIAL_GAME_PHASE_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testAllClientsJoinGame (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.RANDOM);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    final ActionResult result = controller.waitForAllClientsToJoinGame ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = INITIAL_GAME_PHASE_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testDeterminePlayerTurnOrder (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.RANDOM);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    final ActionResult result = controller.waitForAllClientsToReceivePlayerTurnOrder ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = INITIAL_GAME_PHASE_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testDistributeInitialArmies (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.RANDOM);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    final ActionResult result = controller.waitForAllClientsToReceiveInitialArmies ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testManualCountryAssignmentNoServerResponseToIllegalClientRequest (final String sessionName,
                                                                                 final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.MANUAL);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveInitialArmies ().hasAnyFailed ());
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
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testManualCountryAssignmentReceivesDeniedEventOnInvalidRequest (final String sessionName,
                                                                              final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.MANUAL);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveInitialArmies ().hasAnyFailed ());
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
    final Optional <PlayerClaimCountryRequestEvent> resentRequest = firstClient
            .waitForEventCommunication (PlayerClaimCountryRequestEvent.class);
    assertTrue (resentRequest.isPresent ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testManualCountryAssignmentForAllClientsInOrder (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.MANUAL);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveInitialArmies ().hasAnyFailed ());
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
    final ActionResult result = controller.waitForAllClientsToReceiveCountryAssignment ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testRandomCountryAssignment (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.RANDOM);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveInitialArmies ().hasAnyFailed ());
    final ActionResult result = controller.waitForAllClientsToReceiveCountryAssignment ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = INITIAL_GAME_PHASE_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testInitialReinforcementPhase (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.RANDOM);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveInitialArmies ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveCountryAssignment ().hasAnyFailed ());
    // this controller method performs assertions so we don't need to do anything
    controller.performRandomInitialArmyPlacement ();
    TestUtil.pause (100);
    assertTrue (stateMachineMonitor.entered ("TurnPhase").atLeastOnce ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (groups = INITIAL_GAME_PHASE_TEST_GROUP_NAME, dataProvider = SINGLETON_PROVIDER)
  public void testInitialReinforcementPhaseServerRequestTimeout (final String sessionName, final Logger log)
  {
    final DedicatedGameSession session = createNewTestSession (sessionName, InitialCountryAssignment.RANDOM);
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (session.getStateMachine (), log);
    final TestClientPool clientPool = session.getTestClientPool ();
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (controller.waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveInitialArmies ().hasAnyFailed ());
    assertFalse (controller.waitForAllClientsToReceiveCountryAssignment ().hasAnyFailed ());
    TestUtil.pause (TestServerApplicationFactory.TEST_SERVER_REQUEST_TIMEOUT_MS);
    final TestMonitor monitor = new TestMonitor (clientPool.count ());
    clientPool.waitForAllClientsToReceive (SkipPlayerTurnEvent.class, 5000,
                                           new ClientEventCallback <SkipPlayerTurnEvent> ()
                                           {
                                             @Override
                                             public void onEventReceived (final Optional <SkipPlayerTurnEvent> event,
                                                                          final TestClient client)
                                             {
                                               // make sure event was received and the first player was skipped
                                               if (event.isPresent () && event.get ().getPerson ().getTurnOrder () == 1)
                                               {
                                                 monitor.checkIn ();
                                               }
                                             }
                                           });
    monitor.awaitCompletion (5000);
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  private DedicatedGameSession createNewTestSession (final String sessionName,
                                                     final InitialCountryAssignment assignmentMode)
  {
    final GameRules rules = ClassicGameRules.builder ().maxHumanPlayers ().initialCountryAssignment (assignmentMode)
            .build ();

    final DedicatedGameSession testSession = new DedicatedGameSession (sessionName,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, rules);

    sessions.add (testSession);

    log.trace ("Initializing test session {}", sessionName);
    TestSessions.start (sessionName, testSession);

    return testSession;
  }
}
