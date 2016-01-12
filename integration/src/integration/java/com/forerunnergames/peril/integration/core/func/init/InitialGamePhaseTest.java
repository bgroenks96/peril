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

import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.INITIAL_GAME_PHASE_TEST_GROUP_NAME;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.TestUtil;
import com.forerunnergames.peril.integration.core.StateMachineTest;
import com.forerunnergames.peril.integration.core.func.ActionResult;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public final class InitialGamePhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private final String sessionName;
  private DedicatedGameSession session;
  private InitialGamePhaseController controller;
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
    controller = new InitialGamePhaseController (session);
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
    assertTrue (controller.connectAllClientsToGameServer ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testAllClientsJoinServer", groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
  public void testAllClientsJoinGame ()
  {
    final InitialGamePhaseController controller = new InitialGamePhaseController (session);
    controller.sendForAllClientsJoinGameRequest ();
    final ActionResult result = controller.waitForAllClientsToJoinGame ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testAllClientsJoinGame", groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
  public void testDeterminePlayerTurnOrder ()
  {
    final ActionResult result = controller.waitForAllClientsToReceivePlayerTurnOrder ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testDeterminePlayerTurnOrder", groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
  public void testDistributeInitialArmies ()
  {
    final ActionResult result = controller.waitForAllClientsToReceiveInitialArmies ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
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
    final ActionResult result = controller.waitForAllClientsToReceiveCountryAssignment ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testDistributeInitialArmies", groups = { RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME })
  public void testRandomCountryAssignment ()
  {
    final ActionResult result = controller.waitForAllClientsToReceiveCountryAssignment ();
    for (final TestClient client : result.failed ())
    {
      log.debug ("Event not received by [{}]", client);
    }
    assertFalse (result.hasAnyFailed ());
    assertEquals (result.verified (), session.getTestClientCount ());
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testRandomCountryAssignment", groups = { RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME })
  public void testInitialReinforcementPhase_RandomAssignmentMode ()
  {
    // this controller method performs assertions so we don't need to do anything
    controller.randomlyPlaceInitialReinforcements ();
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }

  @Test (dependsOnMethods = "testManualCountryAssignmentForAllClientsInOrder",
         groups = MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME)
  public void testInitialReinforcementPhase_ManualAssignmentMode ()
  {
    // this controller method performs assertions so we don't need to do anything
    controller.randomlyPlaceInitialReinforcements ();
    assertFalse (stateMachineTest.checkError ().isPresent ());
  }
}
