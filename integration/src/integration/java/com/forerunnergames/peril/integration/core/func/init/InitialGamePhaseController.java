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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.common.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.integration.TestMonitor;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.core.func.TestPhaseController;
import com.forerunnergames.peril.integration.core.func.WaitForCommunicationActionResult;
import com.forerunnergames.peril.integration.server.ClientEventProcessor;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InitialGamePhaseController implements TestPhaseController
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseController.class);
  private static final String PLAYER_NAME_BASE = "TestPlayer";
  private final DedicatedGameSession session;

  public InitialGamePhaseController (final DedicatedGameSession session)
  {
    this.session = session;
  }

  @Override
  public void fastForwardGameState ()
  {
    assertTrue (connectAllClientsToGameServer ());
    assertFalse (waitForAllClientsToJoinGame ().hasAnyFailed ());
    assertFalse (waitForAllClientsToReceivePlayerTurnOrder ().hasAnyFailed ());
    assertFalse (waitForAllClientsToReceiveInitialArmies ().hasAnyFailed ());
    assertFalse (waitForAllClientsToReceiveCountryAssignment ().hasAnyFailed ());
    performRandomInitialArmyPlacement ();
  }

  /**
   * @return true if all clients connected and joined successfully
   */
  public boolean connectAllClientsToGameServer ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    log.trace ("Waiting for clients to connect...");
    clientPool.waitForAllClients ();
    session.getTestClientPool ().sendAll (new JoinGameServerRequestEvent ());
    log.trace ("Waiting for clients to be accepted into game server...");
    return clientPool.waitForAllClientsToReceive (JoinGameServerSuccessEvent.class).isEmpty ();
  }

  public WaitForCommunicationActionResult waitForAllClientsToJoinGame ()
  {
    sendForAllClientsJoinGameRequest ();
    final TestClientPool clientPool = session.getTestClientPool ();
    final AtomicInteger verifyCount = new AtomicInteger ();
    final ClientEventCallback <PlayerJoinGameSuccessEvent> playerJoinGameCallback = new ClientEventCallback <PlayerJoinGameSuccessEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <PlayerJoinGameSuccessEvent> event, final TestClient client)
      {
        Arguments.checkIsNotNull (event, "event");
        Arguments.checkIsNotNull (client, "client");

        if (!event.isPresent ()) return;
        final PlayerPacket player = event.get ().getPlayer ();
        final String expectedName = Strings.format ("{}{}", PLAYER_NAME_BASE, client.getClientId ());
        if (player.getName ().equals (expectedName)) verifyCount.getAndIncrement ();
        client.setPlayer (event.get ().getPlayer ());
      }
    };
    final ImmutableSet <TestClient> failed = clientPool.waitForAllClientsToReceive (PlayerJoinGameSuccessEvent.class,
                                                                                    playerJoinGameCallback);
    return new WaitForCommunicationActionResult (failed, verifyCount.get ());
  }

  public WaitForCommunicationActionResult waitForAllClientsToReceivePlayerTurnOrder ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final AtomicInteger verifyCount = new AtomicInteger ();
    final ClientEventCallback <DeterminePlayerTurnOrderCompleteEvent> determineTurnOrderCallback = new ClientEventCallback <DeterminePlayerTurnOrderCompleteEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <DeterminePlayerTurnOrderCompleteEvent> event, final TestClient client)
      {
        Arguments.checkIsNotNull (event, "event");
        Arguments.checkIsNotNull (client, "client");

        if (!event.isPresent ()) return;
        if (event.get ().getPlayersSortedByTurnOrder ().contains (client.getPlayer ())) verifyCount.incrementAndGet ();
      }
    };
    final ImmutableSet <TestClient> failed = clientPool
            .waitForAllClientsToReceive (DeterminePlayerTurnOrderCompleteEvent.class, determineTurnOrderCallback);
    return new WaitForCommunicationActionResult (failed, verifyCount.get ());
  }

  public WaitForCommunicationActionResult waitForAllClientsToReceiveInitialArmies ()
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
    return new WaitForCommunicationActionResult (failed, verifyCount.get ());
  }

  public WaitForCommunicationActionResult waitForAllClientsToReceiveCountryAssignment ()
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
    return new WaitForCommunicationActionResult (failed, verifyCount.get ());
  }

  /**
   * Randomly places armies on behalf of each connected client. This performs in-line assertions checks for
   * client/server communication and will throw an AssertionError upon failure.
   */
  public void performRandomInitialArmyPlacement ()
  {
    final TestMonitor monitor = new TestMonitor (1);
    final TestClientPool clientPool = session.getTestClientPool ();

    clientPool.waitForAllClientsToReceive (BeginInitialReinforcementPhaseEvent.class);

    final ClientEventProcessor processor = new ClientEventProcessor (clientPool);

    final ClientEventCallback <PlayerBeginReinforcementEvent> requestCallback;
    requestCallback = new ClientEventCallback <PlayerBeginReinforcementEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <PlayerBeginReinforcementEvent> event, final TestClient client)
      {
        monitor.assertTrue (event.isPresent ());
        final ImmutableSet <CountryPacket> availableCountries = event.get ().getPlayerOwnedCountries ();
        monitor.assertFalse (availableCountries.isEmpty ());
        final CountryPacket someCountry = Randomness.getRandomElementFrom (availableCountries);
        client.sendEvent (new PlayerReinforceCountryRequestEvent (someCountry.getName (), 1));
        final Optional <PlayerReinforceCountrySuccessEvent> chkEvent2;
        chkEvent2 = client.waitForEventCommunication (PlayerReinforceCountrySuccessEvent.class);
        monitor.assertTrue (chkEvent2.isPresent ());
        final PlayerReinforceCountrySuccessEvent event2 = chkEvent2.get ();
        monitor.assertEquals (someCountry.getName (), event2.getCountryName ());
      }
    };

    processor.registerCallback (PlayerBeginReinforcementEvent.class, requestCallback);

    processor.start (EndInitialReinforcementPhaseEvent.class, monitor);

    monitor.awaitCompletion ();
  }

  private void sendForAllClientsJoinGameRequest ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    for (int i = 0; i < clientPool.count (); i++)
    {
      final TestClient client = clientPool.get (i);
      final String playerName = Strings.format ("{}{}", PLAYER_NAME_BASE, client.getClientId ());
      clientPool.send (i, new PlayerJoinGameRequestEvent (playerName));
    }
  }
}
