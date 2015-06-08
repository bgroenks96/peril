package com.forerunnergames.peril.server.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.core.shared.application.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerLeaveGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRemote;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.peril.server.EventBusHandler;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.ClientCommunicator;
import com.forerunnergames.tools.net.ClientConnector;
import com.forerunnergames.tools.net.DefaultServerConfiguration;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.ServerConfiguration;
import com.forerunnergames.tools.net.events.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.DeniedEvent;
import com.forerunnergames.tools.net.events.SuccessEvent;

import java.net.InetSocketAddress;

import net.engio.mbassy.bus.MBassador;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplayerControllerTest
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerControllerTest.class);
  private static final String DEFAULT_TEST_SERVER_NAME = "test-server";
  private static final String DEFAULT_TEST_SERVER_ADDR = "server@test";
  private static final int DEFAULT_TEST_PORT = 8888;

  private final ClientConnector mockConnector = mock (ClientConnector.class);
  private final ClientCommunicator mockCommunicator = mock (ClientCommunicator.class);
  private final MBassador <Event> eventBus = EventBusFactory.create ();
  private final EventBusHandler handler = new EventBusHandler (eventBus);
  private final MultiplayerControllerBuilder builder = builder (mockConnector,
                                                                new PlayerCommunicator (mockCommunicator), eventBus);

  private int clientCount;

  @Test
  public void testSuccessfulHostClientJoinGameServer ()
  {
    // for logging clarity
    log.info ("<==== testSuccessfulHostClientJoinGameServer ====>");

    final MultiplayerController mpc = builder.build ();
    final Remote host = createHost ();

    connect (host);
    final ServerConfiguration config = createConfig (mpc);
    eventBus.publish (communication (new JoinGameServerRequestEvent (config), host));
    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {

      @Override
      public boolean matches (Object arg0)
      {
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchConfig = matchEvent.getConfiguration ();
        return matchConfig.getServerName ().equals (config.getServerName ())
                && matchConfig.getServerTcpPort () == host.getPort ();
      }

      @Override
      public void describeTo (Description arg0)
      {
      }
    };
    verify (mockCommunicator, only ()).sendTo (eq (host), argThat (successEventMatcher));
  }

  @Test
  public void testSuccessfulClientJoinGameServer ()
  {
    log.info ("<==== testSuccessfulClientJoinGameServer ====>");

    final MultiplayerController mpc = builder.build ();

    // add and connect host client
    addHost (mpc);
    // weak verify for host; sufficient for this test case
    verify (mockCommunicator, only ()).sendTo (any (Remote.class), isA (JoinGameServerSuccessEvent.class));

    final Remote client = createClient ();
    connect (client);
    final ServerConfiguration config = createConfig (mpc);
    eventBus.publish (communication (new JoinGameServerRequestEvent (config), client));
    final BaseMatcher <JoinGameServerSuccessEvent> successEventMatcher = new BaseMatcher <JoinGameServerSuccessEvent> ()
    {

      @Override
      public boolean matches (Object arg0)
      {
        final JoinGameServerSuccessEvent matchEvent = (JoinGameServerSuccessEvent) arg0;
        final ServerConfiguration matchConfig = matchEvent.getConfiguration ();
        return matchConfig.getServerName ().equals (config.getServerName ())
                && matchConfig.getServerTcpPort () == client.getPort ();
      }

      @Override
      public void describeTo (Description arg0)
      {
      }
    };
    verify (mockCommunicator).sendTo (eq (client), argThat (successEventMatcher));
  }

  @Test
  public void testClientJoinRequestBeforeHostDenied ()
  {
    log.info ("<==== testClientJoinRequestBeforeHostDenied ====>");

    final MultiplayerController mpc = builder.build ();
    final Remote client = createClient ();

    connect (client);
    final ServerConfiguration config = createConfig (mpc);
    eventBus.publish (communication (new JoinGameServerRequestEvent (config), client));
    final BaseMatcher <JoinGameServerDeniedEvent> denialEventMatcher = new BaseMatcher <JoinGameServerDeniedEvent> ()
    {

      @Override
      public boolean matches (Object arg0)
      {
        final JoinGameServerDeniedEvent matchEvent = (JoinGameServerDeniedEvent) arg0;
        final ServerConfiguration matchConfig = matchEvent.getConfiguration ();
        // for future reference, JoinGameServerDenied even should really set the client IP for the return config...
        return matchConfig.getServerName ().equals (config.getServerName ())
                && matchConfig.getServerTcpPort () == config.getServerTcpPort ();
      }

      @Override
      public void describeTo (Description arg0)
      {
      }
    };
    verify (mockCommunicator, only ()).sendTo (eq (client), argThat (denialEventMatcher));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testClientJoinRequestWithGameServerFullDenied ()
  {
    log.info ("<==== testClientJoinRequestWithGameServerFullDenied ====>");

    final MultiplayerController mpc = builder.build ();

    // add and connect host client
    addHost (mpc);
    addClient (mpc);
    // weak verify for host and client success events
    verify (mockCommunicator, times (2)).sendTo (any (Remote.class), isA (JoinGameServerSuccessEvent.class));

    final Remote client = createClient ();
    connect (client);
    final ServerConfiguration config = createConfig (mpc);
    eventBus.publish (communication (new JoinGameServerRequestEvent (config), client));
    final BaseMatcher <JoinGameServerDeniedEvent> deniedEventMatcher = new BaseMatcher <JoinGameServerDeniedEvent> ()
    {

      @Override
      public boolean matches (Object arg0)
      {
        final JoinGameServerDeniedEvent matchEvent = (JoinGameServerDeniedEvent) arg0;
        final ServerConfiguration matchConfig = matchEvent.getConfiguration ();
        // for future reference, JoinGameServerDenied even should really set the client IP for the return config...
        return matchConfig.getServerName ().equals (config.getServerName ())
                && matchConfig.getServerTcpPort () == config.getServerTcpPort ();
      }

      @Override
      public void describeTo (Description arg0)
      {
      }
    };
    verify (mockCommunicator).sendTo (eq (client), argThat (deniedEventMatcher));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testValidPlayerJoinGameRequestPublished ()
  {
    log.info ("<==== testValidPlayerJoinGameRequestPublished ====>");

    final MultiplayerController mpc = builder.build ();

    final Remote host = addHost (mpc);
    verify (mockCommunicator, only ()).sendTo (eq (host), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player";
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), host);
  }

  @Test
  public void testIgnorePlayerJoinGameRequestBeforeJoiningGameServer ()
  {
    log.info ("<==== testIgnorePlayerJoinGameRequestBeforeJoiningGameServer ====>");

    final MultiplayerController mpc = builder.build ();

    final Remote host = addHost (mpc);
    verify (mockCommunicator, only ()).sendTo (eq (host), isA (JoinGameServerSuccessEvent.class));

    final Remote client = createClient ();
    connect (client);
    final String playerName = "Test-Player";
    eventBus.publish (communication (new PlayerJoinGameRequestEvent (playerName), client));

    // assert that no event was published after receiving bad request
    assertTrue (handler.lastEventWasType (ClientCommunicationEvent.class));
    // verify the controller did not send anything via the communicator
    verifyNoMoreInteractions (mockCommunicator);
  }

  @Test
  public void testPlayerJoinGameSuccess ()
  {
    log.info ("<==== testPlayerJoinGameSuccess ====>");

    final MultiplayerController mpc = builder.build ();

    final Remote host = addHost (mpc);
    verify (mockCommunicator, only ()).sendTo (eq (host), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player-0";
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), host);

    final PlayerPacket mockPacket = mock (PlayerPacket.class);
    when (mockPacket.getName ()).thenReturn (playerName);
    final SuccessEvent successEvent = new PlayerJoinGameSuccessEvent (mockPacket);
    eventBus.publish (successEvent);
    verify (mockCommunicator).sendTo (eq (host), eq (successEvent));
    assertTrue (mpc.isPlayerInGame (mockPacket));
  }

  @Test
  public void testPlayerJoinGameDenied ()
  {
    log.info ("<==== testPlayerJoinGameDenied ====>");

    final MultiplayerController mpc = builder.build ();

    final Remote host = addHost (mpc);
    verify (mockCommunicator, only ()).sendTo (eq (host), any (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player-0";
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), host);

    final PlayerPacket mockPacket = mock (PlayerPacket.class);
    when (mockPacket.getName ()).thenReturn (playerName);
    // make up a reason... doesn't have to be true :)
    final PlayerJoinGameDeniedEvent.Reason reason = PlayerJoinGameDeniedEvent.Reason.DUPLICATE_ID;
    final DeniedEvent <PlayerJoinGameDeniedEvent.Reason> deniedEvent = new PlayerJoinGameDeniedEvent (playerName,
            reason);
    eventBus.publish (deniedEvent);
    verify (mockCommunicator).sendTo (eq (host), eq (deniedEvent));
    assertFalse (mpc.isPlayerInGame (mockPacket));
  }

  @Test
  public void testPlayerLeaveGameSuccess ()
  {
    log.info ("<==== testPlayerLeaveGameSuccess ====>");

    final MultiplayerController mpc = builder.build ();

    final Remote host = addHost (mpc);
    verify (mockCommunicator, only ()).sendTo (eq (host), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player-0";
    final PlayerPacket mockPacket = mock (PlayerPacket.class);
    when (mockPacket.getName ()).thenReturn (playerName);
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), host);
    eventBus.publish (new PlayerJoinGameSuccessEvent (mockPacket));
    verify (mockCommunicator).sendTo (eq (host), isA (PlayerJoinGameSuccessEvent.class));
    assertTrue (mpc.isPlayerInGame (mockPacket));

    eventBus.publish (new ClientDisconnectionEvent (host));
    // make sure nothing was sent to the disconnecting player
    verify (mockCommunicator, never ()).sendTo (eq (host), isA (PlayerLeaveGameSuccessEvent.class));
    assertTrue (handler.lastEventWasType (PlayerLeaveGameSuccessEvent.class));
    assertFalse (mpc.isPlayerInGame (mockPacket));
  }

  // <<<<<<<<<<<< Test helper facilities >>>>>>>>>>>>>> //

  private void publishAndAssert (final Event event, final Remote client)
  {
    eventBus.publish (communication (event, client));
    assertTrue (handler.lastEventWasType (event.getClass ()));
    assertEquals (event, handler.lastEvent (event.getClass ()));
  }

  private Remote createHost ()
  {
    return createClientWith (NetworkSettings.LOCALHOST_ADDRESS);
  }

  private Remote createClient ()
  {
    return createClientWith ("forerunnergames.com");
  }

  private Remote createClientWith (final String address)
  {
    Arguments.checkIsNotNull (address, "address");

    final int port = 1000 + clientCount;
    return new KryonetRemote (clientCount++, new InetSocketAddress (address, port));
  }

  private void connect (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    eventBus.publish (new ClientConnectionEvent (client));
  }

  private ClientCommunicationEvent communication (final Event event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    return new ClientCommunicationEvent (event, client);
  }

  // create default server config using current builder settings
  private ServerConfiguration createConfig (final MultiplayerController controller)
  {
    Arguments.checkIsNotNull (controller, "controller");

    return new DefaultServerConfiguration (builder.serverName, DEFAULT_TEST_SERVER_ADDR, builder.port);
  }

  private Remote addHost (final MultiplayerController mpc)
  {
    Arguments.checkIsNotNull (mpc, "mpc");

    final Remote host = createHost ();
    addClient (host, mpc);
    return host;
  }

  private Remote addClient (final MultiplayerController mpc)
  {
    Arguments.checkIsNotNull (mpc, "mpc");

    final Remote client = createClient ();
    addClient (client, mpc);
    return client;
  }

  private void addClient (final Remote client, final MultiplayerController mpc)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (mpc, "mpc");

    connect (client);
    eventBus.publish (communication (new JoinGameServerRequestEvent (createConfig (mpc)), client));
  }

  // convenience for fetching a new MultiplayerControllerBuilder
  // Note: package private visibility is intended; other test classes in package should have access.
  static final MultiplayerControllerBuilder builder (final ClientConnector connector,
                                                     final PlayerCommunicator communicator,
                                                     final MBassador <Event> eventBus)
  {
    return new MultiplayerControllerBuilder (connector, communicator, eventBus);
  }

  /*
   * Configurable test builder for MultiplayerController. Returns default values if left unchanged.
   */
  static class MultiplayerControllerBuilder
  {
    private final MBassador <Event> eventBus;
    private final ClientConnector connector;
    private final PlayerCommunicator communicator;

    private String serverName = DEFAULT_TEST_SERVER_NAME;
    private int port = DEFAULT_TEST_PORT;
    // game configuration fields
    private GameMode gameMode = GameMode.CLASSIC;
    private int playerLimit = ClassicGameRules.DEFAULT_PLAYER_LIMIT;
    private int winPercent = ClassicGameRules.DEFAULT_WIN_PERCENTAGE;
    private int totalCountryCount = ClassicGameRules.DEFAULT_TOTAL_COUNTRY_COUNT;
    private InitialCountryAssignment initialCountryAssignment = InitialCountryAssignment.RANDOM;

    private MultiplayerControllerBuilder (final ClientConnector connector,
                                          final PlayerCommunicator communicator,
                                          final MBassador <Event> eventBus)
    {
      this.connector = connector;
      this.communicator = communicator;
      this.eventBus = eventBus;
    }

    MultiplayerControllerBuilder serverName (final String serverName)
    {
      Arguments.checkIsNotNull (serverName, "serverName");

      this.serverName = serverName;
      return this;
    }

    MultiplayerControllerBuilder serverPort (final int port)
    {
      Arguments.checkIsNotNegative (port, "port");
      Arguments.checkUpperInclusiveBound (port, NetworkSettings.MAX_PORT_VALUE, "port");

      this.port = port;
      return this;
    }

    MultiplayerControllerBuilder playerLimit (final int playerLimit)
    {
      Arguments.checkIsNotNegative (playerLimit, "playerLimit");

      this.playerLimit = playerLimit;
      return this;
    }

    MultiplayerControllerBuilder winPercent (final int winPercent)
    {
      Arguments.checkIsNotNegative (winPercent, "winPercent");
      Arguments.checkUpperInclusiveBound (winPercent, 100, "winPercent");

      this.winPercent = winPercent;
      return this;
    }

    MultiplayerControllerBuilder countryCount (final int totalCountryCount)
    {
      Arguments.checkIsNotNegative (totalCountryCount, "totalCountryCount");

      this.totalCountryCount = totalCountryCount;
      return this;
    }

    // add game mode and/or initial-country-assignment later if needed

    MultiplayerController build ()
    {
      GameConfiguration config = new DefaultGameConfiguration (gameMode, playerLimit, winPercent, totalCountryCount,
              initialCountryAssignment);
      MultiplayerController controller = new MultiplayerController (serverName, port, config, connector, communicator,
              eventBus);
      controller.initialize ();
      return controller;
    }
  }
}
