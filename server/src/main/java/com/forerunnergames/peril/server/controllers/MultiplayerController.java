package com.forerunnergames.peril.server.controllers;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.serverAddressFrom;
import static com.forerunnergames.tools.net.events.EventFluency.clientFrom;
import static com.forerunnergames.tools.net.events.EventFluency.messageFrom;

import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.shared.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.events.client.request.CreateGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.CreateGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerLeaveGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerLeaveGameDeniedEvent.Reason;
import com.forerunnergames.peril.core.shared.net.events.server.success.CreateGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerLeaveGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.ClientConfiguration;
import com.forerunnergames.tools.net.ClientConnector;
import com.forerunnergames.tools.net.DefaultClientConfiguration;
import com.forerunnergames.tools.net.DefaultServerConfiguration;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.events.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.ClientDisconnectionEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* TODO Refactor:
 *
 * - This class should not be directly modifying game state.
 *
 * - It's only purpose is to facilitate communication between multiple network clients and the game state machine.
 *
 * - This effectively hides the existence of a remote network from the game state machine, so that the state machine
 *   doesn't know or care if its players are local or remote, or some combination of both.
 *
 * - This class should be refactored until it accurately reflects that purpose.
 *
 * - The main way to accomplish this refactoring is by focusing on communicating with the state machine via the
 *   event bus only.
 *
 * - This class receives questions from clients in the form of *RequestEvent's, which should be forwarded to the
 *   state machine via the event bus, NOT handled directly in this class, and NOT directly delegated to the PlayerModel,
 *   which is what currently happens.
 *
 * - Then this class should subscribe to (listen for) *AnswerEvent's on the event bus, which should be forwarded back
 *   to the clients.
 *
 * - If you want to see a better example of how this class should look, see the client version of MultiplayerController
 *   in the client module at peril/client/src/com/forerunnergames/peril/client/controllers/MultiplayerController.java.
 *   It facilitates communication between the server and the client's UI logic via the event bus by subscribing to
 *   *RequestEvent's from the client UI logic, passing them on to the server, and then listening for *AnswerEvent's
 *   from the server, and finally passing them on to the client UI logic, so that the UI can update it's state to
 *   accurately reflect the current state of the server.
 */
