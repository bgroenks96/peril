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

package com.forerunnergames.peril.server.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.ai.events.AiDisconnectionEvent;
import com.forerunnergames.peril.ai.net.AiClient;
import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.eventbus.EventBusHandler;
import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerQuitGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerEndTurnAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerDisconnectEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerQuitGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.SpectatorJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultPlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.common.playmap.DefaultPlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.core.events.DefaultEventRegistry;
import com.forerunnergames.peril.core.events.EventRegistry;
import com.forerunnergames.peril.server.communicators.AiPlayerCommunicator;
import com.forerunnergames.peril.server.communicators.CoreCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultSpectatorCommunicator;
import com.forerunnergames.peril.server.communicators.HumanPlayerCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.communicators.SpectatorCommunicator;
import com.forerunnergames.peril.server.kryonet.KryonetRemoteClient;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.NetworkConstants;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientEvent;
import com.forerunnergames.tools.net.server.configuration.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;
import com.forerunnergames.tools.net.server.remote.RemoteClient;
import com.forerunnergames.tools.net.server.remote.RemoteClientCommunicator;
import com.forerunnergames.tools.net.server.remote.RemoteClientConnector;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.matthiasmann.AsyncExecution;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import net.engio.mbassy.bus.MBassador;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

public class MultiplayerControllerTest
{
  private static final String DEFAULT_TEST_GAME_SERVER_NAME = "test-server";
  private static final GameServerType DEFAULT_GAME_SERVER_TYPE = GameServerType.DEDICATED;
  private static final String DEFAULT_TEST_SERVER_ADDRESS = "server@test";
  private static final int DEFAULT_TEST_SERVER_PORT = 8888;
  private static final int DEFAULT_TEST_REQUEST_TIMEOUT = 5000;
  private final EventBusHandler eventHandler = new EventBusHandler ();
  private final RemoteClientConnector mockConnector = mock (RemoteClientConnector.class, Mockito.RETURNS_SMART_NULLS);
  private final RemoteClientCommunicator mockHumanClientCommunicator = mock (RemoteClientCommunicator.class,
                                                                             Mockito.RETURNS_SMART_NULLS);
  private final RemoteClientCommunicator mockAiClientCommunicator = mock (RemoteClientCommunicator.class,
                                                                          Mockito.RETURNS_SMART_NULLS);
  private final RemoteClientCommunicator mockSpectatorClientCommunicator = mock (RemoteClientCommunicator.class,
                                                                                 Mockito.RETURNS_SMART_NULLS);
  private final PlayerCommunicator humanPlayerCommunicator = new HumanPlayerCommunicator (mockHumanClientCommunicator);
  private final PlayerCommunicator aiPlayerCommunicator = new AiPlayerCommunicator (mockAiClientCommunicator);
  private final SpectatorCommunicator spectatorCommunicator = new DefaultSpectatorCommunicator (
          mockSpectatorClientCommunicator);
  private final CoreCommunicator mockCoreCommunicator = mock (CoreCommunicator.class, Mockito.RETURNS_SMART_NULLS);
  private final MultiplayerControllerBuilder mpcBuilder = builder (mockConnector, humanPlayerCommunicator,
                                                                   aiPlayerCommunicator, spectatorCommunicator,
                                                                   mockCoreCommunicator);
  private int clientCount = 0;
  private EventRegistry eventRegistry;
  private MBassador <Event> eventBus;

  // convenience method for fetching a new MultiplayerControllerBuilder
  // Note: package private visibility is intended; other test classes in package should have access.
  static MultiplayerControllerBuilder builder (final RemoteClientConnector connector,
                                               final PlayerCommunicator humanPlayerCommunicator,
                                               final PlayerCommunicator aiPlayerCommunicator,
                                               final SpectatorCommunicator spectatorCommunicator,
                                               final CoreCommunicator coreCommunicator)
  {
    Arguments.checkIsNotNull (connector, "connector");
    Arguments.checkIsNotNull (humanPlayerCommunicator, "humanPlayerCommunicator");
    Arguments.checkIsNotNull (aiPlayerCommunicator, "aiPlayerCommunicator");
    Arguments.checkIsNotNull (spectatorCommunicator, "spectatorCommunicator");
    Arguments.checkIsNotNull (coreCommunicator, "coreCommunicator");

    return new MultiplayerControllerBuilder (connector, humanPlayerCommunicator, aiPlayerCommunicator,
            spectatorCommunicator, coreCommunicator);
  }

  @Before
  public void setup ()
  {
    eventBus = EventBusFactory.create (ImmutableSet.of (EventBusHandler.createEventBusFailureHandler ()));
    eventRegistry = new DefaultEventRegistry (eventBus, new AsyncExecution ());
    eventHandler.subscribe (eventBus);
  }

  @After
  public void tearDown ()
  {
    eventHandler.unsubscribe (eventBus);
    eventBus.shutdown ();
  }

  @Test
  public void testSuccessfulHostClientJoinGameServer ()
  {
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventRegistry, eventBus);

