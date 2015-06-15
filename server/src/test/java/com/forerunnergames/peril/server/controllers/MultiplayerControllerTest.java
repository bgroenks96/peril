package com.forerunnergames.peril.server.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.core.shared.application.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.events.client.request.CreateGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.CreateGameServerSuccessEvent;
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
import com.forerunnergames.tools.net.ClientConfiguration;
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

public class MultiplayerControllerTest
{
  private static final String DEFAULT_TEST_GAME_SERVER_NAME = "test-server";
  private static final GameServerType DEFAULT_GAME_SERVER_TYPE = GameServerType.DEDICATED;
  private static final String DEFAULT_TEST_SERVER_ADDRESS = "server@test";
  private static final int DEFAULT_TEST_SERVER_PORT = 8888;

  private final ClientConnector mockConnector = mock (ClientConnector.class);
  private final ClientCommunicator mockCommunicator = mock (ClientCommunicator.class);
  private final MBassador <Event> eventBus = EventBusFactory.create ();
  private final EventBusHandler handler = new EventBusHandler (eventBus);
  private final MultiplayerControllerBuilder builder = builder (mockConnector,
                                                                new PlayerCommunicator (mockCommunicator), eventBus);
  private int clientCount = 0;

  @Test
  public void testSuccessfulHostClientCreateGameServer ()
  {
    final MultiplayerController mpc = builder.gameServerType (GameServerType.HOST_AND_PLAY).build ();

    final Remote host = createHost ();
    connect (host);

    final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (DEFAULT_TEST_GAME_SERVER_NAME,
            GameServerType.HOST_AND_PLAY, mpc.getGameConfiguration (), createDefaultServerConfig ());
    eventBus.publish (communication (new CreateGameServerRequestEvent (gameServerConfig), host));

    final BaseMatcher <CreateGameServerSuccessEvent> successEventMatcher = new BaseMatcher <CreateGameServerSuccessEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (CreateGameServerSuccessEvent.class));
        final CreateGameServerSuccessEvent matchEvent = (CreateGameServerSuccessEvent) arg0;
        final GameServerConfiguration matchGameServerConfig = matchEvent.getGameServerConfiguration ();
        final ClientConfiguration matchClientConfig = matchEvent.getClientConfiguration ();
        return matchGameServerConfig.getServerAddress ().equals (gameServerConfig.getServerAddress ())
                && matchClientConfig.getClientAddress ().equals (host.getAddress ())
                && matchGameServerConfig.getServerTcpPort () == gameServerConfig.getServerTcpPort ()
                && matchClientConfig.getClientTcpPort () == host.getPort ()
                && matchGameServerConfig.getGameServerName ().equals (gameServerConfig.getGameServerName ())
                && matchGameServerConfig.getGameServerType () == gameServerConfig.getGameServerType ()
                && matchGameServerConfig.getGameMode () == gameServerConfig.getGameMode ()
                && matchGameServerConfig.getPlayerLimit () == gameServerConfig.getPlayerLimit ()
                && matchGameServerConfig.getWinPercentage () == gameServerConfig.getWinPercentage ()
                && matchGameServerConfig.getTotalCountryCount () == gameServerConfig.getTotalCountryCount ()
                && matchGameServerConfig.getInitialCountryAssignment () == gameServerConfig
                        .getInitialCountryAssignment ();
      }

      @Override
      public void describeTo (final Description arg0)
      {
      }
    };
    verify (mockCommunicator, only ()).sendTo (eq (host), argThat (successEventMatcher));
  }

  @Test
  public void testSuccessfulClientJoinGameServer ()
  {
    final MultiplayerController mpc = builder.build ();

    final Remote client = createClient ();
    connect (client);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    eventBus.publish (communication (new JoinGameServerRequestEvent (serverConfig), client));

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
    verify (mockCommunicator).sendTo (eq (client), argThat (successEventMatcher));
  }

  @Test
  public void testClientJoinRequestBeforeHostDenied ()
  {
    final MultiplayerController mpc = builder.gameServerType (GameServerType.HOST_AND_PLAY).build ();

    final Remote client = createClient ();
    connect (client);

    final ServerConfiguration serverConfig = createDefaultServerConfig ();
    eventBus.publish (communication (new JoinGameServerRequestEvent (serverConfig), client));

    final BaseMatcher <JoinGameServerDeniedEvent> denialEventMatcher = new BaseMatcher <JoinGameServerDeniedEvent> ()
    {
      @Override
      public boolean matches (final Object arg0)
      {
        assertThat (arg0, instanceOf (JoinGameServerDeniedEvent.class));
        final JoinGameServerDeniedEvent matchEvent = (JoinGameServerDeniedEvent) arg0;
        final ServerConfiguration matchServerConfig = matchEvent.getServerConfiguration ();
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
    verify (mockCommunicator, only ()).sendTo (eq (client), argThat (denialEventMatcher));
    verify (mockConnector, only ()).disconnect (eq (client));
  }

  @Test
  public void testValidPlayerJoinGameRequestPublished ()
  {
    final MultiplayerController mpc = builder.build ();

    final Remote client = addClient ();
    verify (mockCommunicator, only ()).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player";
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), client);
  }

  @Test
  public void testIgnorePlayerJoinGameRequestBeforeJoiningGameServer ()
  {
    final MultiplayerController mpc = builder.build ();

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
    final MultiplayerController mpc = builder.build ();

    final Remote client = addClient ();
    verify (mockCommunicator, only ()).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player-0";
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), client);

    final PlayerPacket mockPacket = mock (PlayerPacket.class);
    when (mockPacket.getName ()).thenReturn (playerName);
    final SuccessEvent successEvent = new PlayerJoinGameSuccessEvent (mockPacket);
    eventBus.publish (successEvent);
    verify (mockCommunicator).sendTo (eq (client), eq (successEvent));
    assertTrue (mpc.isPlayerInGame (mockPacket));
  }

  @Test
  public void testPlayerJoinGameDenied ()
  {
    final MultiplayerController mpc = builder.build ();

    final Remote client = addClient ();
    verify (mockCommunicator, only ()).sendTo (eq (client), any (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player-0";
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), client);

    final PlayerPacket mockPacket = mock (PlayerPacket.class);
    when (mockPacket.getName ()).thenReturn (playerName);
    // make up a reason... doesn't have to be true :)
    final PlayerJoinGameDeniedEvent.Reason reason = PlayerJoinGameDeniedEvent.Reason.DUPLICATE_ID;
    final DeniedEvent <PlayerJoinGameDeniedEvent.Reason> deniedEvent = new PlayerJoinGameDeniedEvent (playerName,
            reason);
    eventBus.publish (deniedEvent);
    verify (mockCommunicator).sendTo (eq (client), eq (deniedEvent));
    assertFalse (mpc.isPlayerInGame (mockPacket));
  }

  @Test
  public void testPlayerLeaveGameSuccess ()
  {
    final MultiplayerController mpc = builder.build ();

    final Remote client = addClient ();
    verify (mockCommunicator, only ()).sendTo (eq (client), isA (JoinGameServerSuccessEvent.class));

    final String playerName = "Test-Player-0";
    final PlayerPacket mockPacket = mock (PlayerPacket.class);
    when (mockPacket.getName ()).thenReturn (playerName);
    publishAndAssert (new PlayerJoinGameRequestEvent (playerName), client);
    eventBus.publish (new PlayerJoinGameSuccessEvent (mockPacket));
    verify (mockCommunicator).sendTo (eq (client), isA (PlayerJoinGameSuccessEvent.class));
    assertTrue (mpc.isPlayerInGame (mockPacket));

    eventBus.publish (new ClientDisconnectionEvent (client));
    // make sure nothing was sent to the disconnecting player
    verify (mockCommunicator, never ()).sendTo (eq (client), isA (PlayerLeaveGameSuccessEvent.class));
    assertTrue (handler.lastEventWasType (PlayerLeaveGameSuccessEvent.class));
    assertFalse (mpc.isPlayerInGame (mockPacket));
  }

  // <<<<<<<<<<<< Test helper facilities >>>>>>>>>>>>>> //

  // convenience method for fetching a new MultiplayerControllerBuilder
  // Note: package private visibility is intended; other test classes in package should have access.
  static MultiplayerControllerBuilder builder (final ClientConnector connector,
                                               final PlayerCommunicator communicator,
                                               final MBassador <Event> eventBus)
  {
    return new MultiplayerControllerBuilder (connector, communicator, eventBus);
  }

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

  private ServerConfiguration createDefaultServerConfig ()
  {
    return new DefaultServerConfiguration (DEFAULT_TEST_SERVER_ADDRESS, DEFAULT_TEST_SERVER_PORT);
  }

  private Remote addHost ()
  {
    final Remote host = createHost ();
    addClientWithServerAddress (host, NetworkSettings.LOCALHOST_ADDRESS);
    return host;
  }

  private Remote addClient ()
  {
    final Remote client = createClient ();
    addClient (client);
    return client;
  }

  private void addClient (final Remote client)
  {
    connect (client);
    eventBus.publish (communication (new JoinGameServerRequestEvent (createDefaultServerConfig ()), client));
  }

  private void addClientWithServerAddress (final Remote client, final String serverAddress)
  {
    connect (client);
    eventBus.publish (communication (new JoinGameServerRequestEvent (new DefaultServerConfiguration (serverAddress,
            DEFAULT_TEST_SERVER_PORT)), client));
  }

  /*
   * Configurable test builder for MultiplayerController. Returns default values if left unchanged.
   */
  static class MultiplayerControllerBuilder
  {
    private final MBassador <Event> eventBus;
    private final ClientConnector connector;
    private final PlayerCommunicator communicator;
    // game configuration fields
    private final GameMode gameMode = GameMode.CLASSIC;
    private final InitialCountryAssignment initialCountryAssignment = InitialCountryAssignment.RANDOM;
    // game server configuration fields
    private String gameServerName = DEFAULT_TEST_GAME_SERVER_NAME;
    private GameServerType gameServerType = DEFAULT_GAME_SERVER_TYPE;
    // server configuration fields
    private int serverPort = DEFAULT_TEST_SERVER_PORT;
    private int playerLimit = ClassicGameRules.DEFAULT_PLAYER_LIMIT;
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

    MultiplayerControllerBuilder serverPort (final int serverPort)
    {
      Arguments.checkIsNotNegative (serverPort, "serverPort");
      Arguments.checkUpperInclusiveBound (serverPort, NetworkSettings.MAX_PORT_VALUE, "serverPort");

      this.serverPort = serverPort;
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

    MultiplayerController build ()
    {
      final GameConfiguration config = new DefaultGameConfiguration (gameMode, playerLimit, winPercent,
              totalCountryCount, initialCountryAssignment);
      final MultiplayerController controller = new MultiplayerController (gameServerName, gameServerType, serverPort,
              config, connector, communicator, eventBus);
      controller.initialize ();
      return controller;
    }

    // add game mode and/or initial-country-assignment later if needed

    private MultiplayerControllerBuilder (final ClientConnector connector,
                                          final PlayerCommunicator communicator,
                                          final MBassador <Event> eventBus)
    {
      this.connector = connector;
      this.communicator = communicator;
      this.eventBus = eventBus;
    }
  }
}