public final class MultiplayerController extends ControllerAdapter
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private final Set <Remote> clientsInServer;
  private final BiMap <Remote, PlayerPacket> clientsToPlayers;
  private final ClientConnector clientConnector;
  private final PlayerCommunicator playerCommunicator;
  private final MBassador <Event> eventBus;
  private final GameConfiguration gameConfig;
  private final boolean shouldShutDown = false;
  private final Map <String, Remote> playerJoinGameRequestCache;
  private final String gameServerName;
  private final GameServerType gameServerType;
  private final int serverTcpPort;
  @Nullable
  private Remote host = null;

  public MultiplayerController (final String gameServerName,
                                final GameServerType gameServerType,
                                final int serverTcpPort,
                                final GameConfiguration gameConfig,
                                final ClientConnector clientConnector,
                                final PlayerCommunicator playerCommunicator,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (gameServerName, "gameServerName");
    Arguments.checkIsNotNull (gameServerType, "gameServerType");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");
    Arguments.checkUpperInclusiveBound (serverTcpPort, NetworkSettings.MAX_PORT_VALUE, "serverTcpPort");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (clientConnector, "clientConnector");
    Arguments.checkIsNotNull (playerCommunicator, "playerCommunicator");
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
    clientsToPlayers = Maps.synchronizedBiMap (HashBiMap.<Remote, PlayerPacket> create (gameConfig.getPlayerLimit ()));
    playerJoinGameRequestCache = Collections.synchronizedMap (new HashMap <String, Remote> ());
  }

  @Override
  public void initialize ()
  {
    eventBus.subscribe (this);
  }

  @Override
  public boolean shouldShutDown ()
  {
    return shouldShutDown;
  }

  boolean isPlayerInGame (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return clientsToPlayers.containsValue (player);
  }

  GameConfiguration getGameConfiguration ()
  {
    return gameConfig;
  }

  boolean isClientInServer (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    return clientsInServer.contains (client);
  }

  // <<<<< remote inbound/outbound event communication >>>>> //

  @Handler
  public void onClientConnectionEvent (final ClientConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.info ("Client [{}] connected.", clientFrom (event));

    final ClientConnectorDaemon connector = new ClientConnectorDaemon ();
    connector.onConnect (clientFrom (event));
  }

  @Handler
  public void onClientDisconnectionEvent (final ClientDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Remote client = clientFrom (event);

    log.info ("Client [{}] disconnected.", client);

    if (!clientsToPlayers.containsKey (client))
    {
      // TODO: PlayerLeaveGameDeniedEvent is pointless and should be changed or removed.
      eventBus.publish (new PlayerLeaveGameDeniedEvent (Reason.PLAYER_DOES_NOT_EXIST));
      return;
    }

    final PlayerPacket disconnectedPlayer = clientsToPlayers.get (client);
    final Event leaveGameEvent = new PlayerLeaveGameSuccessEvent (disconnectedPlayer.getName ());
    eventBus.publish (leaveGameEvent);
    sendToAllPlayersExcept (disconnectedPlayer, leaveGameEvent);
    remove (client);
  }

  @Handler
  public void onClientCommunicationEvent (final ClientCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    if (messageFrom (event) instanceof CreateGameServerRequestEvent)
    {
      onEvent ((CreateGameServerRequestEvent) messageFrom (event), clientFrom (event));
    }
    else if (messageFrom (event) instanceof JoinGameServerRequestEvent)
    {
      onEvent ((JoinGameServerRequestEvent) messageFrom (event), clientFrom (event));
    }
    else if (messageFrom (event) instanceof PlayerJoinGameRequestEvent)
    {
      onEvent ((PlayerJoinGameRequestEvent) messageFrom (event), clientFrom (event));
    }
    else
    {
      log.warn ("Received unrecognized message [{}] from client [{}].", messageFrom (event), clientFrom (event));
    }
  }

  @Handler
  public void onPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final String playerName = playerNameFrom (event);

    // if no client mapping is available, silently ignore success event
    // this is to prevent failure under cases such as client disconnecting while join request is being processed
    if (!playerJoinGameRequestCache.containsKey (playerName)) return;

    final Remote client = playerJoinGameRequestCache.get (playerName);

    final PlayerPacket newPlayer = playerFrom (event);
    final PlayerPacket oldPlayer = clientsToPlayers.forcePut (client, newPlayer);
    if (oldPlayer != null)
    {
      // this generally shouldn't happen... but if it does, log a warning message
      log.warn ("Overwrote previous player mapping for client [{}] | old player: [{}] | new player: [{}]", client,
                oldPlayer, newPlayer);
    }

    sendToAllPlayers (event);

    playerJoinGameRequestCache.remove (playerName);
  }

  @Handler
  public void onPlayerJoinGameDeniedEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final String playerName = playerNameFrom (event);

    // if no client mapping is available, silently ignore denied event
    // this is to prevent failure under cases such as client disconnecting while join request is being processed
    if (!playerJoinGameRequestCache.containsKey (playerName)) return;

    playerCommunicator.sendTo (playerJoinGameRequestCache.get (playerName), event);

    playerJoinGameRequestCache.remove (playerName);
  }

  // <<<<< event handlers for inbound core events >>>>> //

  // This event is for joining host-and-play servers only as the host (the host created and connected to a local server).
  private void onEvent (final CreateGameServerRequestEvent event, final Remote client)
  {
    log.info ("Received create game server request from {}", client);

    // check if client is already in server
    if (clientsInServer.contains (client))
    {
      sendCreateGameServerDenied (client, event, "You have already joined this game server.");
      return;
    }

    // if someone other than the local host is attempting to join via CreateGameServerRequestEvent, we have a problem
    if (!isLocalHost (client))
    {
      sendCreateGameServerDenied (client, event, "Only the host can create a host-and-play gmae server.");
      return;
    }

    // CreateGameServerRequestEvent implies host-and-play, so if this is a dedicated server, we have a problem
    if (isDedicated ())
    {
      sendCreateGameServerDenied (client, event, "Cannot create a host-and-play game server. This is a dedicated game server.");
      return;
    }

    // if someone else already joined the game server, we have a problem
    if (!clientsInServer.isEmpty ())
    {
      sendCreateGameServerDenied (client, event, "Cannot create a host-and-play game server after others have already joined it.");
      return;
    }

    sendCreateGameServerSuccess (client, serverAddressFrom (event));
  }

  // This event is for joining dedicated servers only as a non-host.
  private void onEvent (final JoinGameServerRequestEvent event, final Remote client)
  {
    log.info ("Received join game server request from {}", client);

    // check if client is already in server
    if (clientsInServer.contains (client))
    {
      sendJoinGameServerDenied (client, event, "You have already joined this game server.");
      return;
    }

    // local host can only join via CreateGameServerRequestEvent
    if (isLocalHost (client))
    {
      sendJoinGameServerDenied (client, event, "You cannot join this server as the host.");
      return;
    }

    // local host must join first via CreateGameServerRequestEvent in a host-and-play server
    if (isHostAndPlay () && !isHostConnected () && !isLocalHost (client))
    {
      sendJoinGameServerDenied (client, event, "Waiting for the host to connect...");
      return;
    }

    if (isHostAndPlay () && isLocalHost (client)) host = client;

    sendJoinGameServerSuccess (client, serverAddressFrom (event), ImmutableSet.copyOf (clientsToPlayers.values ()));
  }

  private void onEvent (final PlayerJoinGameRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    // client is connected but not in game server; just ignore request event
    if (!clientsInServer.contains (client))
    {
      log.warn ("Ignoring join game request from player [{}] | REASON: unrecognized client [{}].",
                playerNameFrom (event), client);
      return;
    }
    // spam guard: if client request is already being processed, ignore new request event
    if (playerJoinGameRequestCache.containsKey (playerNameFrom (event))) return;

    log.info ("Received request to join game from player {}", playerNameFrom (event));

    playerJoinGameRequestCache.put (playerNameFrom (event), client);

    eventBus.publish (event);
  }

  // <<<<< internal utility methods and types >>>>>> //

  private void sendCreateGameServerSuccess (final Remote client, final String serverAddress)

  {
    final Event successEvent = new CreateGameServerSuccessEvent (createGameServerConfig (serverAddress),
            createClientConfig (client.getAddress (), client.getPort ()));
    playerCommunicator.sendTo (client, successEvent);
    clientsInServer.add (client);
    log.info ("Client [{}] successfully created & joined game server.", client);
  }

  private void sendCreateGameServerDenied (final Remote client,
                                           final CreateGameServerRequestEvent event,
                                           final String reason)
  {
    playerCommunicator.sendTo (client,
                               new CreateGameServerDeniedEvent (event, new DefaultClientConfiguration (client
                                       .getAddress (), client.getPort ()), reason));
    clientConnector.disconnect (client);
    log.warn ("Denied create game server request from [{}]; REASON: {}", client, reason);
  }

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
    playerCommunicator.sendTo (client,
                               new JoinGameServerDeniedEvent (event, new DefaultClientConfiguration (client
                                       .getAddress (), client.getPort ()), reason));
    clientConnector.disconnect (client);
    log.warn ("Denied join game server request from [{}]; REASON: {}", client, reason);
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

  private void sendToPlayer (final PlayerPacket player, final Object object)
  {
    playerCommunicator.sendToPlayer (ImmutableBiMap.copyOf (clientsToPlayers.inverse ()), player, object);
  }

  private void sendToAllPlayers (final Object object)
  {
    playerCommunicator.sendToAllPlayers (ImmutableBiMap.copyOf (clientsToPlayers.inverse ()), object);
  }

  private void sendToAllPlayersExcept (final PlayerPacket player, final Object object)
  {
    playerCommunicator.sendToAllPlayersExcept (ImmutableBiMap.copyOf (clientsToPlayers.inverse ()), player, object);
  }

  private void remove (final Remote client)
  {
    clientsInServer.remove (client);
    clientsToPlayers.remove (client);
  }

  private final class ClientConnectorDaemon implements Runnable
  {
    private Remote client;

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

    public void onConnect (final Remote client)
    {
      Arguments.checkIsNotNull (client, "client");

      this.client = client;

      final Thread thread = new Thread (this);
      thread.setDaemon (true);
      thread.start ();
    }
  }

  /*
  @EventSubscriber (eventClass = ClientConnectionEvent.class)
  public void onClientConnectionEvent (ClientConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.info ("Client [{}] connected.", clientFrom (event));
  }

  @EventSubscriber (eventClass = ClientDisconnectionEvent.class)
  public void onClientDisconnectionEvent (ClientDisconnectionEvent disconnectionEvent)
  {
    Arguments.checkIsNotNull (disconnectionEvent, "disconnectionEvent");

    if (! existsPlayerWith (clientFrom (disconnectionEvent)))
    {
      log.warn ("Client [{}] disconnected, but did not exist as a player.", clientFrom (disconnectionEvent));
      return;
    }

    log.info ("Player [{}] disconnected.", playerWith (clientFrom (disconnectionEvent)));

    final PlayerLeaveGameSuccessEvent playerLeaveGameSuccessEvent =
            new PlayerLeaveGameSuccessEvent (playerWith (clientFrom (disconnectionEvent)));

    removePlayerWith (clientFrom (disconnectionEvent));
    sendToAll (playerLeaveGameSuccessEvent);
    sendToAll (statusMessage (playerNameFrom (playerLeaveGameSuccessEvent) + " left the game."));
  }

  private boolean existsPlayerWith (final Remote client)
  {
    return clientsToPlayerIds.containsKey (client) && playerIdsToClients.containsValue (client);
  }

  private void removePlayerWith (final Remote client)
  {
    remove (playerWith (client));
  }

  private Player playerWith (final Remote client)
  {
    assert clientsToPlayerIds.containsKey (client);
    assert playerIdsToClients.containsValue (client);

    return playerModel.playerWith (clientsToPlayerIds.get (client));
  }

  private void remove (final Player player)
  {
    playerModel.remove (player);
    clientsToPlayerIds.remove (playerIdsToClients.remove (idOf (player)));
  }

  private Remote clientOf (final Player player)
  {
    assert playerIdsToClients.containsKey (idOf (player));
    assert clientsToPlayerIds.containsValue (idOf (player));

    return playerIdsToClients.get (idOf (player));
  }

  private void sendToAll (final Object object)
  {
    clientCommunicator.sendToAll (object);
  }

  @EventSubscriber (eventClass = ClientCommunicationEvent.class)
  public void onClientCommunicationEvent (final ClientCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event [{}] received.", event);

    if (questionFrom (event) instanceof ChatMessageRequestEvent)
    {
      onEvent ((ChatMessageRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof PlayerJoinGameRequestEvent)
    {
      onEvent ((PlayerJoinGameRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof ChangePlayerColorRequestEvent)
    {
      onEvent ((ChangePlayerColorRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof ChangePlayerLimitRequestEvent)
    {
      onEvent ((ChangePlayerLimitRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof ChangePlayerTurnOrderRequestEvent)
    {
      onEvent ((ChangePlayerTurnOrderRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof JoinMultiplayerServerRequestEvent)
    {
      onEvent ((JoinMultiplayerServerRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof OpenMultiplayerServerRequestEvent)
    {
      onEvent ((OpenMultiplayerServerRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof QuitMultiplayerServerRequestEvent)
    {
      onEvent ((QuitMultiplayerServerRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof KickPlayerFromGameRequestEvent)
    {
      onEvent ((KickPlayerFromGameRequestEvent) questionFrom (event), clientFrom (event));
    }
    else
    {
      log.warn ("Received unrecognized message [{}] from client [{}].", questionFrom (event), clientFrom (event));
    }
  }

  private void onEvent (final PlayerJoinGameRequestEvent requestEvent, final Remote sender)
  {
    Result result = addClientToGame (sender);

    if (result.isFailure())
    {
      playerJoinGameDenied (playerNameFrom (requestEvent), result.getMessage(), sender);
      return;
    }

    Player player = createPlayer (withPlayerNameFrom (requestEvent));
    result = addPlayerToGame (player);

    if (result.isFailure())
    {
      playerJoinGameDenied (playerNameFrom (requestEvent), result.getMessage(), sender);
      return;
    }

    playerJoinGameSuccess (player, sender);
  }

  private Result addClientToGame (final Remote requestingClient)
  {
    if (shouldWaitForHostToJoinFirst (requestingClient))
    {
      return Result.failure ("Please wait for the host to join the game first!");
    }

    return Result.success();
  }

  private boolean shouldWaitForHostToJoinFirst (final Remote joiningClient)
  {
    return host != null && ! existsPlayerWith (host) && joiningClient.isNot (host);
  }

  private void playerJoinGameDenied (final String playerName,
                                     final PlayerJoinGameDeniedEvent.REASON reason,
                                     final Remote sender)
  {
    PlayerJoinGameDeniedEvent deniedEvent = new PlayerJoinGameDeniedEvent (playerName, reason);
    sendTo (sender, deniedEvent);
    if (isHost (sender)) sendTo (sender, new CloseMultiplayerServerSuccessEvent());
    disconnect (sender);
  }

  private Player createPlayer (final String name)
  {
    return PlayerFactory.create (name, playerModel.nextAvailableId());
  }

  private Result addPlayerToGame (final Player player)
  {
    return playerModel.add (player);
  }

  private void playerJoinGameSuccess (final Player player, final Remote client)
  {
    register (idOf (player), client);
    PlayerJoinGameSuccessEvent successEvent = new PlayerJoinGameSuccessEvent (player);
    playerFrom(successEvent).setIdentity (PersonIdentity.NON_SELF);
    sendToAllExcept (client, successEvent);
    sendToAllExcept (client, statusMessage (player.getName() + " joined the game as a player."));
    playerFrom(successEvent).setIdentity (PersonIdentity.SELF);
    sendTo (client, successEvent);
    sendTo (client, statusMessage ("You successfully joined \"" + gameServerName + "\" multiplayer game."));
  }

  private void sendTo (final Remote client, final Object object)
  {
    clientCommunicator.sendTo (client, object);
  }

  private void sendToAllExcept (final Remote client, final Object object)
  {
    clientCommunicator.sendToAllExcept (client, object);
  }

  private void register (final Id playerId, final Remote playerClient)
  {
    playerIdsToClients.put (playerId, playerClient);
    clientsToPlayerIds.put (playerClient, playerId);
  }

  private void onEvent (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    if (! isHost (sender))
    {
      kickPlayerNonSelfFromGameDenied (requestEvent, sender);
    }
    else if (! isHost (clientOf (playerFrom (requestEvent))))
    {
      kickPlayerNonSelfFromGameSuccess (requestEvent, sender);
    }
    else
    {
      kickPlayerSelfFromGameDenied (requestEvent, sender);
    }
  }

  private boolean isHost (final Remote remote)
  {
    return host != null && remote.equals (host);
  }

  private void kickPlayerNonSelfFromGameSuccess (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    disconnect (clientOf (playerFrom (requestEvent)));
    KickPlayerFromGameSuccessEvent successEvent =
            new KickPlayerFromGameSuccessEvent (playerFrom (requestEvent), reasonForKickFrom (requestEvent));
    playerFrom(requestEvent).setIdentity (PersonIdentity.NON_SELF);
    sendToAll (successEvent);
    sendTo (sender, statusMessage ("You kicked " + playerNameFrom (requestEvent) + " from the game. Reason: " +
            reasonForKickFrom (requestEvent)));
    sendToAllExcept (sender, statusMessage (playerNameFrom (requestEvent) + " was kicked from the game. Reason: " +
            reasonForKickFrom (requestEvent)));
  }

  private void kickPlayerSelfFromGameDenied (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    KickPlayerFromGameDeniedEvent deniedEvent =
            new KickPlayerFromGameDeniedEvent (
                    playerFrom (requestEvent), reasonForKickFrom (requestEvent), "You can't kick yourself!");
    playerFrom(deniedEvent).setIdentity (PersonIdentity.SELF);
    sendTo (sender, deniedEvent);
  }

  private void kickPlayerNonSelfFromGameDenied (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    KickPlayerFromGameDeniedEvent deniedEvent =
            new KickPlayerFromGameDeniedEvent (
                    playerFrom (requestEvent), reasonForKickFrom (requestEvent), "Only the host can kick players!");
    playerFrom(deniedEvent).setIdentity (PersonIdentity.NON_SELF);
    sendTo (sender, deniedEvent);
  }

  private void disconnect (final Remote client)
  {
    clientConnector.disconnect (client);
  }

  private void onEvent (final ChatMessageRequestEvent requestEvent, final Remote sender)
  {
    sendToAll (new ChatMessageSuccessEvent (withMessageFrom (requestEvent)));
  }

  private Author withAuthorOf (final Remote sender)
  {
    return existsPlayerWith (sender) ? playerWith (sender) : null;
  }

  private void onEvent (final ChangePlayerColorRequestEvent requestEvent, final Remote sender)
  {
    if (! clientOf (playerFrom (requestEvent)).equals (sender))
    {
      changePlayerColorDenied (requestEvent, sender, "You can only change your own color!");
      return;
    }

    Result result =
            playerModel.changeColorOf (
                    idOf (playerFrom (requestEvent)),
                    previousColorFrom (requestEvent),
                    currentColorFrom (requestEvent));

    if (result.isFailure())
    {
      changePlayerColorDenied (requestEvent, sender, result.getMessage());
      return;
    }

    changePlayerColorSuccess (currentColorFrom (requestEvent), previousColorFrom (requestEvent), sender);
  }

  private void changePlayerColorDenied (final ChangePlayerColorRequestEvent requestEvent,
                                        final Remote sender,
                                        final String reason)
  {
    ChangePlayerColorDeniedEvent deniedEvent = new ChangePlayerColorDeniedEvent (requestEvent, reason);
    sendTo (sender, deniedEvent);
    sendTo (sender, statusMessage (reason));
  }

  private void changePlayerColorSuccess (final PlayerColor currentColor,
                                         final PlayerColor previousColor,
                                         final Remote sender)
  {
    final ChangePlayerColorSuccessEvent successEvent =
            new ChangePlayerColorSuccessEvent (playerWith (sender), currentColor, previousColor);

    sendToAllExcept (sender, successEvent);
    sendToAllExcept (sender, statusMessage (playerNameFrom (successEvent) + " changed colors from " +
            previousColorFrom(successEvent).toLowerCase() + " to " + currentColorFrom(successEvent).toLowerCase() + "."));
  }

  private StatusMessageEvent statusMessage (final String text)
  {
    return new DefaultStatusMessage (text);
  }

  private void onEvent (final ChangePlayerLimitRequestEvent requestEvent, final Remote sender)
  {
    if (! isHost (sender))
    {
      changePlayerLimitDenied (deltaFrom (requestEvent), sender, "Only the host can change the player limit.");
      return;
    }

    playerModel.changePlayerLimitBy (deltaFrom (requestEvent));

    changePlayerLimitSuccess (deltaFrom (requestEvent), sender);
  }

  private void changePlayerLimitDenied (final int playerLimitDelta, final Remote sender, final String reason)
  {
    ChangePlayerLimitDeniedEvent deniedEvent = new ChangePlayerLimitDeniedEvent (playerLimitDelta, reason);
    sendTo (sender, deniedEvent);
  }

  private void changePlayerLimitSuccess (final int playerLimitDelta, final Remote sender)
  {
    ChangePlayerLimitSuccessEvent successEvent = new ChangePlayerLimitSuccessEvent (playerLimitDelta);
    sendToAllExcept (sender, statusMessage ("The maximum number of players was changed from " +
            (playerModel.getPlayerLimit() - successEvent.getPlayerLimitDelta()) + " to " +
            playerModel.getPlayerLimit() + "."));
    sendToAll (successEvent);
  }

  private void onEvent (final ChangePlayerTurnOrderRequestEvent requestEvent, final Remote sender)
  {
    // TODO The Model should check if the turn order change is valid.
    // TODO If not, then send PlayerTurnOrderChangeDeniedEvent to the sender only.

    changeTurnOrderOf (playerFrom (requestEvent), currentTurnOrderFrom (requestEvent));
    ChangePlayerTurnOrderSuccessEvent successEvent = new ChangePlayerTurnOrderSuccessEvent (requestEvent);
    sendTo (sender, statusMessage ("You are now the " + currentTurnOrderFrom(successEvent).toMixedOrdinal() +
            " player."));
    sendToAllExcept (sender, statusMessage (playerNameFrom (successEvent) + " is now the " +
            currentTurnOrderFrom(successEvent).toMixedOrdinal() + " player."));
    sendToAll (successEvent);
  }

  private void changeTurnOrderOf (final Player player, final PlayerTurnOrder turnOrder)
  {
    player.setTurnOrder (turnOrder);
  }

  private void onEvent (final OpenMultiplayerServerRequestEvent requestEvent, Remote sender)
  {
    Result result = checkServerIsEmpty();

    if (result.isFailure())
    {
      openMultiplayerServerDenied (requestEvent, sender, result.getMessage());
      return;
    }

    result = checkIsValid (serverNameFrom (requestEvent), serverTcpPortFrom (requestEvent), addressOf (sender));

    if (result.isFailure())
    {
      openMultiplayerServerDenied (requestEvent, sender, result.getMessage());
      return;
    }

    openMultiplayerServerSuccess (sender);
  }

  private Result checkServerIsEmpty()
  {
    return playerModel.isEmpty() ? Result.success() : Result.failure ("There are already players in the server.");
  }

  private Result checkIsValid (final String name, final int tcpPort, final String address)
  {
    if (! serverNameMatches (name))
    {
      return Result.failure ("Requested server name doesn't match the actual server name.");
    }
    else if (! serverTcpPortMatches (tcpPort))
    {
      return Result.failure ("Requested server port doesn't match the actual server port.");
    }
    else if (! isLocal (address))
    {
      return Result.failure ("You can only host & play on the same machine as the server!");
    }

    return Result.success();
  }

  private boolean serverNameMatches (final String gameServerName)
  {
    return this.gameServerName.equals (gameServerName);
  }

  private boolean serverTcpPortMatches (final int tcpPort)
  {
    return serverTcpPort == tcpPort;
  }

  private boolean isLocal (final String address)
  {
    return address.equals (NetworkSettings.LOCALHOST_ADDRESS);
  }

  private void openMultiplayerServerDenied (final OpenMultiplayerServerRequestEvent event,
                                            final Remote sender,
                                            final String reason)
  {
    sendTo (sender, new OpenMultiplayerServerDeniedEvent (event, reason));
  }

  private void openMultiplayerServerSuccess (final Remote sender)
  {
    host = sender;
    sendTo (sender, new OpenMultiplayerServerSuccessEvent (gameServerName, serverTcpPort));
  }

  private void onEvent (final JoinMultiplayerServerRequestEvent requestEvent, Remote sender)
  {
    joinMultiplayerServerSuccess (serverAddressFrom (requestEvent), sender);
  }

  private void joinMultiplayerServerSuccess (final String serverAddress, final Remote joiningClient)
  {
    sendTo (joiningClient,
            new JoinMultiplayerServerSuccessEvent (
                    gameServerName, serverAddress, serverTcpPort, playerModel.getPlayers(), playerModel.getPlayerLimit()));
  }

  private void onEvent (final QuitMultiplayerServerRequestEvent event, final Remote sender)
  {
    if (isHost (sender))
    {
      closeServer();
    }
    else if (existsPlayerWith (sender))
    {
      disconnect (sender);
    }
    else
    {
      log.warn ("Client [{}] sent event [{}], but does not exist as a player.", sender, event);
    }
  }

  private void closeServer()
  {
    sendTo (host, new CloseMultiplayerServerSuccessEvent());
    sendToAllExcept (host, statusMessage ("The host is shutting down the server."));
    clientConnector.disconnectAll();
    shouldShutDown = true;
  }
  */
}
