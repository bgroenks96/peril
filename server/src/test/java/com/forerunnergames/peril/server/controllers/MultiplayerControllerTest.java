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

package com.forerunnergames.peril.server.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
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
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.SpectatorJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.kryonet.KryonetRemote;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.common.playmap.DefaultPlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.server.communicators.AiPlayerCommunicator;
import com.forerunnergames.peril.server.communicators.CoreCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultSpectatorCommunicator;
import com.forerunnergames.peril.server.communicators.HumanPlayerCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.communicators.SpectatorCommunicator;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.NetworkConstants;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.ClientCommunicator;
import com.forerunnergames.tools.net.client.ClientConnector;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;
import com.forerunnergames.tools.net.server.configuration.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.net.InetSocketAddress;
import java.util.Set;

import net.engio.mbassy.bus.MBassador;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class MultiplayerControllerTest
{
  private static final String DEFAULT_TEST_GAME_SERVER_NAME = "test-server";
  private static final GameServerType DEFAULT_GAME_SERVER_TYPE = GameServerType.DEDICATED;
  private static final String DEFAULT_TEST_SERVER_ADDRESS = "server@test";
  private static final int DEFAULT_TEST_SERVER_PORT = 8888;
  private final EventBusHandler eventHandler = new EventBusHandler ();
  private final ClientConnector mockConnector = mock (ClientConnector.class, Mockito.RETURNS_SMART_NULLS);
  private final ClientCommunicator mockHumanClientCommunicator = mock (ClientCommunicator.class,
                                                                       Mockito.RETURNS_SMART_NULLS);
  private final ClientCommunicator mockAiClientCommunicator = mock (ClientCommunicator.class,
                                                                    Mockito.RETURNS_SMART_NULLS);
  private final ClientCommunicator mockSpectatorClientCommunicator = mock (ClientCommunicator.class,
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
  private MBassador <Event> eventBus;

  // convenience method for fetching a new MultiplayerControllerBuilder
  // Note: package private visibility is intended; other test classes in package should have access.
  static MultiplayerControllerBuilder builder (final ClientConnector connector,
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
    eventHandler.subscribe (eventBus);
    // default mock for core communicator - returns empty player data
    mockCoreCommunicatorPlayersWith ();
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
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventBus);

    final Remote host = createHumanHost ();
    connect (host);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);

    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerSuccessEvent.class));
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchServerConfig = matchEvent.getGameServerConfiguration ();
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchServerConfig.getServerAddress ().equals (serverConfig.getServerAddress ())
                && matchClientConfig.getClientAddress ().equals (host.getAddress ())
                && matchServerConfig.getServerTcpPort () == serverConfig.getServerTcpPort ()
                && matchClientConfig.getClientTcpPort () == host.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (host), argThat (successEventMatcher));
  }

  @Test
  public void testSuccessfulHumanClientJoinGameServer ()
  {
    mpcBuilder.build (eventBus);

    final Remote client = createHumanClient ();
    connect (client);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), client);

    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerSuccessEvent.class));
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchServerConfig = matchEvent.getGameServerConfiguration ();
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchServerConfig.getServerAddress ().equals (serverConfig.getServerAddress ())
                && matchClientConfig.getClientAddress ().equals (client.getAddress ())
                && matchServerConfig.getServerTcpPort () == serverConfig.getServerTcpPort ()
                && matchClientConfig.getClientTcpPort () == client.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator).sendTo (eq (client), argThat (successEventMatcher));
  }

  @Test
  public void testSuccessfulAiClientJoinGameServer ()
  {
    mpcBuilder.aiPlayerLimit (1).build (eventBus);

    final Remote client = createAiClient (GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1"));
    connect (client);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    communicateEventFromClient (new AiJoinGameServerRequestEvent (), client);

    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerSuccessEvent.class));
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchServerConfig = matchEvent.getGameServerConfiguration ();
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchServerConfig.getServerAddress ().equals (serverConfig.getServerAddress ())
                && matchClientConfig.getClientAddress ().equals (client.getAddress ())
                && matchServerConfig.getServerTcpPort () == serverConfig.getServerTcpPort ()
                && matchClientConfig.getClientTcpPort () == client.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockAiClientCommunicator).sendTo (eq (client), argThat (successEventMatcher));
  }

  @Test
  public void testTwoHumanClientsJoinGameServerSimultaneously ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventBus);

    final Remote client1 = joinHumanClientToGameServer ();
    final Remote client2 = joinHumanClientToGameServer ();

    final Set <PlayerPacket> players = Sets.newConcurrentHashSet ();
    final Function <Remote, PlayerJoinGameSuccessEvent> clientJoinAsPlayer = new Function <Remote, PlayerJoinGameSuccessEvent> ()
    {
      @Override
      public PlayerJoinGameSuccessEvent apply (final Remote client)
      {
        final String playerName = "TestPlayer" + client.getConnectionId ();
        final PlayerPacket player = createMockHumanPlayer (playerName);
        final PlayerJoinGameRequestEvent requestEvent = new HumanPlayerJoinGameRequestEvent (playerName);
        communicateEventFromClient (requestEvent, client);
        assertTrue (eventHandler.wasFiredExactlyOnce (requestEvent));
        players.add (player);
        final PlayerJoinGameSuccessEvent successEvent = new PlayerJoinGameSuccessEvent (player,
                ImmutableSet.copyOf (players), mpc.getPlayerLimit ());
        final BaseMatcher <PlayerJoinGameSuccessEvent> successEventMatcher = new BaseMatcher <PlayerJoinGameSuccessEvent> ()
        {
          @Override
          public boolean matches (final Object arg0)
          {
            if (!(arg0 instanceof PlayerJoinGameSuccessEvent)) return false;
            final PlayerJoinGameSuccessEvent event = (PlayerJoinGameSuccessEvent) arg0;
            return event.getPlayer ().equals (player) && event.getPlayersInGame ().equals (players);
          }

          @Override
          public void describeTo (final Description arg0)
          {
          }
        };
        communicateEventFromCore (successEvent);
        verify (mockHumanClientCommunicator, times (players.size ())).sendTo (any (Remote.class),
                                                                              argThat (successEventMatcher));
        assertTrue (mpc.isPlayerInGame (player));

        return successEvent;
      }
    };

    final PlayerJoinGameSuccessEvent clientResult1 = clientJoinAsPlayer.apply (client1);
    assertNotNull (clientResult1);
    final PlayerJoinGameSuccessEvent clientResult2 = clientJoinAsPlayer.apply (client2);
    assertNotNull (clientResult2);
    assertTrue (clientResult1.getOtherPlayersInGame ().isEmpty ());
    assertEquals (ImmutableSet.of (clientResult1.getPlayer ()), clientResult2.getOtherPlayersInGame ());
  }

  @Test
  public void testTwoAiClientsJoinGameServerSimultaneously ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (2).build (eventBus);

    final Remote client1 = joinAiClientToGameServer (GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1"));
    final Remote client2 = joinAiClientToGameServer (GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer2"));

    final Set <PlayerPacket> players = Sets.newConcurrentHashSet ();
    final Function <Remote, PlayerJoinGameSuccessEvent> clientJoinAsPlayer = new Function <Remote, PlayerJoinGameSuccessEvent> ()
    {
      @Override
      public PlayerJoinGameSuccessEvent apply (final Remote client)
      {
        final String playerName = client.getAddress ();
        final PlayerPacket player = createMockAiPlayer (playerName);
        final PlayerJoinGameRequestEvent requestEvent = new AiPlayerJoinGameRequestEvent (playerName);
        communicateEventFromClient (requestEvent, client);
        assertTrue (eventHandler.wasFiredExactlyOnce (requestEvent));
        players.add (player);
        final PlayerJoinGameSuccessEvent successEvent = new PlayerJoinGameSuccessEvent (player,
                ImmutableSet.copyOf (players), mpc.getPlayerLimit ());
        final BaseMatcher <PlayerJoinGameSuccessEvent> successEventMatcher = new BaseMatcher <PlayerJoinGameSuccessEvent> ()
        {
          @Override
          public boolean matches (final Object arg0)
          {
            if (!(arg0 instanceof PlayerJoinGameSuccessEvent)) return false;
            final PlayerJoinGameSuccessEvent event = (PlayerJoinGameSuccessEvent) arg0;
            return event.getPlayer ().equals (player) && event.getPlayersInGame ().equals (players);
          }

          @Override
          public void describeTo (final Description arg0)
          {
          }
        };
        communicateEventFromCore (successEvent);
        verify (mockAiClientCommunicator, times (1)).sendTo (any (Remote.class), argThat (successEventMatcher));
        assertTrue (mpc.isPlayerInGame (player));

        return successEvent;
      }
    };

    final PlayerJoinGameSuccessEvent clientResult1 = clientJoinAsPlayer.apply (client1);
    assertNotNull (clientResult1);
    final PlayerJoinGameSuccessEvent clientResult2 = clientJoinAsPlayer.apply (client2);
    assertNotNull (clientResult2);
    assertTrue (clientResult1.getOtherPlayersInGame ().isEmpty ());
    assertEquals (ImmutableSet.of (clientResult1.getPlayer ()), clientResult2.getOtherPlayersInGame ());
  }

  @Test
  public void testHumanClientJoinRequestBeforeHostDenied ()
  {
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventBus);

    final Remote client = addHumanClient ();

    final BaseMatcher <JoinGameServerDeniedEvent> denialEventMatcher = new BaseMatcher <JoinGameServerDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerDeniedEvent.class));
        final JoinGameServerDeniedEvent matchEvent = (JoinGameServerDeniedEvent) arg0;
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchClientConfig.getClientAddress ().equals (client.getAddress ())
                && matchClientConfig.getClientTcpPort () == client.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client), argThat (denialEventMatcher));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testAiClientJoinRequestBeforeHostSuccessful ()
  {
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).aiPlayerLimit (1).build (eventBus);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    final Remote client = addAiClient (GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1"));

    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerSuccessEvent.class));
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchServerConfig = matchEvent.getGameServerConfiguration ();
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchServerConfig.getServerAddress ().equals (serverConfig.getServerAddress ())
                && matchClientConfig.getClientAddress ().equals (client.getAddress ())
                && matchServerConfig.getServerTcpPort () == serverConfig.getServerTcpPort ()
                && matchClientConfig.getClientTcpPort () == client.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockAiClientCommunicator).sendTo (eq (client), argThat (successEventMatcher));
  }

  @Test
  public void testHostClientJoinDedicatedGameServerDenied ()
  {
    mpcBuilder.gameServerType (GameServerType.DEDICATED).build (eventBus);

    final Remote host = createHumanHost ();
    connect (host);

    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);

    final BaseMatcher <JoinGameServerDeniedEvent> deniedEventMatcher = new BaseMatcher <JoinGameServerDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerDeniedEvent.class));
        final JoinGameServerDeniedEvent matchEvent = (JoinGameServerDeniedEvent) arg0;
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchClientConfig.getClientAddress ().equals (host.getAddress ())
                && matchClientConfig.getClientTcpPort () == host.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (host), argThat (deniedEventMatcher));
  }

  @Test
  public void testNonHostClientJoinGameServerAfterHostSuccessful ()
  {
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventBus);

    final Remote host = createHumanHost ();
    connect (host);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);

    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerSuccessEvent.class));
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchServerConfig = matchEvent.getGameServerConfiguration ();
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchServerConfig.getServerAddress ().equals (serverConfig.getServerAddress ())
                && matchClientConfig.getClientAddress ().equals (host.getAddress ())
                && matchServerConfig.getServerTcpPort () == serverConfig.getServerTcpPort ()
                && matchClientConfig.getClientTcpPort () == host.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (host), argThat (successEventMatcher));

    final Remote client = addHumanClient ();
    verify (mockHumanClientCommunicator).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));
  }

  @Test
  public void testHostClientJoinGameServerAfterHostDenied ()
  {
    // MPC still needs to be built in order to register the event bus
    mpcBuilder.gameServerType (GameServerType.HOST_AND_PLAY).build (eventBus);

    final Remote host = createHumanHost ();
    connect (host);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), host);

    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerSuccessEvent.class));
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchServerConfig = matchEvent.getGameServerConfiguration ();
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchServerConfig.getServerAddress ().equals (serverConfig.getServerAddress ())
                && matchClientConfig.getClientAddress ().equals (host.getAddress ())
                && matchServerConfig.getServerTcpPort () == serverConfig.getServerTcpPort ()
                && matchClientConfig.getClientTcpPort () == host.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (host), argThat (successEventMatcher));

    final Remote duplicateHost = createHumanHost ();
    connect (duplicateHost);
    communicateEventFromClient (new HumanJoinGameServerRequestEvent (), duplicateHost);
    verify (mockHumanClientCommunicator).sendTo (eq (duplicateHost), isA (JoinGameServerDeniedEvent.class));
  }

  @Test
  public void testHumanClientJoinRequestDeniedBecauseInvalidIpEmpty ()
  {
    mpcBuilder.build (eventBus);

    final Remote client = addHumanClientWithAddress ("");

    final BaseMatcher <JoinGameServerDeniedEvent> denialEventMatcher = new BaseMatcher <JoinGameServerDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerDeniedEvent.class));
        final JoinGameServerDeniedEvent matchEvent = (JoinGameServerDeniedEvent) arg0;
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchClientConfig.getClientAddress ().equals (client.getAddress ())
                && matchClientConfig.getClientTcpPort () == client.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client), argThat (denialEventMatcher));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testClientJoinRequestDeniedBecauseMatchesServerIp ()
  {
    mpcBuilder.serverAddress ("1.2.3.4").build (eventBus);

    final Remote client = addHumanClientWithAddress ("1.2.3.4");

    final BaseMatcher <JoinGameServerDeniedEvent> denialEventMatcher = new BaseMatcher <JoinGameServerDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerDeniedEvent.class));
        final JoinGameServerDeniedEvent matchEvent = (JoinGameServerDeniedEvent) arg0;
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchClientConfig.getClientAddress ().equals (client.getAddress ())
                && matchClientConfig.getClientTcpPort () == client.getPort ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client), argThat (denialEventMatcher));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testValidHumanPlayerJoinGameRequestPublished ()
  {
    mpcBuilder.build (eventBus);

    final Remote client = addHumanClient ();
    verify (mockHumanClientCommunicator, only ()).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "TestPlayer1";
    final Event event = new HumanPlayerJoinGameRequestEvent (playerName);
    communicateEventFromClient (event, client);
    assertLastEventWas (event);
  }

  @Test
  public void testValidAiPlayerJoinGameRequestPublished ()
  {
    mpcBuilder.aiPlayerLimit (1).build (eventBus);

    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final Remote client = addAiClient (playerName);
    verify (mockAiClientCommunicator, only ()).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    final Event event = new AiPlayerJoinGameRequestEvent (playerName);
    communicateEventFromClient (event, client);
    assertLastEventWas (event);
  }

  @Test
  public void testIgnoreHumanPlayerJoinGameRequestBeforeJoiningGameServer ()
  {
    mpcBuilder.build (eventBus);

    // Connect client to server, but do not join client to game server.
    final Remote client = createHumanClient ();
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
    mpcBuilder.aiPlayerLimit (1).build (eventBus);

    // Connect client to server, but do not join client to game server.
    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final Remote client = createAiClient (playerName);
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
    final MultiplayerController mpc = mpcBuilder.build (eventBus);
    final String playerName1 = "TestPlayer1";
    final String playerName2 = "TestPlayer2";

    final ClientPlayerTuple clientPlayer1 = addHumanClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addHumanClientAndMockPlayerToGameServer (playerName2, mpc);

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer1.client),
                                                 argThat (allOf (Matchers.isA (PlayerJoinGameSuccessEvent.class),
                                                                 new PersonIdentityMatcher (PersonIdentity.NON_SELF))));

    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer2.client),
                                                 argThat (allOf (Matchers.isA (PlayerJoinGameSuccessEvent.class),
                                                                 new PersonIdentityMatcher (PersonIdentity.SELF))));

    verify (mockAiClientCommunicator).sendToAllExcept (eq (clientPlayer2.client),
                                                       argThat (allOf (Matchers.isA (PlayerJoinGameSuccessEvent.class),
                                                                       new PersonIdentityMatcher (
                                                                               PersonIdentity.NON_SELF))));

    verifyZeroInteractions (mockSpectatorClientCommunicator);

    assertTrue (mpc.isPlayerInGame (clientPlayer2.player));
  }

  @Test
  public void testPlayerJoinGameSuccessForTwoAiClients ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (2).build (eventBus);
    final String playerName1 = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final String playerName2 = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer2");

    addAiClientAndMockPlayerToGameServer (playerName1, mpc);
    final ClientPlayerTuple clientPlayer2 = addAiClientAndMockPlayerToGameServer (playerName2, mpc);

    verify (mockAiClientCommunicator).sendToAllExcept (eq (clientPlayer2.client),
                                                       argThat (allOf (Matchers.isA (PlayerJoinGameSuccessEvent.class),
                                                                       new PersonIdentityMatcher (
                                                                               PersonIdentity.NON_SELF))));

    verify (mockAiClientCommunicator).sendTo (eq (clientPlayer2.client),
                                              argThat (allOf (Matchers.isA (PlayerJoinGameSuccessEvent.class),
                                                              new PersonIdentityMatcher (PersonIdentity.SELF))));

    verifyZeroInteractions (mockHumanClientCommunicator);
    verifyZeroInteractions (mockSpectatorClientCommunicator);

    assertTrue (mpc.isPlayerInGame (clientPlayer2.player));
  }

  @Test
  public void testHumanPlayerJoinGameDenied ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventBus);
    final Remote client = joinHumanClientToGameServer ();

    final String playerName = "TestPlayer1";
    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName), client);

    final PlayerPacket player = createMockHumanPlayer (playerName);
    // make up a reason... doesn't have to be true :)
    final PlayerJoinGameDeniedEvent.Reason reason = PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME;
    final DeniedEvent <PlayerJoinGameDeniedEvent.Reason> deniedEvent = new PlayerJoinGameDeniedEvent (playerName,
            reason);
    communicateEventFromCore (deniedEvent);
    verify (mockHumanClientCommunicator).sendTo (eq (client), eq (deniedEvent));
    assertFalse (mpc.isPlayerInGame (player));
  }

  @Test
  public void testAiPlayerJoinGameDenied ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (1).build (eventBus);
    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final Remote client = joinAiClientToGameServer (playerName);

    communicateEventFromClient (new AiPlayerJoinGameRequestEvent (playerName), client);

    final PlayerPacket player = createMockAiPlayer (playerName);
    // make up a reason... doesn't have to be true :)
    final PlayerJoinGameDeniedEvent.Reason reason = PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME;
    final DeniedEvent <PlayerJoinGameDeniedEvent.Reason> deniedEvent = new PlayerJoinGameDeniedEvent (playerName,
            reason);
    communicateEventFromCore (deniedEvent);
    verify (mockAiClientCommunicator).sendTo (eq (client), eq (deniedEvent));
    assertFalse (mpc.isPlayerInGame (player));
  }

  @Test
  public void testHumanPlayerLeaveGame ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventBus);
    final ClientPlayerTuple clientPlayer = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);

    mockCoreCommunicatorPlayersWith (clientPlayer.player ());

    disconnect (clientPlayer.client ());
    verify (mockCoreCommunicator).notifyRemovePlayerFromGame (eq (clientPlayer.player ()));
    communicateEventFromCore (new PlayerLeaveGameEvent (clientPlayer.player (), ImmutableSet.<PlayerPacket> of ()));

    // make sure nothing was sent to the disconnecting player
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer.client ()),
                                                           isA (PlayerLeaveGameEvent.class));
    assertFalse (mpc.isPlayerInGame (clientPlayer.player ()));
  }

  @Test
  public void testAiPlayerLeaveGame ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (1).build (eventBus);
    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer1");
    final ClientPlayerTuple clientPlayer = addAiClientAndMockPlayerToGameServer (playerName, mpc);

    mockCoreCommunicatorPlayersWith (clientPlayer.player ());

    disconnect (clientPlayer.client ());
    verify (mockCoreCommunicator).notifyRemovePlayerFromGame (eq (clientPlayer.player ()));
    communicateEventFromCore (new PlayerLeaveGameEvent (clientPlayer.player (), ImmutableSet.<PlayerPacket> of ()));

    // make sure nothing was sent to the disconnecting player
    verify (mockAiClientCommunicator, never ()).sendTo (eq (clientPlayer.client ()), isA (PlayerLeaveGameEvent.class));
    assertFalse (mpc.isPlayerInGame (clientPlayer.player ()));
  }

  @Test
  public void testSpectatorJoinGameSuccess ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (GameSettings.MAX_SPECTATORS).build (eventBus);
    final ClientPlayerTuple clientPlayer = addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);
    final Remote spectatorClient = addHumanClient ();
    addMockSpectatorToGameWithName ("TestSpectator", spectatorClient, mpc);
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer.client ()), isA (PlayerJoinGameSuccessEvent.class));
    verify (mockHumanClientCommunicator)
            .sendTo (eq (clientPlayer.client ()), isA (SpectatorJoinGameSuccessEvent.class));
    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient), isA (SpectatorJoinGameSuccessEvent.class));
  }

  @Test
  public void testSpectatorJoinGameDeniedDuplicatesPlayerName ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (GameSettings.MAX_SPECTATORS).build (eventBus);
    final ClientPlayerTuple clientPlayer = addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);
    final Remote spectatorClient = addHumanClient ();
    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestPlayer"), spectatorClient);
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer.client ()), isA (PlayerJoinGameSuccessEvent.class));
    final Matcher <SpectatorJoinGameDeniedEvent> matcher = new BaseMatcher <SpectatorJoinGameDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        if (!(arg0 instanceof SpectatorJoinGameDeniedEvent)) return false;
        return ((SpectatorJoinGameDeniedEvent) arg0).getReason ()
                .equals (SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_NAME);
      }

      @Override
      public void describeTo (final Description arg0)
      {
        arg0.appendText (SpectatorJoinGameDeniedEvent.class.getSimpleName () + ": ")
                .appendValue (SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_NAME);
      }
    };
    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient), argThat (matcher));
    verify (mockHumanClientCommunicator, never ()).sendTo (eq (clientPlayer.client ()), argThat (matcher));
  }

  @Test
  public void testSpectatorJoinGameDeniedDuplicatesSpectatorName ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (GameSettings.MAX_SPECTATORS).build (eventBus);
    addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);
    final Remote spectatorClient1 = addHumanClient ();
    addMockSpectatorToGameWithName ("TestSpectator", spectatorClient1, mpc);
    final Remote spectatorClient2 = addHumanClient ();
    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestSpectator"), spectatorClient2);
    final Matcher <SpectatorJoinGameDeniedEvent> matcher = new BaseMatcher <SpectatorJoinGameDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        if (!(arg0 instanceof SpectatorJoinGameDeniedEvent)) return false;
        return ((SpectatorJoinGameDeniedEvent) arg0).getReason ()
                .equals (SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_NAME);
      }

      @Override
      public void describeTo (final Description arg0)
      {
        arg0.appendText (SpectatorJoinGameDeniedEvent.class.getSimpleName () + ": ")
                .appendValue (SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_NAME);
      }
    };
    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient2), argThat (matcher));
  }

  @Test
  public void testSpectatorJoinGameDeniedMaxSpectatorCountReached ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (1).build (eventBus);
    addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);
    final Remote spectatorClient1 = addHumanClient ();
    addMockSpectatorToGameWithName ("TestSpectator1", spectatorClient1, mpc);
    final Remote spectatorClient2 = addHumanClient ();
    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestSpectator2"), spectatorClient2);
    final Matcher <SpectatorJoinGameDeniedEvent> matcher = new BaseMatcher <SpectatorJoinGameDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        if (!(arg0 instanceof SpectatorJoinGameDeniedEvent)) return false;
        return ((SpectatorJoinGameDeniedEvent) arg0).getReason ()
                .equals (SpectatorJoinGameDeniedEvent.Reason.GAME_IS_FULL);
      }

      @Override
      public void describeTo (final Description arg0)
      {
        arg0.appendText (SpectatorJoinGameDeniedEvent.class.getSimpleName () + ": ")
                .appendValue (SpectatorJoinGameDeniedEvent.Reason.GAME_IS_FULL);
      }
    };
    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient2), argThat (matcher));
  }

  @Test
  public void testSpectatorJoinGameDeniedSpectatingDisabled ()
  {
    final MultiplayerController mpc = mpcBuilder.spectatorLimit (0).build (eventBus);
    addHumanClientAndMockPlayerToGameServer ("TestPlayer", mpc);
    final Remote spectatorClient = addHumanClient ();
    communicateEventFromClient (new SpectatorJoinGameRequestEvent ("TestSpectator"), spectatorClient);
    final Matcher <SpectatorJoinGameDeniedEvent> matcher = new BaseMatcher <SpectatorJoinGameDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        if (!(arg0 instanceof SpectatorJoinGameDeniedEvent)) return false;
        return ((SpectatorJoinGameDeniedEvent) arg0).getReason ()
                .equals (SpectatorJoinGameDeniedEvent.Reason.SPECTATING_DISABLED);
      }

      @Override
      public void describeTo (final Description arg0)
      {
        arg0.appendText (SpectatorJoinGameDeniedEvent.class.getSimpleName () + ": ")
                .appendValue (SpectatorJoinGameDeniedEvent.Reason.SPECTATING_DISABLED);
      }
    };
    verify (mockSpectatorClientCommunicator).sendTo (eq (spectatorClient), argThat (matcher));
  }

  @Test
  public void testValidPlayerClaimCountryResponseRequestEvent ()
  {
    // Create a game server with manual initial country assignment.
    final MultiplayerController mpc = mpcBuilder.initialCountryAssignment (InitialCountryAssignment.MANUAL)
            .build (eventBus);

    final ClientPlayerTuple clientPlayer = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);

    mockCoreCommunicatorPlayersWith (clientPlayer.player ());

    // Request that the player/client claim an available country.
    communicateEventFromCore (new PlayerClaimCountryRequestEvent (clientPlayer.player (),
            ImmutableSet.<CountryPacket> of ()));
    verify (mockHumanClientCommunicator).sendTo (eq (clientPlayer.client ()),
                                                 isA (PlayerClaimCountryRequestEvent.class));

    // Simulate player/client claiming a country.
    final Event event = new PlayerClaimCountryResponseRequestEvent ("Test Country 1");
    communicateEventFromClient (event, clientPlayer.client ());

    // Verify that player/client's country claim was published.
    assertEventFiredExactlyOnce (PlayerClaimCountryResponseRequestEvent.class);
    assertEventFiredExactlyOnce (event);
  }

  @Test
  public void testInvalidPlayerClaimCountryResponseRequestEventIgnoredBecauseClientIsNotAPlayer ()
  {
    // Create a game server with manual initial country assignment.
    mpcBuilder.initialCountryAssignment (InitialCountryAssignment.MANUAL).build (eventBus);

    final Remote client = joinHumanClientToGameServer ();

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
            .build (eventBus);

    final ClientPlayerTuple first = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);
    final ClientPlayerTuple second = addHumanClientAndMockPlayerToGameServer ("TestPlayer2", mpc);

    // Request that the player/client claim an available country.
    communicateEventFromCore (new PlayerClaimCountryRequestEvent (first.player (), ImmutableSet.<CountryPacket> of ()));
    verify (mockHumanClientCommunicator).sendTo (eq (first.client ()), isA (PlayerClaimCountryRequestEvent.class));

    mockCoreCommunicatorPlayersWith (first.player (), second.player ());

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
            .build (eventBus);

    final ClientPlayerTuple first = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);
    final ClientPlayerTuple second = addHumanClientAndMockPlayerToGameServer ("TestPlayer2", mpc);

    mockCoreCommunicatorPlayersWith (first.player (), second.player ());

    // Request that the first player/client claim an available country.
    final Event claimCountryRequestEvent1 = new PlayerClaimCountryRequestEvent (first.player (),
            ImmutableSet.<CountryPacket> of ());
    communicateEventFromCore (claimCountryRequestEvent1);
    verify (mockHumanClientCommunicator).sendTo (first.client (), claimCountryRequestEvent1);
    // Make sure that the request was not sent to the second player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (second.client (), claimCountryRequestEvent1);

    // Simulate & verify first player/client claiming a country.
    final Event claimCountryResponseRequestEvent1 = new PlayerClaimCountryResponseRequestEvent ("Test Country 1");
    communicateEventFromClient (claimCountryResponseRequestEvent1, first.client ());
    assertLastEventWas (claimCountryResponseRequestEvent1);

    // Request that the second player/client claim an available country.
    final Event claimCountryRequestEvent2 = new PlayerClaimCountryRequestEvent (second.player (),
            ImmutableSet.<CountryPacket> of ());
    communicateEventFromCore (claimCountryRequestEvent2);
    verify (mockHumanClientCommunicator).sendTo (second.client (), claimCountryRequestEvent2);
    // Make sure that the request was not sent to the first player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (first.client (), claimCountryRequestEvent2);

    // Simulate & verify second player/client claiming a country.
    final Event claimCountryResponseRequestEvent2 = new PlayerClaimCountryResponseRequestEvent ("Test Country 2");
    communicateEventFromClient (claimCountryResponseRequestEvent2, second.client ());
    assertLastEventWas (claimCountryResponseRequestEvent2);

    // Request that the first player/client claim an available country.
    final Event claimCountryRequestEvent3 = new PlayerClaimCountryRequestEvent (first.player (),
            ImmutableSet.<CountryPacket> of ());
    communicateEventFromCore (claimCountryRequestEvent3);
    verify (mockHumanClientCommunicator).sendTo (first.client (), claimCountryRequestEvent3);
    // Make sure that the request was not sent to the second player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (second.client (), claimCountryRequestEvent3);

    // Simulate & verify first player/client claiming a country.
    final Event claimCountryResponseRequestEvent3 = new PlayerClaimCountryResponseRequestEvent ("Test Country 3");
    communicateEventFromClient (claimCountryResponseRequestEvent3, first.client ());
    assertLastEventWas (claimCountryResponseRequestEvent3);

    // Request that the second player/client claim an available country.
    final Event claimCountryRequestEvent4 = new PlayerClaimCountryRequestEvent (second.player (),
            ImmutableSet.<CountryPacket> of ());
    communicateEventFromCore (claimCountryRequestEvent4);
    verify (mockHumanClientCommunicator).sendTo (second.client (), claimCountryRequestEvent4);
    // Make sure that the request was not sent to the first player/client.
    verify (mockHumanClientCommunicator, never ()).sendTo (first.client (), claimCountryRequestEvent4);

    // Simulate & verify second player/client claiming a country.
    final Event claimCountryResponseRequestEvent4 = new PlayerClaimCountryResponseRequestEvent ("Test Country 4");
    communicateEventFromClient (claimCountryResponseRequestEvent4, second.client ());
    assertLastEventWas (claimCountryResponseRequestEvent4);

    // Request that the first player/client claim an available country.
    final Event claimCountryRequestEvent5 = new PlayerClaimCountryRequestEvent (first.player (),
            ImmutableSet.<CountryPacket> of ());
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
            .build (eventBus);

    final ClientPlayerTuple first = addHumanClientAndMockPlayerToGameServer ("TestPlayer1", mpc);

    mockCoreCommunicatorPlayersWith (first.player ());

    // Simulate player/client claiming a country BEFORE receiving a request to do so from the server.
    final Event event = communicateEventFromClient (new PlayerClaimCountryResponseRequestEvent ("Test Country 1"),
                                                    first.client ());

    // Verify that player/client's country claim was NOT published.
    assertLastEventWas (event);
  }

  @Test
  public void testStalePlayerPacketDataIsUpdatedOnAccess ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventBus);

    final String playerName = "TestPlayer";
    addHumanClientAndMockPlayerToGameServer (playerName, mpc);

    final PlayerPacket player = createMockHumanPlayer (playerName);
    final PlayerPacket updatedPlayer = createMockHumanPlayer (playerName);

    // here's the updated part
    when (updatedPlayer.getArmiesInHand ()).thenReturn (5);
    mockCoreCommunicatorPlayersWith (updatedPlayer);

    // TODO ... need some mechanism for polling player data from core/server
  }

  // <<<<<<<<<<<< Test helper facilities >>>>>>>>>>>>>> //

  // unit test 1 for bug detailed in PERIL-100: https://forerunnergames.atlassian.net/browse/PERIL-100
  @Test
  public void testHumanClientDisconnectAfterSendingPlayerJoinGameRequest ()
  {
    final MultiplayerController mpc = mpcBuilder.build (eventBus);
    final Remote client = joinHumanClientToGameServer ();
    final String playerName = "TestPlayer";
    final PlayerPacket player = createMockHumanPlayer (playerName);
    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName), client);
    // disconnect client
    disconnect (client);
    assertFalse (mpc.isClientInServer (client));
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player), mpc.getPlayerLimit ()));
    verify (mockCoreCommunicator).notifyRemovePlayerFromGame (eq (player));
  }

  // unit test 2 for bug detailed in PERIL-100: https://forerunnergames.atlassian.net/browse/PERIL-100
  @Test
  public void testAiClientDisconnectAfterSendingPlayerJoinGameRequest ()
  {
    final MultiplayerController mpc = mpcBuilder.aiPlayerLimit (1).build (eventBus);
    final String playerName = GameSettings.getAiPlayerNameWithMandatoryClanTag ("TestPlayer");
    final Remote client = joinAiClientToGameServer (playerName);
    final PlayerPacket player = createMockAiPlayer (playerName);
    communicateEventFromClient (new AiPlayerJoinGameRequestEvent (playerName), client);
    // disconnect client
    disconnect (client);
    assertFalse (mpc.isClientInServer (client));
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player), mpc.getPlayerLimit ()));
    verify (mockCoreCommunicator).notifyRemovePlayerFromGame (eq (player));
  }

  private PlayerPacket createMockHumanPlayer (final String playerName)
  {
    final PlayerPacket player = mock (PlayerPacket.class);
    when (player.getName ()).thenReturn (playerName);
    when (player.hasName (eq (playerName))).thenReturn (true);
    when (player.getSentience ()).thenReturn (PersonSentience.HUMAN);
    when (player.has (PersonSentience.HUMAN)).thenReturn (true);
    when (player.has (PersonSentience.AI)).thenReturn (false);
    when (player.is (eq (player))).thenReturn (true);
    when (player.isNot (argThat (not (equalTo (player))))).thenReturn (true);
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
    when (player.isNot (argThat (not (equalTo (player))))).thenReturn (true);
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
    when (spectator.isNot (argThat (not (equalTo (spectator))))).thenReturn (true);
    when (spectator.toString ()).thenReturn (spectatorName);

    return spectator;
  }

  private ClientPlayerTuple addHumanClientAndMockPlayerToGameServer (final String playerName,
                                                                     final MultiplayerController mpc)
  {
    final Remote client = joinHumanClientToGameServer ();
    final PlayerPacket player = addMockHumanPlayerToGameWithName (playerName, client, mpc);

    return new ClientPlayerTuple (client, player);
  }

  private ClientPlayerTuple addAiClientAndMockPlayerToGameServer (final String playerName,
                                                                  final MultiplayerController mpc)
  {
    assert GameSettings.isValidAiPlayerNameWithMandatoryClanTag (playerName);
    final Remote client = joinAiClientToGameServer (playerName);
    final PlayerPacket player = addMockAiPlayerToGameWithName (playerName, client, mpc);

    return new ClientPlayerTuple (client, player);
  }

  private Remote joinHumanClientToGameServer ()
  {
    final Remote client = addHumanClient ();
    verify (mockHumanClientCommunicator).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    return client;
  }

  private Remote joinAiClientToGameServer (final String playerName)
  {
    final Remote client = addAiClient (playerName);
    verify (mockAiClientCommunicator).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    return client;
  }

  private PlayerPacket addMockHumanPlayerToGameWithName (final String playerName,
                                                         final Remote client,
                                                         final MultiplayerController mpc)
  {
    assert mpc.getPlayerLimitFor (PersonSentience.HUMAN) > 0;
    final PlayerPacket player = createMockHumanPlayer (playerName);
    communicateEventFromClient (new HumanPlayerJoinGameRequestEvent (playerName), client);
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player), mpc.getPlayerLimit ()));
    verify (mockHumanClientCommunicator).sendTo (eq (client), isA (PlayerJoinGameSuccessEvent.class));
    assertTrue (mpc.isPlayerInGame (player));

    return player;
  }

  private PlayerPacket addMockAiPlayerToGameWithName (final String playerName,
                                                      final Remote client,
                                                      final MultiplayerController mpc)
  {
    assert GameSettings.isValidAiPlayerNameWithMandatoryClanTag (playerName);
    assert mpc.getPlayerLimitFor (PersonSentience.AI) > 0;
    final PlayerPacket player = createMockAiPlayer (playerName);
    communicateEventFromClient (new AiPlayerJoinGameRequestEvent (playerName), client);
    communicateEventFromCore (new PlayerJoinGameSuccessEvent (player, ImmutableSet.of (player), mpc.getPlayerLimit ()));
    verify (mockAiClientCommunicator).sendTo (eq (client), isA (PlayerJoinGameSuccessEvent.class));
    assertTrue (mpc.isPlayerInGame (player));

    return player;
  }

  private SpectatorPacket addMockSpectatorToGameWithName (final String spectatorName,
                                                          final Remote client,
                                                          final MultiplayerController mpc)
  {
    assert mpc.getSpectatorLimit () > 0;
    communicateEventFromClient (new SpectatorJoinGameRequestEvent (spectatorName), client);
    verify (mockSpectatorClientCommunicator).sendTo (eq (client), isA (SpectatorJoinGameSuccessEvent.class));

    return createMockSpectator (spectatorName);
  }

  private void mockCoreCommunicatorPlayersWith (final PlayerPacket... players)
  {
    when (mockCoreCommunicator.fetchCurrentPlayerData ()).thenReturn (ImmutableSet.copyOf (players));
    // mock core communicator request publishing
    doAnswer (new Answer <InvocationOnMock> ()
    {
      @Override
      public InvocationOnMock answer (final InvocationOnMock invocation) throws Throwable
      {
        eventBus.publish ((Event) invocation.getArguments () [1]);
        return null;
      }
    }).when (mockCoreCommunicator).publishPlayerResponseRequestEvent (any (PlayerPacket.class),
                                                                      any (ResponseRequestEvent.class),
                                                                      any (PlayerInputRequestEvent.class));
    doAnswer (new Answer <InvocationOnMock> ()
    {
      @Override
      public InvocationOnMock answer (final InvocationOnMock invocation) throws Throwable
      {
        eventBus.publish ((Event) invocation.getArguments () [1]);
        return null;
      }
    }).when (mockCoreCommunicator).publishPlayerRequestEvent (any (PlayerPacket.class), any (PlayerRequestEvent.class));
  }

  @SuppressWarnings ("unused")
  private void assertLastEventWasType (final Class <?> eventType)
  {
    assertTrue ("Expected last event was type [" + eventType.getSimpleName () + "], but was ["
                        + eventHandler.lastEventType () + "] All events (newest to oldest): ["
                        + eventHandler.getAllEvents () + "].", eventHandler.lastEventWasType (eventType));
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

  private ClientCommunicationEvent communicateEventFromClient (final Event event, final Remote client)
  {
    final ClientCommunicationEvent clientCommunicationEvent = new ClientCommunicationEvent (event, client);
    eventBus.publish (clientCommunicationEvent);

    return clientCommunicationEvent;
  }

  private void communicateEventFromCore (final Event event)
  {
    eventBus.publish (event);
  }

  private void connect (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    eventBus.publish (new ClientConnectionEvent (client));
  }

  private void disconnect (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    eventBus.publish (new ClientDisconnectionEvent (client));
  }

  private ServerConfiguration createDefaultServerConfig ()
  {
    return new DefaultServerConfiguration (DEFAULT_TEST_SERVER_ADDRESS, DEFAULT_TEST_SERVER_PORT);
  }

  private Remote addHumanClient ()
  {
    final Remote client = createHumanClient ();
    addHumanClient (client);

    return client;
  }

  private Remote addHumanClientWithAddress (final String address)
  {
    Arguments.checkIsNotNull (address, "address");

    final Remote client = createHumanClientWithAddress (address);
    addHumanClient (client);

    return client;
  }

  private Remote createHumanClient ()
  {
    return createHumanClientWithAddress ("forerunner.games");
  }

  private Remote createHumanClientWithAddress (final String address)
  {
    Arguments.checkIsNotNull (address, "address");

    final int port = 1000 + clientCount;
    return new KryonetRemote (clientCount++, new InetSocketAddress (address, port));
  }

  private Remote createHumanHost ()
  {
    return createHumanClientWithAddress (NetworkConstants.LOCALHOST_ADDRESS);
  }

  private void addHumanClient (final Remote client)
  {
    connect (client);
    eventBus.publish (new ClientCommunicationEvent (new HumanJoinGameServerRequestEvent (), client));
  }

  private Remote addAiClient (final String playerName)
  {
    final Remote client = createAiClient (playerName);
    addAiClient (client);

    return client;
  }

  private Remote createAiClient (final String playerName)
  {
    return new AiClient (playerName);
  }

  private void addAiClient (final Remote client)
  {
    connect (client);
    eventBus.publish (new ClientCommunicationEvent (new AiJoinGameServerRequestEvent (), client));
  }

  /*
   * Configurable test builder for MultiplayerController. Returns default values if left unchanged.
   */
  static class MultiplayerControllerBuilder
  {
    private final ClientConnector connector;
    private final PlayerCommunicator humanPlayerCommunicator;
    private final PlayerCommunicator aiPlayerCommunicator;
    private final SpectatorCommunicator spectatorCommunicator;
    private final CoreCommunicator coreCommunicator;
    // game configuration fields
    private final GameMode gameMode = GameMode.CLASSIC;
    private final PlayMapMetadata playMapMetadata = new DefaultPlayMapMetadata (
            GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_NAME, GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_TYPE, gameMode,
            GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_DIR_NAME, GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_DIR_TYPE);
    private InitialCountryAssignment initialCountryAssignment = ClassicGameRules.DEFAULT_INITIAL_COUNTRY_ASSIGNMENT;
    // game server configuration fields
    private String gameServerName = DEFAULT_TEST_GAME_SERVER_NAME;
    private GameServerType gameServerType = DEFAULT_GAME_SERVER_TYPE;
    // server configuration fields
    private String serverAddress = DEFAULT_TEST_SERVER_ADDRESS;
    private int serverPort = DEFAULT_TEST_SERVER_PORT;
    private int humanPlayerLimit = ClassicGameRules.DEFAULT_PLAYER_LIMIT;
    private int aiPlayerLimit = GameSettings.MIN_AI_PLAYERS;
    private int spectatorLimit = GameSettings.DEFAULT_SPECTATOR_LIMIT;
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

      this.humanPlayerLimit = humanPlayerLimit;
      return this;
    }

    MultiplayerControllerBuilder aiPlayerLimit (final int aiPlayerLimit)
    {
      Arguments.checkIsNotNegative (aiPlayerLimit, "aiPlayerLimit");

      this.aiPlayerLimit = aiPlayerLimit;
      return this;
    }

    MultiplayerControllerBuilder spectatorLimit (final int spectatorLimit)
    {
      Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");

      this.spectatorLimit = spectatorLimit;
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

    MultiplayerController build (final MBassador <Event> eventBus)
    {
      Arguments.checkIsNotNull (eventBus, "eventBus");

      final PersonLimits personLimits = PersonLimits.builder ().humanPlayers (humanPlayerLimit)
              .aiPlayers (aiPlayerLimit).spectators (spectatorLimit).build ();

      final GameRules gameRules = GameRulesFactory.create (gameMode, personLimits.getPlayerLimit (), winPercent,
                                                           totalCountryCount, initialCountryAssignment);

      final GameConfiguration gameConfig = new DefaultGameConfiguration (gameMode, personLimits, winPercent,
              initialCountryAssignment, playMapMetadata, gameRules);

      final ServerConfiguration serverConfig = new DefaultServerConfiguration (serverAddress, serverPort);

      final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (gameServerName,
              gameServerType, gameConfig, serverConfig);

      final MultiplayerController controller = new MultiplayerController (gameServerConfig, connector,
              humanPlayerCommunicator, aiPlayerCommunicator, spectatorCommunicator, coreCommunicator, eventBus);

      controller.initialize ();

      return controller;
    }

    // add game mode and/or initial-country-assignment later if needed

    private MultiplayerControllerBuilder (final ClientConnector connector,
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

  private final class PersonIdentityMatcher extends BaseMatcher <PlayerJoinGameSuccessEvent>
  {
    private final PersonIdentity identity;

    PersonIdentityMatcher (final PersonIdentity identity)
    {
      this.identity = identity;
    }

    @Override
    public boolean matches (final Object item)
    {
      return item instanceof PlayerJoinGameSuccessEvent
              && ((PlayerJoinGameSuccessEvent) item).getIdentity () == identity;
    }

    @Override
    public void describeTo (final Description description)
    {
      description.appendText (Strings.format ("matches {}: [{}].", identity.getClass ().getSimpleName (), identity));
    }
  }

  private final class ClientPlayerTuple
  {
    private final Remote client;
    private final PlayerPacket player;

    ClientPlayerTuple (final Remote client, final PlayerPacket player)
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

    @Override
    public String toString ()
    {
      return Strings.format ("{}: Client: {} | Player: {}", getClass ().getSimpleName (), client, player);
    }

    public Remote client ()
    {
      return client;
    }

    public PlayerPacket player ()
    {
      return player;
    }
  }
}