    final RemoteClient host = createHumanHost ();
    connect (host);

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);

    verify (mockHumanClientCommunicator, only ())
            .sendTo (eq (host), argThat (new JoinGameServerSuccessEventMatcher (createDefaultServerConfig (), host)));
  }

  @Test
  public void testSuccessfulHumanClientJoinGameServer ()
  {
    mpcBuilder.build (eventRegistry, eventBus);

    final RemoteClient client = createHumanClient ();
    connect (client);

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), client);

    verify (mockHumanClientCommunicator)
            .sendTo (eq (client),
                     argThat (new JoinGameServerSuccessEventMatcher (createDefaultServerConfig (), client)));
  }

  @Test
  public void testSuccessfulAiClientJoinGameServer ()
  {
    mpcBuilder.aiPlayerLimit (1).build (eventRegistry, eventBus);

    final RemoteClient client = createAiClient (GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1"));
    connect (client);

    communicateEventFromClient (new AiJoinGameServerRequestEvent (), client);

    verify (mockAiClientCommunicator)
            .sendTo (eq (client),
                     argThat (new JoinGameServerSuccessEventMatcher (createDefaultServerConfig (), client)));
  }

  @Test
  public void testTwoHumanClientsJoinGameServerSimultaneously ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);

    final RemoteClient client1 = joinHumanClientToGameServer ();
    final RemoteClient client2 = joinHumanClientToGameServer ();

    final Set <PlayerPacket> players = Sets.newConcurrentHashSet ();
    final Function <RemoteClient, PlayerJoinGameSuccessEvent> clientJoinAsPlayer = new Function <RemoteClient, PlayerJoinGameSuccessEvent> ()
    {
      @Override
      public PlayerJoinGameSuccessEvent apply (final RemoteClient client)
      {
        final String playerName = "TestPlayer" + client.getConnectionId ();
        final PlayerPacket player = createMockHumanPlayer (playerName);
        final PlayerJoinGameRequestEvent requestEvent = new HumanPlayerJoinGameRequestEvent (playerName);
        communicateEventFromClient (requestEvent, client);
        assertTrue (eventHandler.wasFiredExactlyOnce (requestEvent));
        players.add (player);
        final PlayerJoinGameSuccessEvent successEvent = new PlayerJoinGameSuccessEvent (player,
                ImmutableSet.copyOf (players), mpc.getPersonLimits ());
        communicateEventFromCore (successEvent);
        verify (mockHumanClientCommunicator, times (players.size ()))
                .sendTo (any (RemoteClient.class),
                         argThat (new PlayerJoinGameSuccessEventMatcher (player, ImmutableSet.copyOf (players))));
        assertTrue (mpc.isPlayerInGame (player));

        return successEvent;
      }
    };

    final PlayerJoinGameSuccessEvent clientResult1 = clientJoinAsPlayer.apply (client1);
    assertNotNull (clientResult1);
    final PlayerJoinGameSuccessEvent clientResult2 = clientJoinAsPlayer.apply (client2);
    assertNotNull (clientResult2);
    assertTrue (clientResult1.getOtherPlayersInGame ().isEmpty ());
    assertEquals (ImmutableSet.of (clientResult1.getPerson ()), clientResult2.getOtherPlayersInGame ());
  }

  @Test
  public void testTwoAiClientsJoinGameServerSimultaneously ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (2).build (eventRegistry, eventBus);

    final RemoteClient client1 = joinAiClientToGameServer (GameSettings
            .getAiPlayerNameWithMandatoryClanTag ("TestPlayer1"));
    final RemoteClient client2 = joinAiClientToGameServer (GameSettings
            .getAiPlayerNameWithMandatoryClanTag ("TestPlayer2"));

    final Set <PlayerPacket> players = Sets.newConcurrentHashSet ();
    final Function <RemoteClient, PlayerJoinGameSuccessEvent> clientJoinAsPlayer = new Function <RemoteClient, PlayerJoinGameSuccessEvent> ()
    {
      @Override
      public PlayerJoinGameSuccessEvent apply (final RemoteClient client)
      {
        final String playerName = client.getAddress ();
        final PlayerPacket player = createMockAiPlayer (playerName);
        final PlayerJoinGameRequestEvent requestEvent = new AiPlayerJoinGameRequestEvent (playerName);
        communicateEventFromClient (requestEvent, client);
        assertTrue (eventHandler.wasFiredExactlyOnce (requestEvent));
        players.add (player);
        final PlayerJoinGameSuccessEvent successEvent = new PlayerJoinGameSuccessEvent (player,
                ImmutableSet.copyOf (players), mpc.getPersonLimits ());
        communicateEventFromCore (successEvent);
        verify (mockAiClientCommunicator, times (1))
                .sendTo (any (RemoteClient.class),
                         argThat (new PlayerJoinGameSuccessEventMatcher (player, ImmutableSet.copyOf (players))));
        assertTrue (mpc.isPlayerInGame (player));

        return successEvent;
      }
    };

    final PlayerJoinGameSuccessEvent clientResult1 = clientJoinAsPlayer.apply (client1);
    assertNotNull (clientResult1);
    final PlayerJoinGameSuccessEvent clientResult2 = clientJoinAsPlayer.apply (client2);
    assertNotNull (clientResult2);
    assertTrue (clientResult1.getOtherPlayersInGame ().isEmpty ());
    assertEquals (ImmutableSet.of (clientResult1.getPerson ()), clientResult2.getOtherPlayersInGame ());
  }

  @Test
  public void testHumanClientJoinRequestBeforeHostDenied ()
  {
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventRegistry, eventBus);

    final RemoteClient client = addHumanClient ();

    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client),
                                                          argThat (new JoinGameServerDeniedEventMatcher (client)));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testAiClientJoinRequestBeforeHostSuccessful ()
  {
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).aiPlayerLimit (1).build (eventRegistry, eventBus);

    final RemoteClient client = addAiClient (GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1"));

    verify (mockAiClientCommunicator)
            .sendTo (eq (client),
                     argThat (new JoinGameServerSuccessEventMatcher (createDefaultServerConfig (), client)));
  }

  @Test
  public void testHostClientJoinDedicatedGameServerDenied ()
  {
    mpcBuilder.gameServerType (GameServerType.DEDICATED).build (eventRegistry, eventBus);

    final RemoteClient host = createHumanHost ();
    connect (host);

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);

    verify (mockHumanClientCommunicator, only ()).sendTo (eq (host),
                                                          argThat (new JoinGameServerDeniedEventMatcher (host)));
  }

  @Test
  public void testNonHostClientJoinGameServerAfterHostSuccessful ()
  {
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventRegistry, eventBus);

    final RemoteClient host = createHumanHost ();
    connect (host);

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);
    verify (mockHumanClientCommunicator, only ())
            .sendTo (eq (host), argThat (new JoinGameServerSuccessEventMatcher (createDefaultServerConfig (), host)));

    final RemoteClient client = addHumanClient ();
    verify (mockHumanClientCommunicator).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));
  }

  @Test
  public void testHostClientJoinGameServerAfterHostDenied ()
  {
    // MPC still needs to be built in order to register the event bus
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventRegistry, eventBus);

    final RemoteClient host = createHumanHost ();
    connect (host);

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);
    verify (mockHumanClientCommunicator, only ())
            .sendTo (eq (host), argThat (new JoinGameServerSuccessEventMatcher (createDefaultServerConfig (), host)));

    final RemoteClient duplicateHost = createHumanHost ();
    connect (duplicateHost);
    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), duplicateHost);
    verify (mockHumanClientCommunicator).sendTo (eq (duplicateHost), isA (JoinGameServerDeniedEvent.class));
  }

  @Test
  public void testHumanClientJoinRequestDeniedBecauseInvalidIpEmpty ()
  {
    mpcBuilder.build (eventRegistry, eventBus);

    final RemoteClient client = addHumanClientWithAddress ("");

    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client),
                                                          argThat (new JoinGameServerDeniedEventMatcher (client)));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testClientJoinRequestDeniedBecauseMatchesServerIp ()
  {
    mpcBuilder.serverAddress ("1.2.3.4").build (eventRegistry, eventBus);

    final RemoteClient client = addHumanClientWithAddress ("1.2.3.4");

    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client),
                                                          argThat (new JoinGameServerDeniedEventMatcher (client)));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testValidHumanPlayerJoinGameRequestPublished ()
  {
    mpcBuilder.build (eventRegistry, eventBus);

    final RemoteClient client = addHumanClient ();
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "TestPlayer1";
    final ClientEvent event = new HumanPlayerJoinGameRequestEvent (playerName);
    communicateEventFromClient (event, client);
    assertLastEventWas (event);
  }

  @Test
  public void testValidAiPlayerJoinGameRequestPublished ()
  {
    mpcBuilder.aiPlayerLimit (1).build (eventRegistry, eventBus);

    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final RemoteClient client = addAiClient (playerName);
    verify (mockAiClientCommunicator, only ()).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    final ClientEvent event = new AiPlayerJoinGameRequestEvent (playerName);
    communicateEventFromClient (event, client);
    assertLastEventWas (event);
  }

  @Test
  public void testIgnoreHumanPlayerJoinGameRequestBeforeJoiningGameServer ()
  {
    mpcBuilder.build (eventRegistry, eventBus);

    // Connect client to server, but do not join client to game server.
    final RemoteClient client = createHumanClient ();
    connect (client);

    // Simulate bad request.
    final Event event = communicateEventFromClient (new HumanPlayerJoinGameRequestEvent ("TestPlayer1"), client);

    // Assert that no event was published after receiving bad request.
    assertLastEventWas (event);

    // Verify the controller did not send anything via the communicator.
    verifyNoMoreInteractions (mockHumanClientCommunicator);
  }

  @Test
  public void testIgnoreAiPlayerJoinGameRequestBeforeJoiningGameServer ()
  {
    mpcBuilder.aiPlayerLimit (1).build (eventRegistry, eventBus);

    // Connect client to server, but do not join client to game server.
    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final RemoteClient client = createAiClient (playerName);
    connect (client);

    // Simulate bad request.
    final Event event = communicateEventFromClient (new AiPlayerJoinGameRequestEvent (playerName), client);

    // Assert that no more events were published after receiving bad request and disconnection.
    assertSecondToLastEventWas (event);
    assertLastEventWasType (AiDisconnectionEvent.class);

    // Verify the controller did not send anything via the communicator.
    verifyNoMoreInteractions (mockAiClientCommunicator);
  }

  @Test
  public void testPlayerJoinGameSuccessForTwoHumanClients ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);
    final String playerName1 = "TestPlayer1";
    final String playerName2 = "TestPlayer2";

    final ClientPlayerTuple clientPlayer1 = addHumanClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer (playerName2, mpc);

    verify (mockHumanClientCommunicator)
            .sendTo (eq (clientPlayer1.client),
                     argThat (new PlayerJoinGameSuccessEventIdentityMatcher (PersonIdentity.NON_SELF)));

    verify (mockHumanClientCommunicator)
            .sendTo (eq (clientPlayer2.client),
                     argThat (new PlayerJoinGameSuccessEventIdentityMatcher (PersonIdentity.SELF)));

    verify (mockAiClientCommunicator)
            .sendToAllExcept (eq (clientPlayer2.client),
                              argThat (new PlayerJoinGameSuccessEventIdentityMatcher (PersonIdentity.NON_SELF)));

    verifyZeroInteractions (mockSpectatorClientCommunicator);

    assertTrue (mpc.isPlayerInGame (clientPlayer2.player));
  }

  @Test
  public void testPlayerJoinGameSuccessForTwoAiClients ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (2).build (eventRegistry, eventBus);
    final String playerName1 = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final String playerName2 = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer2");

    addAiClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addAiClientAndMockPlayerToGameServer (playerName2, mpc);

    verify (mockAiClientCommunicator)
            .sendToAllExcept (eq (clientPlayer2.client),
                              argThat (new PlayerJoinGameSuccessEventIdentityMatcher (PersonIdentity.NON_SELF)));

    verify (mockAiClientCommunicator)
            .sendTo (eq (clientPlayer2.client),
                     argThat (new PlayerJoinGameSuccessEventIdentityMatcher (PersonIdentity.SELF)));

    verifyZeroInteractions (mockHumanClientCommunicator);
    verifyZeroInteractions (mockSpectatorClientCommunicator);

    assertTrue (mpc.isPlayerInGame (clientPlayer2.player));
  }

  @Test
  public void testHumanPlayerJoinGameDenied ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);
    final RemoteClient client = joinHumanClientToGameServer ();

    final String playerName = "TestPlayer1";
    final PlayerJoinGameRequestEvent request = new HumanPlayerJoinGameRequestEvent (playerName);
    communicateEventFromClient (request, client);

    final PlayerPacket player = createMockHumanPlayer (playerName);
    // make up a reason... doesn't have to be true :)
    final PlayerJoinGameDeniedEvent.Reason reason = PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME;
    final Event deniedEvent = new PlayerJoinGameDeniedEvent (playerName, request, reason);
    communicateEventFromCore (deniedEvent);
    verify (mockHumanClientCommunicator).sendTo (eq (client), eq (deniedEvent));
    assertFalse (mpc.isPlayerInGame (player));
  }

  @Test
  public void testAiPlayerJoinGameDenied ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (1).build (eventRegistry, eventBus);
    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final RemoteClient client = joinAiClientToGameServer (playerName);

    final PlayerJoinGameRequestEvent request = new AiPlayerJoinGameRequestEvent (playerName);
    communicateEventFromClient (request, client);

    final PlayerPacket player = createMockAiPlayer (playerName);
    // make up a reason... doesn't have to be true :)
    final PlayerJoinGameDeniedEvent.Reason reason = PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME;
    final Event deniedEvent = new PlayerJoinGameDeniedEvent (playerName, request, reason);
    communicateEventFromCore (deniedEvent);
    verify (mockAiClientCommunicator).sendTo (eq (client), eq (deniedEvent));
    assertFalse (mpc.isPlayerInGame (player));
  }

  @Test
  public void testHumanPlayerDisconnected ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);
    final ClientPlayerTuple clientPlayer1 = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer ("TestPlayer2", mpc);

    disconnect (clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client ()), isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer1.client ()),
                                                           isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (any (RemoteClient.class),
                                                           isA (PlayerQuitGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));
  }

  @Test
  public void testHumanPlayerRejoinSucceedsWithValidId ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);
    final String playerName1 = "TestPlayer1";
    final String playerName2 = "TestPlayer2";
    final ClientPlayerTuple clientPlayer1 = addHumanClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer (playerName2, mpc);

    reset (mockHumanClientCommunicator);

    final Optional <UUID> maybe = mpc.getPlayerServerId (clientPlayer1.player ());
    assertTrue (maybe.isPresent ());
    final UUID secretIdPlayer1 = maybe.get ();

    disconnect (clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client ()), isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer1.client ()),
                                                           isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (any (RemoteClient.class),
                                                           isA (PlayerQuitGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer1.client ()), isA (JoinGameServerSuccessEvent.class));

    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName1, secretIdPlayer1),
                                clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer1.client ()), isA (PlayerJoinGameSuccessEvent.class));
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client ()), isA (PlayerJoinGameSuccessEvent.class));

    assertTrue (mpc.isPlayerInGame (clientPlayer1.player ()));
  }

  @Test
  public void testHumanPlayerRejoinFailsWithInvalidId ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);
    final String playerName1 = "TestPlayer1";
    final String playerName2 = "TestPlayer2";
    final ClientPlayerTuple clientPlayer1 = addHumanClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer (playerName2, mpc);

    reset (mockHumanClientCommunicator);

    disconnect (clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client ()), isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer1.client ()),
                                                           isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (any (RemoteClient.class),
                                                           isA (PlayerQuitGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer1.client ()), isA (JoinGameServerSuccessEvent.class));

    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName1, UUID.randomUUID ()),
                                clientPlayer1.client ());

    final PlayerJoinGameDeniedEventMatcher deniedEventMatcher = new PlayerJoinGameDeniedEventMatcher (playerName1,
            PlayerJoinGameDeniedEvent.Reason.INVALID_ID);
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer1.client ()), argThat (deniedEventMatcher));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer1.client ()),
                                                           isA (PlayerJoinGameSuccessEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer2.client ()),
                                                           isA (PlayerJoinGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));
  }

  @Test
  public void testHumanPlayerRejoinFailsWithNameMismatch ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);
    final String playerName1 = "TestPlayer1";
    final String playerName2 = "TestPlayer2";
    final String playerReconnectName = "SomeOtherName";
    final ClientPlayerTuple clientPlayer1 = addHumanClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer (playerName2, mpc);

    reset (mockHumanClientCommunicator);

    final Optional <UUID> maybe = mpc.getPlayerServerId (clientPlayer1.player ());
    assertTrue (maybe.isPresent ());
    final UUID secretIdPlayer1 = maybe.get ();

    disconnect (clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client ()), isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer1.client ()),
                                                           isA (PlayerDisconnectEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (any (RemoteClient.class),
                                                           isA (PlayerQuitGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), clientPlayer1.client ());

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer1.client ()), isA (JoinGameServerSuccessEvent.class));

    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerReconnectName, secretIdPlayer1),
                                clientPlayer1.client ());

    final PlayerJoinGameDeniedEventMatcher deniedEventMatcher = new PlayerJoinGameDeniedEventMatcher (
            playerReconnectName, PlayerJoinGameDeniedEvent.Reason.NAME_MISMATCH);
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer1.client ()), argThat (deniedEventMatcher));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer1.client ()),
                                                           isA (PlayerJoinGameSuccessEvent.class));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer2.client ()),
                                                           isA (PlayerJoinGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));
  }

  @Test
  public void testAiPlayerQuitGame ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (1).build (eventRegistry, eventBus);
    final String playerName1 = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final String playerName2 = "TestPlayer2";
    final ClientPlayerTuple clientPlayer1 = addAiClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer (playerName2, mpc);

    communicateEventFromClient (new PlayerQuitGameRequestEvent (), clientPlayer1.client ());

    verify (mockAiClientCommunicator).sendToAll (isA (PlayerQuitGameSuccessEvent.class));
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client ()), isA (PlayerQuitGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));
  }

  @Test
  public void testHumanPlayerQuitGame ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (1).build (eventRegistry, eventBus);
    final String playerName1 = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final String playerName2 = "TestPlayer2";
    final ClientPlayerTuple clientPlayer1 = addAiClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer (playerName2, mpc);

    communicateEventFromClient (new PlayerQuitGameRequestEvent (), clientPlayer1.client ());

    verify (mockAiClientCommunicator).sendToAll (isA (PlayerQuitGameSuccessEvent.class));
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client ()), isA (PlayerQuitGameSuccessEvent.class));

    assertFalse (mpc.isPlayerInGame (clientPlayer1.player ()));
  }

  @Test
  public void testSpectatorJoinGameSuccess ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (ClassicGameRules.MAX_SPECTATOR_LIMIT)
            .build (eventRegistry, eventBus);
    final ClientPlayerTuple clientPlayer = addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);
    final RemoteClient spectatorClient = addHumanClient ();
    addMockSpectatorToGameWithName ("TestSpectator", spectatorClient, mpc);
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer.client ()), isA (PlayerJoinGameSuccessEvent.class));
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer.client ()),
                                                 isA (SpectatorJoinGameSuccessEvent.class));
    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient), isA (SpectatorJoinGameSuccessEvent.class));
  }

  @Test
  public void testSpectatorJoinGameDeniedDuplicatesPlayerName ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (ClassicGameRules.MAX_SPECTATOR_LIMIT)
            .build (eventRegistry, eventBus);
    final ClientPlayerTuple clientPlayer = addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);
    final RemoteClient spectatorClient = addHumanClient ();

    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestPlayer"), spectatorClient);
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer.client ()), isA (PlayerJoinGameSuccessEvent.class));

    final ArgumentMatcher <SpectatorJoinGameDeniedEvent> matcher = new SpectatorJoinGameDeniedEventMatcher (
            SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_PLAYER_NAME);
    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient), argThat (matcher));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer.client ()), argThat (matcher));
  }

  @Test
  public void testSpectatorJoinGameDeniedDuplicatesSpectatorName ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (ClassicGameRules.MAX_SPECTATOR_LIMIT)
            .build (eventRegistry, eventBus);
    addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);

    final RemoteClient spectatorClient1 = addHumanClient ();
    addMockSpectatorToGameWithName ("TestSpectator", spectatorClient1, mpc);

    final RemoteClient spectatorClient2 = addHumanClient ();
    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestSpectator"), spectatorClient2);

    verify (mockSpectatorClientCommunicator)
            .sendTo (eq (spectatorClient2), argThat (new SpectatorJoinGameDeniedEventMatcher (
                    SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_SPECTATOR_NAME)));
  }

  @Test
  public void testSpectatorJoinGameDeniedMaxSpectatorCountReached ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (1).build (eventRegistry, eventBus);
    addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);

    final RemoteClient spectatorClient1 = addHumanClient ();
    addMockSpectatorToGameWithName ("TestSpectator1", spectatorClient1, mpc);

    final RemoteClient spectatorClient2 = addHumanClient ();
    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestSpectator2"), spectatorClient2);

    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient2),
                                                     argThat (new SpectatorJoinGameDeniedEventMatcher (
                                                             SpectatorJoinGameDeniedEvent.Reason.GAME_IS_FULL)));
  }

  @Test
  public void testSpectatorJoinGameDeniedSpectatingDisabled ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (0).build (eventRegistry, eventBus);
    addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);

    final RemoteClient spectatorClient = addHumanClient ();
    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestSpectator"), spectatorClient);

    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient),
                                                     argThat (new SpectatorJoinGameDeniedEventMatcher (
                                                             SpectatorJoinGameDeniedEvent.Reason.SPECTATING_DISABLED)));
  }

  @Test
  public void testValidPlayerClaimCountryResponseRequestEvent ()
  {
    // Create a game server with manual initial country assignment.
    final MultiplayerController mpc = mpcBuilder.initialCountryAssignment (InitialCountryAssignment.MANUAL)
            .build (eventRegistry, eventBus);

    final ClientPlayerTuple clientPlayer = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);

    // Request that the player/client claim an available country.
    communicateEventFromCore (new PlayerClaimCountryRequestEvent (clientPlayer.player (),
            ImmutableSet. <CountryPacket>of ()));
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer.client ()),
                                                 isA (PlayerClaimCountryRequestEvent.class));

    // Simulate player/client claiming a country.
    final ClientEvent event = new PlayerClaimCountryResponseRequestEvent ("Test Country 1");
    communicateEventFromClient (event, clientPlayer.client ());

    // Verify that player/client's country claim was published.
    assertEventFiredExactlyOnce (PlayerClaimCountryResponseRequestEvent.class);
    assertEventFiredExactlyOnce (event);
  }

  @Test
  public void testInvalidPlayerClaimCountryResponseRequestEventIgnoredBecauseClientIsNotAPlayer ()
  {
    // Create a game server with manual initial country assignment.
    mpcBuilder.initialCountryAssignment (InitialCountryAssignment.MANUAL).build (eventRegistry, eventBus);

    final RemoteClient client = joinHumanClientToGameServer ();

    // Simulate player/client claiming a country.
    final Event event = communicateEventFromClient (new PlayerClaimCountryResponseRequestEvent ("Test Country 1"),
                                                    client);

    // Verify that player/client's country claim was NOT published.
    assertLastEventWas (event);
  }

  @Test
  public void testInvalidPlayerClaimCountryResponseRequestEventBecauseWrongClient ()
  {
    // Create a game server with manual initial country assignment.
    final MultiplayerController mpc = mpcBuilder.initialCountryAssignment (InitialCountryAssignment.MANUAL)
            .build (eventRegistry, eventBus);

    final ClientPlayerTuple first = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);
    final ClientPlayerTuple second = addHumanClientAndMockPlayerToGameServer ("TestPlayer2", mpc);

    // Request that the player/client claim an available country.
    communicateEventFromCore (new PlayerClaimCountryRequestEvent (first.player (), ImmutableSet. <CountryPacket>of ()));
    verify (mockHumanClientCommunicator).sendTo (eq (first.client ()), isA (PlayerClaimCountryRequestEvent.class));

    // Simulate WRONG player/client claiming a country.
    final Event event = communicateEventFromClient (new PlayerClaimCountryResponseRequestEvent ("Test Country 1"),
                                                    second.client ());

    // Verify that player/client's country claim was NOT published.
    assertLastEventWas (event);
  }

  @Test
  public void testInvalidPlayerClaimCountryResponseRequestEventBecauseWrongClientAfterMultipleRequests ()
  {
    // Create a game server with manual initial country assignment.
    final MultiplayerController mpc = mpcBuilder.initialCountryAssignment (InitialCountryAssignment.MANUAL)
            .build (eventRegistry, eventBus);

    final ClientPlayerTuple first = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);
    final ClientPlayerTuple second = addHumanClientAndMockPlayerToGameServer ("TestPlayer2", mpc);

    // Request that the first player/client claim an available country.
    final Event claimCountryRequestEvent1 = new PlayerClaimCountryRequestEvent (first.player (),
            ImmutableSet. <CountryPacket>of ());
    communicateEventFromCore (claimCountryRequestEvent1);
    verify (mockHumanClientCommunicator).sendTo (first.client (), claimCountryRequestEvent1);
    // Make sure that the request was not sent to the second player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (second.client (), claimCountryRequestEvent1);

    // Simulate & verify first player/client claiming a country.
    final ClientEvent claimCountryResponseRequestEvent1 = new PlayerClaimCountryResponseRequestEvent ("Test Country 1");
    communicateEventFromClient (claimCountryResponseRequestEvent1, first.client ());
    assertLastEventWas (claimCountryResponseRequestEvent1);

    // Request that the second player/client claim an available country.
    final Event claimCountryRequestEvent2 = new PlayerClaimCountryRequestEvent (second.player (),
            ImmutableSet. <CountryPacket>of ());
    communicateEventFromCore (claimCountryRequestEvent2);
    verify (mockHumanClientCommunicator).sendTo (second.client (), claimCountryRequestEvent2);
    // Make sure that the request was not sent to the first player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (first.client (), claimCountryRequestEvent2);

    // Simulate & verify second player/client claiming a country.
    final ClientEvent claimCountryResponseRequestEvent2 = new PlayerClaimCountryResponseRequestEvent ("Test Country 2");
    communicateEventFromClient (claimCountryResponseRequestEvent2, second.client ());
    assertLastEventWas (claimCountryResponseRequestEvent2);

    // Request that the first player/client claim an available country.
    final Event claimCountryRequestEvent3 = new PlayerClaimCountryRequestEvent (first.player (),
            ImmutableSet. <CountryPacket>of ());
    communicateEventFromCore (claimCountryRequestEvent3);
    verify (mockHumanClientCommunicator).sendTo (first.client (), claimCountryRequestEvent3);
    // Make sure that the request was not sent to the second player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (second.client (), claimCountryRequestEvent3);

    // Simulate & verify first player/client claiming a country.
    final ClientEvent claimCountryResponseRequestEvent3 = new PlayerClaimCountryResponseRequestEvent ("Test Country 3");
    communicateEventFromClient (claimCountryResponseRequestEvent3, first.client ());
    assertLastEventWas (claimCountryResponseRequestEvent3);

    // Request that the second player/client claim an available country.
    final Event claimCountryRequestEvent4 = new PlayerClaimCountryRequestEvent (second.player (),
            ImmutableSet. <CountryPacket>of ());
    communicateEventFromCore (claimCountryRequestEvent4);
    verify (mockHumanClientCommunicator).sendTo (second.client (), claimCountryRequestEvent4);
    // Make sure that the request was not sent to the first player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (first.client (), claimCountryRequestEvent4);

    // Simulate & verify second player/client claiming a country.
    final ClientEvent claimCountryResponseRequestEvent4 = new PlayerClaimCountryResponseRequestEvent ("Test Country 4");
    communicateEventFromClient (claimCountryResponseRequestEvent4, second.client ());
    assertLastEventWas (claimCountryResponseRequestEvent4);

    // Request that the first player/client claim an available country.
    final Event claimCountryRequestEvent5 = new PlayerClaimCountryRequestEvent (first.player (),
            ImmutableSet. <CountryPacket>of ());
    communicateEventFromCore (claimCountryRequestEvent5);
    verify (mockHumanClientCommunicator).sendTo (first.client (), claimCountryRequestEvent5);
    // Make sure that the request was not sent to the second player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (second.client (), claimCountryRequestEvent5);

    // Simulate WRONG (second) player/client claiming a country.
    final Event event = communicateEventFromClient (new PlayerClaimCountryResponseRequestEvent ("Test Country 5"),
                                                    second.client ());

    // Verify that player/client's country claim was NOT published.
    assertLastEventWas (event);
  }

  @Test
  public void testInvalidPlayerClaimCountryResponseRequestEventBecauseNoPriorRequestSentFromServer ()
  {
    // Create a game server with manual initial country assignment.
    final MultiplayerController mpc = mpcBuilder.initialCountryAssignment (InitialCountryAssignment.MANUAL)
            .build (eventRegistry, eventBus);

    final ClientPlayerTuple first = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);

    // Simulate player/client claiming a country BEFORE receiving a request to do so from the server.
    final Event event = communicateEventFromClient (new PlayerClaimCountryResponseRequestEvent ("Test Country 1"),
                                                    first.client ());

    // Verify that player/client's country claim was NOT published.
    assertLastEventWas (event);
  }

  @Test
  public void testStalePlayerPacketDataIsUpdatedOnAccess ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);

    final UUID playerId = UUID.randomUUID ();
    final String playerName = "TestPlayer";
    final RemoteClient client = joinHumanClientToGameServer ();
    final PlayerPacket player = new DefaultPlayerPacket (playerId, playerName, PersonSentience.HUMAN,
            PlayerColor.UNKNOWN, 1, 0, 0);
    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName), client);
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player),
            mpc.getPersonLimits ()));
    verify (mockHumanClientCommunicator).sendTo (eq (client), isA (PlayerJoinGameSuccessEvent.class));
    assertTrue (mpc.isPlayerInGame (player));

    // created updated player with different armies in hand count
    final PlayerPacket updatedPlayer = new DefaultPlayerPacket (playerId, playerName, PersonSentience.HUMAN,
            PlayerColor.UNKNOWN, 1, 0, 5);

    // send some arbitrary player event to server
    communicateEventFromCore (new PlayerEndTurnAvailableEvent (updatedPlayer));

    final PlayerPacket actualPlayer = Iterables.getOnlyElement (mpc.getPlayers ());
    assertEquals (updatedPlayer, actualPlayer);
    assertEquals (updatedPlayer.getArmiesInHand (), actualPlayer.getArmiesInHand ());
  }

  // Unit test 1 for bug detailed in PERIL-100
  @Test
  public void testHumanClientDisconnectAfterSendingPlayerJoinGameRequest ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventRegistry, eventBus);
    final RemoteClient client = joinHumanClientToGameServer ();
    final String playerName = "TestPlayer";
    final PlayerPacket player = createMockHumanPlayer (playerName);
    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName), client);
    // disconnect client
    disconnect (client);
    assertFalse (mpc.isClientInServer (client));
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player),
            mpc.getPersonLimits ()));
    assertFalse (mpc.isPlayerInGame (player));
  }

  // Unit test 2 for bug detailed in PERIL-100
  @Test
  public void testAiClientDisconnectAfterSendingPlayerJoinGameRequest ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (1).build (eventRegistry, eventBus);
    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer");
    final RemoteClient client = joinAiClientToGameServer (playerName);
    final PlayerPacket player = createMockAiPlayer (playerName);
    communicateEventFromClient (new AiPlayerJoinGameRequestEvent (playerName), client);
    // disconnect client
    disconnect (client);
    assertFalse (mpc.isClientInServer (client));
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player),
            mpc.getPersonLimits ()));
    assertFalse (mpc.isPlayerInGame (player));
  }

  // -------------- Test helper facilities -------------- //

  private PlayerPacket createMockHumanPlayer (final String playerName)
  {
    final PlayerPacket player = mock (PlayerPacket.class);
    when (player.getName ()).thenReturn (playerName);
    when (player.hasName (eq (playerName))).thenReturn (true);
    when (player.getSentience ()).thenReturn (PersonSentience.HUMAN);
    when (player.has (PersonSentience.HUMAN)).thenReturn (true);
    when (player.has (PersonSentience.AI)).thenReturn (false);
    when (player.is (eq (player))).thenReturn (true);
    when (player.isNot (not (eq (player)))).thenReturn (true);
    when (player.toString ()).thenReturn (playerName);

    return player;
  }

  private PlayerPacket createMockAiPlayer (final String playerName)
  {
    final PlayerPacket player = mock (PlayerPacket.class);
    when (player.getName ()).thenReturn (playerName);
    when (player.hasName (eq (playerName))).thenReturn (true);
    when (player.getSentience ()).thenReturn (PersonSentience.AI);
    when (player.has (PersonSentience.AI)).thenReturn (true);
    when (player.has (PersonSentience.HUMAN)).thenReturn (false);
    when (player.is (eq (player))).thenReturn (true);
    when (player.isNot (not (eq (player)))).thenReturn (true);
    when (player.toString ()).thenReturn (playerName);

    return player;
  }

  private SpectatorPacket createMockSpectator (final String spectatorName)
  {
    final SpectatorPacket spectator = mock (SpectatorPacket.class);
    when (spectator.getName ()).thenReturn (spectatorName);
    when (spectator.hasName (eq (spectatorName))).thenReturn (true);
    when (spectator.getSentience ()).thenReturn (PersonSentience.HUMAN);
    when (spectator.has (PersonSentience.HUMAN)).thenReturn (true);
    when (spectator.has (PersonSentience.AI)).thenReturn (false);
    when (spectator.is (eq (spectator))).thenReturn (true);
    when (spectator.isNot (not (eq (spectator)))).thenReturn (true);
    when (spectator.toString ()).thenReturn (spectatorName);

    return spectator;
  }

  private ClientPlayerTuple addHumanClientAndMockPlayerToGameServer (final String playerName,
                                                                     final MultiplayerController mpc)
  {
    final RemoteClient client = joinHumanClientToGameServer ();
    final PlayerPacket player = addMockHumanPlayerToGameWithName (playerName, client, mpc);

    return new ClientPlayerTuple (client, player);
  }

  private ClientPlayerTuple addAiClientAndMockPlayerToGameServer (final String playerName,
                                                                  final MultiplayerController mpc)
  {
    assert GameSettings.isValidAiPlayerNameWithMandatoryClanTag (playerName);
    final RemoteClient client = joinAiClientToGameServer (playerName);
    final PlayerPacket player = addMockAiPlayerToGameWithName (playerName, client, mpc);

    return new ClientPlayerTuple (client, player);
  }

  private RemoteClient joinHumanClientToGameServer ()
  {
    final RemoteClient client = addHumanClient ();
    verify (mockHumanClientCommunicator).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    return client;
  }

  private RemoteClient joinAiClientToGameServer (final String playerName)
  {
    final RemoteClient client = addAiClient (playerName);
    verify (mockAiClientCommunicator).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    return client;
  }

  private PlayerPacket addMockHumanPlayerToGameWithName (final String playerName,
                                                         final RemoteClient client,
                                                         final MultiplayerController mpc)
  {
    assert mpc.getPlayerLimitFor (PersonSentience.HUMAN) > 0;
    final PlayerPacket player = createMockHumanPlayer (playerName);
    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName), client);
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player),
            mpc.getPersonLimits ()));
    verify (mockHumanClientCommunicator).sendTo (eq (client), isA (PlayerJoinGameSuccessEvent.class));
    assertTrue (mpc.isPlayerInGame (player));

    return player;
  }

  private PlayerPacket addMockAiPlayerToGameWithName (final String playerName,
                                                      final RemoteClient client,
                                                      final MultiplayerController mpc)
  {
    assert GameSettings.isValidAiPlayerNameWithMandatoryClanTag (playerName);
    assert mpc.getPlayerLimitFor (PersonSentience.AI) > 0;
    final PlayerPacket player = createMockAiPlayer (playerName);
    communicateEventFromClient (new AiPlayerJoinGameRequestEvent (playerName), client);
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player),
            mpc.getPersonLimits ()));
    verify (mockAiClientCommunicator).sendTo (eq (client), isA (PlayerJoinGameSuccessEvent.class));
    assertTrue (mpc.isPlayerInGame (player));

    return player;
  }

  private SpectatorPacket addMockSpectatorToGameWithName (final String spectatorName,
                                                          final RemoteClient client,
                                                          final MultiplayerController mpc)
  {
    assert mpc.getSpectatorLimit () > 0;
    communicateEventFromClient (new SpectatorJoinGameRequestEvent (spectatorName), client);
    verify (mockSpectatorClientCommunicator).sendTo (eq (client), isA (SpectatorJoinGameSuccessEvent.class));

    return createMockSpectator (spectatorName);
  }

  private void assertLastEventWasType (final Class <?> eventType)
  {
    assertTrue ("Expected last event was type [" + eventType.getSimpleName () + "], but was ["
            + eventHandler.lastEventType () + "] All events (newest to oldest): [" + eventHandler.getAllEvents ()
            + "].", eventHandler.lastEventWasType (eventType));
  }

  private void assertSecondToLastEventWas (final Event event)
  {
    assertEquals ("Expected second-to-last event was [" + event + "], but was [" + eventHandler.secondToLastEvent ()
            + "] All events (newest to oldest): [" + eventHandler.getAllEvents () + "].", event,
                  eventHandler.secondToLastEvent ());
  }

  private void assertLastEventWas (final Event event)
  {
    assertEquals ("Expected last event was [" + event + "], but was [" + eventHandler.lastEvent ()
            + "] All events (newest to oldest): [" + eventHandler.getAllEvents () + "].", event,
                  eventHandler.lastEvent ());
  }

  private void assertEventFiredExactlyOnce (final Class <?> eventType)
  {
    assertTrue ("Expected event type [" + eventType.getSimpleName () + "] was fired exactly once, but was fired ["
            + eventHandler.countOf (eventType) + "] times. All events (newest to oldest): ["
            + eventHandler.getAllEvents () + "].", eventHandler.wasFiredExactlyOnce (eventType));
  }

  private void assertEventFiredExactlyOnce (final Event event)
  {
    assertTrue ("Expected event type [" + event.getClass ().getSimpleName ()
            + "] was fired exactly once, but was fired [" + eventHandler.countOf (event.getClass ())
            + "] times. All events (newest to oldest): [" + eventHandler.getAllEvents () + "].",
                eventHandler.wasFiredExactlyOnce (event));
  }

  private ClientCommunicationEvent communicateEventFromClient (final ClientEvent event, final RemoteClient client)
  {
    final ClientCommunicationEvent clientCommunicationEvent = new ClientCommunicationEvent (client, event);
    eventBus.publish (clientCommunicationEvent);

    return clientCommunicationEvent;
  }

  private void communicateEventFromCore (final Event event)
  {
    eventBus.publish (event);
  }

  private void connect (final RemoteClient client)
  {
    Arguments.checkIsNotNull (client, "client");

    eventBus.publish (new ClientConnectionEvent (client));
  }

  private void disconnect (final RemoteClient client)
  {
    Arguments.checkIsNotNull (client, "client");

    eventBus.publish (new ClientDisconnectionEvent (client));
  }

  private ServerConfiguration createDefaultServerConfig ()
  {
    return new DefaultServerConfiguration (DEFAULT_TEST_SERVER_ADDRESS, DEFAULT_TEST_SERVER_PORT);
  }

  private RemoteClient addHumanClient ()
  {
    final RemoteClient client = createHumanClient ();
    addHumanClient (client);

    return client;
  }

  private RemoteClient addHumanClientWithAddress (final String address)
  {
    Arguments.checkIsNotNull (address, "address");

    final RemoteClient client = createHumanClientWithAddress (address);
    addHumanClient (client);

    return client;
  }

  private RemoteClient createHumanClient ()
  {
    return createHumanClientWithAddress ("forerunner.games");
  }

  private RemoteClient createHumanClientWithAddress (final String address)
  {
    Arguments.checkIsNotNull (address, "address");

    final int port = 1000 + clientCount;
    return new KryonetRemoteClient (clientCount++, new InetSocketAddress (address, port));
  }

  private RemoteClient createHumanHost ()
  {
    return createHumanClientWithAddress (NetworkConstants.LOCALHOST_ADDRESS);
  }

  private void addHumanClient (final RemoteClient client)
  {
    connect (client);
    eventBus.publish (new ClientCommunicationEvent (client, new HumanJoinGameServerRequestEvent ()));
  }

  private RemoteClient addAiClient (final String playerName)
  {
    final RemoteClient client = createAiClient (playerName);
    addAiClient (client);

    return client;
  }

  private RemoteClient createAiClient (final String playerName)
  {
    return new AiClient (playerName);
  }

  private void addAiClient (final RemoteClient client)
  {
    connect (client);
    eventBus.publish (new ClientCommunicationEvent (client, new AiJoinGameServerRequestEvent ()));
  }

  /*
   * Configurable test builder for MultiplayerController. Returns default values if left unchanged.
   */
  static class MultiplayerControllerBuilder
  {
    private final RemoteClientConnector connector;
    private final PlayerCommunicator humanPlayerCommunicator;
    private final PlayerCommunicator aiPlayerCommunicator;
    private final SpectatorCommunicator spectatorCommunicator;
    private final CoreCommunicator coreCommunicator;
    // game configuration fields
    private final GameMode gameMode = GameMode.CLASSIC;
    private final PlayMapMetadata playMapMetadata = new DefaultPlayMapMetadata (
            GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_NAME, GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_TYPE, gameMode,
            GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_DIR_NAME, GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_DIR_TYPE);
    private final PersonLimits.Builder personLimitsBuilder = PersonLimits.builder ().classicModeDefaults ();
    private InitialCountryAssignment initialCountryAssignment = ClassicGameRules.DEFAULT_INITIAL_COUNTRY_ASSIGNMENT;
    // game server configuration fields
    private String gameServerName = DEFAULT_TEST_GAME_SERVER_NAME;
    private GameServerType gameServerType = DEFAULT_GAME_SERVER_TYPE;
    // server configuration fields
    private String serverAddress = DEFAULT_TEST_SERVER_ADDRESS;
    private int serverPort = DEFAULT_TEST_SERVER_PORT;
    private int winPercent = ClassicGameRules.DEFAULT_WIN_PERCENTAGE;
    private int totalCountryCount = ClassicGameRules.DEFAULT_TOTAL_COUNTRY_COUNT;

    MultiplayerControllerBuilder gameServerName (final String gameServerName)
    {
      Arguments.checkIsNotNull (gameServerName, "gameServerName");

      this.gameServerName = gameServerName;
      return this;
    }

    MultiplayerControllerBuilder gameServerType (final GameServerType gameServerType)
    {
      Arguments.checkIsNotNull (gameServerType, "gameServerType");

      this.gameServerType = gameServerType;
      return this;
    }

    MultiplayerControllerBuilder serverAddress (final String serverAddress)
    {
      Arguments.checkIsNotNull (serverAddress, "serverAddress");

      this.serverAddress = serverAddress;
      return this;
    }

    MultiplayerControllerBuilder serverPort (final int serverPort)
    {
      Arguments.checkIsNotNegative (serverPort, "serverPort");
      Arguments.checkUpperInclusiveBound (serverPort, NetworkConstants.MAX_PORT, "serverPort");

      this.serverPort = serverPort;
      return this;
    }

    MultiplayerControllerBuilder humanPlayerLimit (final int humanPlayerLimit)
    {
      Arguments.checkIsNotNegative (humanPlayerLimit, "humanPlayerLimit");

      personLimitsBuilder.humanPlayers (humanPlayerLimit);

      return this;
    }

    MultiplayerControllerBuilder aiPlayerLimit (final int aiPlayerLimit)
    {
      Arguments.checkIsNotNegative (aiPlayerLimit, "aiPlayerLimit");

      personLimitsBuilder.aiPlayers (aiPlayerLimit);

      return this;
    }

    MultiplayerControllerBuilder spectatorLimit (final int spectatorLimit)
    {
      Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");

      personLimitsBuilder.spectators (spectatorLimit);

      return this;
    }

    MultiplayerControllerBuilder winPercent (final int winPercent)
    {
      Arguments.checkIsNotNegative (winPercent, "winPercent");
      Arguments.checkUpperInclusiveBound (winPercent, 100, "winPercent");

      this.winPercent = winPercent;
      return this;
    }

    MultiplayerControllerBuilder totalCountryCount (final int totalCountryCount)
    {
      Arguments.checkIsNotNegative (totalCountryCount, "totalCountryCount");

      this.totalCountryCount = totalCountryCount;
      return this;
    }

    MultiplayerControllerBuilder initialCountryAssignment (final InitialCountryAssignment initialCountryAssignment)
    {
      Arguments.checkIsNotNull (initialCountryAssignment, "initialCountryAssignment");

      this.initialCountryAssignment = initialCountryAssignment;
      return this;
    }

    MultiplayerController build (final EventRegistry eventRegistry, final MBassador <Event> eventBus)
    {
      Arguments.checkIsNotNull (eventRegistry, "eventRegistry");
      Arguments.checkIsNotNull (eventBus, "eventBus");

      final GameRules gameRules = GameRulesFactory.create (gameMode, personLimitsBuilder.build (), winPercent,
                                                           totalCountryCount, initialCountryAssignment);

      final GameConfiguration gameConfig = new DefaultGameConfiguration (gameMode, playMapMetadata, gameRules);
      final ServerConfiguration serverConfig = new DefaultServerConfiguration (serverAddress, serverPort);
      final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (gameServerName,
              gameServerType, gameConfig, serverConfig, DEFAULT_TEST_REQUEST_TIMEOUT);

      final MultiplayerController controller = new MultiplayerController (gameServerConfig, connector,
              humanPlayerCommunicator, aiPlayerCommunicator, spectatorCommunicator, coreCommunicator, eventRegistry,
              eventBus);

      controller.initialize ();

      return controller;
    }

    // add game mode and/or initial-country-assignment later if needed

    private MultiplayerControllerBuilder (final RemoteClientConnector connector,
                                          final PlayerCommunicator humanPlayerCommunicator,
                                          final PlayerCommunicator aiPlayerCommunicator,
                                          final SpectatorCommunicator spectatorCommunicator,
                                          final CoreCommunicator coreCommunicator)
    {
      this.connector = connector;
      this.humanPlayerCommunicator = humanPlayerCommunicator;
      this.aiPlayerCommunicator = aiPlayerCommunicator;
      this.spectatorCommunicator = spectatorCommunicator;
      this.coreCommunicator = coreCommunicator;
    }
  }

  private static final class JoinGameServerSuccessEventMatcher implements ArgumentMatcher <JoinGameServerSuccessEvent>
  {
    private final ServerConfiguration serverConfig;
    private final RemoteClient client;

    JoinGameServerSuccessEventMatcher (final ServerConfiguration serverConfig, final RemoteClient client)
    {
      Arguments.checkIsNotNull (serverConfig, "serverConfig");
      Arguments.checkIsNotNull (client, "client");

      this.serverConfig = serverConfig;
      this.client = client;
    }

    @Override
    public boolean matches (final JoinGameServerSuccessEvent argument)
    {
      final ServerConfiguration matchServerConfig = argument.getGameServerConfiguration ();
      final ClientConfiguration matchClientConfig = argument.getClientConfiguration ();

      return matchServerConfig.getAddress ().equals (serverConfig.getAddress ())
              && matchClientConfig.getAddress ().equals (client.getAddress ())
              && matchServerConfig.getPort () == serverConfig.getPort ()
              && matchClientConfig.getPort () == client.getPort ();
    }
  }

  private static final class PlayerJoinGameSuccessEventMatcher implements ArgumentMatcher <PlayerJoinGameSuccessEvent>
  {
    private final PlayerPacket player;
    private final ImmutableSet <PlayerPacket> playersInGame;

    PlayerJoinGameSuccessEventMatcher (final PlayerPacket player, final ImmutableSet <PlayerPacket> playersInGame)
    {
      Arguments.checkIsNotNull (player, "player");
      Arguments.checkIsNotNull (playersInGame, "playersInGame");
      Arguments.checkHasNoNullElements (playersInGame, "playersInGame");

      this.player = player;
      this.playersInGame = playersInGame;
    }

    @Override
    public boolean matches (final PlayerJoinGameSuccessEvent argument)
    {
      return Objects.equals (argument.getPerson (), player)
              && Objects.equals (argument.getPlayersInGame (), playersInGame);
    }
  }

  private static final class PlayerJoinGameDeniedEventMatcher implements ArgumentMatcher <PlayerJoinGameDeniedEvent>
  {
    private final String playerName;
    private final PlayerJoinGameDeniedEvent.Reason reason;

    PlayerJoinGameDeniedEventMatcher (final String playerName, final PlayerJoinGameDeniedEvent.Reason reason)
    {
      this.playerName = playerName;
      this.reason = reason;
    }

    @Override
    public boolean matches (final PlayerJoinGameDeniedEvent argument)
    {
      return Objects.equals (argument.getPlayerName (), playerName) && Objects.equals (argument.getReason (), reason);
    }
  }

  private static final class JoinGameServerDeniedEventMatcher implements ArgumentMatcher <JoinGameServerDeniedEvent>
  {
    private final RemoteClient client;

    JoinGameServerDeniedEventMatcher (final RemoteClient client)
    {
      Arguments.checkIsNotNull (client, "client");

      this.client = client;
    }

    @Override
    public boolean matches (final JoinGameServerDeniedEvent argument)
    {
      final ClientConfiguration matchClientConfig = argument.getClientConfiguration ();
      return Objects.equals (matchClientConfig.getAddress (), client.getAddress ())
              && matchClientConfig.getPort () == client.getPort ();
    }
  }

  private static final class SpectatorJoinGameDeniedEventMatcher
          implements ArgumentMatcher <SpectatorJoinGameDeniedEvent>
  {
    private final SpectatorJoinGameDeniedEvent.Reason reason;

    SpectatorJoinGameDeniedEventMatcher (final SpectatorJoinGameDeniedEvent.Reason reason)
    {
      Arguments.checkIsNotNull (reason, "reason");

      this.reason = reason;
    }

    @Override
    public boolean matches (final SpectatorJoinGameDeniedEvent argument)
    {
      return argument.getReason () == reason;
    }

    @Override
    public String toString ()
    {
      return Strings.format ("{}: Reason for denial: [{}]", SpectatorJoinGameDeniedEvent.class.getSimpleName (),
                             reason);
    }
  }

  private final class PlayerJoinGameSuccessEventIdentityMatcher implements ArgumentMatcher <PlayerJoinGameSuccessEvent>
  {
    private final PersonIdentity identity;

    PlayerJoinGameSuccessEventIdentityMatcher (final PersonIdentity identity)
    {
      Arguments.checkIsNotNull (identity, "identity");

      this.identity = identity;
    }

    @Override
    public boolean matches (final PlayerJoinGameSuccessEvent argument)
    {
      Arguments.checkIsNotNull (argument, "argument");

      return argument.getIdentity () == identity;
    }

    @Override
    public String toString ()
    {
      return Strings.format ("matches {}: [{}].", identity.getClass ().getSimpleName (), identity);
    }
  }

  private final class ClientPlayerTuple
  {
    private final RemoteClient client;
    private final PlayerPacket player;

    ClientPlayerTuple (final RemoteClient client, final PlayerPacket player)
    {
      Arguments.checkIsNotNull (client, "client");
      Arguments.checkIsNotNull (player, "player");

      this.client = client;
      this.player = player;
    }

    @Override
    public int hashCode ()
    {
      int result = client.hashCode ();
      result = 31 * result + player.hashCode ();
      return result;
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (this == obj) return true;
      if (obj == null || getClass () != obj.getClass ()) return false;
      final ClientPlayerTuple clientPlayerTuple = (ClientPlayerTuple) obj;
      return client.equals (clientPlayerTuple.client) && player.equals (clientPlayerTuple.player);
    }

    public RemoteClient client ()
    {
      return client;
    }

    public PlayerPacket player ()
    {
      return player;
    }

    @Override
    public String toString ()
    {
      return Strings.format ("{}: Client: {} | Player: {}", getClass ().getSimpleName (), client, player);
    }

  }
}
