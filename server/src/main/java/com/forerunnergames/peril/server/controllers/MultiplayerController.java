package com.forerunnergames.peril.server.controllers;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.recipientsFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.serverAddressFrom;
import static com.forerunnergames.tools.net.events.EventFluency.clientFrom;

import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.state.events.DestroyGameEvent;
import com.forerunnergames.peril.core.shared.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.NetworkEventHandler;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DestroyGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.core.shared.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.peril.server.communicators.CoreCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping.RegisteredClientPlayerNotFoundException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.client.ClientConnector;
import com.forerunnergames.tools.net.client.DefaultClientConfiguration;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseDeniedEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseSuccessEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiplayerController extends ControllerAdapter
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private final Multimap <PlayerPacket, PlayerInputRequestEvent> playerInputRequestEventCache = HashMultimap.create ();
  private final Map <String, Remote> playerJoinGameRequestCache;
  private final Set <Remote> clientsInServer;
  private final ClientPlayerMapping clientsToPlayers;
  private final ClientConnectorDaemon connectorDaemon = new ClientConnectorDaemon ();
  private final ClientConnector clientConnector;
  private final PlayerCommunicator playerCommunicator;
  private final MBassador <Event> eventBus;
  private final GameConfiguration gameConfig;
  private boolean shouldShutDown = false;
  private final String gameServerName;
  private final GameServerType gameServerType;
  private final int serverTcpPort;
  @Nullable
  private Remote host = null;
  @Nullable
  private NetworkEventHandler networkEventHandler = null;

  public MultiplayerController (final String gameServerName,
                                final GameServerType gameServerType,
                                final int serverTcpPort,
                                final GameConfiguration gameConfig,
                                final ClientConnector clientConnector,
                                final PlayerCommunicator playerCommunicator,
                                final CoreCommunicator coreCommunicator,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (gameServerName, "gameServerName");
    Arguments.checkIsNotNull (gameServerType, "gameServerType");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");
    Arguments.checkUpperInclusiveBound (serverTcpPort, NetworkSettings.MAX_PORT_VALUE, "serverTcpPort");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (clientConnector, "clientConnector");
    Arguments.checkIsNotNull (playerCommunicator, "playerCommunicator");
    Arguments.checkIsNotNull (coreCommunicator, "coreCommunicator");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.gameServerName = gameServerName;
    this.gameServerType = gameServerType;
    this.serverTcpPort = serverTcpPort;
    this.gameConfig = gameConfig;
    this.clientConnector = clientConnector;
    this.playerCommunicator = playerCommunicator;
    this.eventBus = eventBus;

    clientsInServer = Collections.synchronizedSet (new HashSet <Remote> ());
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type.
    clientsToPlayers = new ClientPlayerMapping (coreCommunicator, gameConfig.getPlayerLimit ());
    playerJoinGameRequestCache = Collections.synchronizedMap (new HashMap <String, Remote> ());
  }

  @Override
  public void initialize ()
  {
    eventBus.subscribe (this);
    eventBus.publish (new CreateGameEvent ());
    networkEventHandler = new ServerNetworkEventHandler (this);
  }

  @Override
  public boolean shouldShutDown ()
  {
    return shouldShutDown;
  }

  @Override
  public void shutDown ()
  {
    log.debug ("Shutting down [{}]", this);

    eventBus.publish (new DestroyGameEvent ());
    if (host != null) playerCommunicator.sendTo (host, new DestroyGameServerEvent ());
    eventBus.unsubscribe (this);
    connectorDaemon.threadPool.shutdown ();
    shouldShutDown = true;
  }

  public GameConfiguration getGameConfiguration ()
  {
    return gameConfig;
  }

  public boolean isPlayerInGame (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return clientsToPlayers.clientFor (player).isPresent ();
  }

  @Handler
  public void onEvent (final ClientConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);
    log.info ("Client [{}] connected.", clientFrom (event));

    connectorDaemon.onConnect (clientFrom (event));
  }

  @Handler
  public void onEvent (final ClientDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Remote client = clientFrom (event);

    log.info ("Client [{}] disconnected.", client);

    Optional <PlayerPacket> playerQuery;
    try
    {
      playerQuery = clientsToPlayers.playerFor (client);
    }
    catch (final RegisteredClientPlayerNotFoundException e)
    {
      log.error ("Error resolving client to player.", e);
      return;
    }

    if (!playerQuery.isPresent ())
    {
      log.warn ("Client [{}] disconnected but did not exist as a player.", client);
      return;
    }

    final PlayerPacket disconnectedPlayer = playerQuery.get ();
    final Event leaveGameEvent = new PlayerLeaveGameEvent (disconnectedPlayer);
    eventBus.publish (leaveGameEvent);
    sendToAllPlayersExcept (disconnectedPlayer, leaveGameEvent);
    remove (client);
  }

  // <<<<< inbound events from core module >>>>> //

  @Handler
  public void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final String playerName = playerNameFrom (event);

    // if no client mapping is available, silently ignore success event
    // this is to prevent failure under cases such as client disconnecting while join request is being processed
    if (!playerJoinGameRequestCache.containsKey (playerName)) return;

    final Remote client = playerJoinGameRequestCache.get (playerName);

    final PlayerPacket newPlayer = playerFrom (event);
    final Optional <PlayerPacket> oldPlayer = clientsToPlayers.put (client, newPlayer);
    if (oldPlayer.isPresent ())
    {
      // this generally shouldn't happen... but if it does, log a warning message
      log.warn ("Overwrote previous player mapping for client [{}] | old player: [{}] | new player: [{}]", client,
                oldPlayer.get (), newPlayer);
    }

    sendToAllPlayers (event);

    playerJoinGameRequestCache.remove (playerName);
  }

  @Handler
  public void onEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final String playerName = playerNameFrom (event);

    // if no client mapping is available, silently ignore denied event
    // this is to prevent failure under cases such as client disconnecting while join request is being processed
    if (!playerJoinGameRequestCache.containsKey (playerName)) return;

    playerCommunicator.sendTo (playerJoinGameRequestCache.get (playerName), event);

    playerJoinGameRequestCache.remove (playerName);
  }

  @Handler
  public void onEvent (final StatusMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    for (final PlayerPacket recipient : recipientsFrom (event))
    {
      sendToPlayer (recipient, event);
    }
  }

  @Handler
  public void onEvent (final ServerNotificationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    // We have separate handlers / senders for these ServerNotificationEvent's, so don't send twice.
    if (event instanceof PlayerLeaveGameEvent || event instanceof StatusMessageEvent) return;

    sendToAllPlayers (event);
  }

  @Handler
  public void onEvent (final PlayerInputRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final boolean wasAdded = playerInputRequestEventCache.put (playerFrom (event), event);
    assert wasAdded;

    sendToPlayer (playerFrom (event), event);
  }

  @Handler
  public void onEvent (final ResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    sendToAllPlayers (event);
  }

  @Handler
  public void onEvent (final ResponseDeniedEvent <?> event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    // TODO Only send to the requesting player.
    // TODO We need a sub-interface of ResponseDeniedEvent that includes the PlayerPacket.

    sendToAllPlayers (event);
  }

  // <<<<< remote inbound/outbound event communication >>>>> //

  @Handler
  void onEvent (final ClientCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    networkEventHandler.handle (event.getMessage (), event.getClient ());
  }

  void onEvent (final JoinGameServerRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    log.trace ("Event received [{}]", event);
    log.info ("Received join game server request from {}", client);

    // check if client is already in server
    if (clientsInServer.contains (client))
    {
      sendJoinGameServerDenied (client, event, "You have already joined this game server.");
      return;
    }

    // local host cannot join a dedicated server
    if (! isHostAndPlay () && isLocalHost (client))
    {
      sendJoinGameServerDenied (client, event, "You cannot join a dedicated server as localhost.");
      return;
    }

    // local host must join first in a host-and-play server
    if (isHostAndPlay () && !isHostConnected () && !isLocalHost (client))
    {
      sendJoinGameServerDenied (client, event, "Waiting for the host to connect...");
      return;
    }

    // only one local host can join a host-and-play server
    if (isHostAndPlay () && isHostConnected () && isLocalHost (client))
    {
      sendJoinGameServerDenied (client, event, "The host has already joined this server.");
      return;
    }

    // local host has joined the host-and-play server
    if (isHostAndPlay () && !isHostConnected () && isLocalHost (client)) host = client;

    sendJoinGameServerSuccess (client, serverAddressFrom (event), clientsToPlayers.players ());
  }

  void onEvent (final PlayerJoinGameRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    log.trace ("Event received [{}]", event);

    // client is connected but not in game server; just ignore request event
    if (!clientsInServer.contains (client))
    {
      log.warn ("Ignoring join game request from player [{}] | REASON: unrecognized client [{}].",
                playerNameFrom (event), client);
      return;
    }

    // spam guard: if client request is already being processed, ignore new request event
    if (playerJoinGameRequestCache.containsKey (playerNameFrom (event))) return;

    log.trace ("Received [{}] from player [{}]", event, playerNameFrom (event));

    playerJoinGameRequestCache.put (playerNameFrom (event), client);

    eventBus.publish (event);
  }

  void onEvent (final PlayerSelectCountryResponseRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    log.trace ("Event received [{}]", event);

    Optional <PlayerPacket> playerQuery;
    try
    {
      playerQuery = clientsToPlayers.playerFor (client);
    }
    catch (final RegisteredClientPlayerNotFoundException e)
    {
      log.error ("Error resolving client to player.", e);
      return;
    }

    if (!playerQuery.isPresent ())
    {
      log.warn ("Ignoring event [{}] from non-player client [{}]", event, client);
      return;
    }

    final PlayerPacket player = playerQuery.get ();

    if (!waitingForResponseToEventFromPlayer (PlayerSelectCountryRequestEvent.class, player))
    {
      log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding server request of type [{}] was sent to that player.",
                event, player, PlayerSelectCountryRequestEvent.class);
      return;
    }

    handlePlayerResponseTo (PlayerSelectCountryRequestEvent.class, event, player);
  }

  private void sendToPlayer (final PlayerPacket player, final Object object)
  {
    playerCommunicator.sendToPlayer (player, object, clientsToPlayers);
  }

  private void sendToAllPlayers (final Object object)
  {
    playerCommunicator.sendToAllPlayers (object, clientsToPlayers);
  }

  private void sendToAllPlayersExcept (final PlayerPacket player, final Object object)
  {
    playerCommunicator.sendToAllPlayersExcept (player, object, clientsToPlayers);
  }

  private void remove (final Remote client)
  {
    clientsInServer.remove (client);
    clientsToPlayers.remove (client);
  }

  // <<<<< internal event utility methods and types >>>>>> //

  private void sendJoinGameServerSuccess (final Remote client,
                                          final String serverAddress,
                                          final ImmutableSet <PlayerPacket> players)
  {
    final Event successEvent = new JoinGameServerSuccessEvent (createGameServerConfig (serverAddress),
            createClientConfig (client.getAddress (), client.getPort ()), players);
    playerCommunicator.sendTo (client, successEvent);
    clientsInServer.add (client);
    log.info ("Client [{}] successfully joined game server.", client);
  }

  private void sendJoinGameServerDenied (final Remote client,
                                         final JoinGameServerRequestEvent event,
                                         final String reason)
  {
    playerCommunicator.sendTo (client, new JoinGameServerDeniedEvent (event,
            new DefaultClientConfiguration (client.getAddress (), client.getPort ()), reason));
    clientConnector.disconnect (client);
    log.warn ("Denied [{}] from [{}]; REASON: {}", event, client, reason);
  }

  private boolean isLocalHost (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    return client.getAddress ().equals (NetworkSettings.LOCALHOST_ADDRESS);
  }

  private boolean isHostConnected ()
  {
    return host != null && clientsInServer.contains (host);
  }

  private boolean isHostAndPlay ()
  {
    return gameServerType.is (GameServerType.HOST_AND_PLAY);
  }

  private boolean isDedicated ()
  {
    return gameServerType.is (GameServerType.DEDICATED);
  }

  private GameServerConfiguration createGameServerConfig (final String serverAddress)
  {
    return new DefaultGameServerConfiguration (gameServerName, gameServerType, gameConfig,
            new DefaultServerConfiguration (serverAddress, serverTcpPort));
  }

  private ClientConfiguration createClientConfig (final String clientAddress, final int clientPort)
  {
    return new DefaultClientConfiguration (clientAddress, clientPort);
  }

  private boolean waitingForResponseToEventFromPlayer (final Class <? extends PlayerInputRequestEvent> requestClass,
                                                       final PlayerPacket player)
  {
    for (final PlayerInputRequestEvent request : playerInputRequestEventCache.get (player))
    {
      if (requestClass.isInstance (request)) return true;
    }

    return false;
  }

  private void handlePlayerResponseTo (final Class <? extends PlayerInputRequestEvent> requestClass,
                                       final ResponseRequestEvent responseRequest,
                                       final PlayerPacket player)
  {
    for (final PlayerInputRequestEvent request : playerInputRequestEventCache.get (player))
    {
      if (requestClass.isInstance (request))
      {
        eventBus.publish (responseRequest);
        final boolean wasRemoved = playerInputRequestEventCache.remove (player, request);
        assert wasRemoved;
        return;
      }
    }

    log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding request of type [{}] was sent to that player.",
              responseRequest, player, requestClass);
  }

  private final class ClientConnectorDaemon
  {
    private final ExecutorService threadPool = Executors.newCachedThreadPool ();

    public void onConnect (final Remote client)
    {
      Arguments.checkIsNotNull (client, "client");

      threadPool.execute (new WaitForConnectionTask (client));
    }

    private class WaitForConnectionTask implements Runnable
    {
      private final Remote client;

      WaitForConnectionTask (final Remote client)
      {
        Arguments.checkIsNotNull (client, "client");

        this.client = client;
      }

      @Override
      public void run ()
      {
        try
        {
          Thread.sleep (NetworkSettings.CLIENT_CONNECTION_TIMEOUT_MS);
        }
        catch (final InterruptedException ignored)
        {
        }

        if (clientsInServer.contains (client)) return;

        clientConnector.disconnect (client);

        log.info ("Client connection timed out [{}].", client.getAddress ());
      }
    }
  }
}
