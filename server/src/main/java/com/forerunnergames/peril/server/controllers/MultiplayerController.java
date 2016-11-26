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

import static com.forerunnergames.tools.net.events.EventFluency.clientFrom;

import com.forerunnergames.peril.ai.events.AiDisconnectionEvent;
import com.forerunnergames.peril.ai.net.AiClient;
import com.forerunnergames.peril.ai.net.AiClientConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.net.NetworkEventHandler;
import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerRejoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerRejoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.DirectPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerNotifyJoinGameEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.SpectatorJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultSpectatorPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.state.events.DestroyGameEvent;
import com.forerunnergames.peril.server.communicators.CoreCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.communicators.SpectatorCommunicator;
import com.forerunnergames.peril.server.controllers.ClientSpectatorMapping.RegisteredClientSpectatorNotFoundException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.NetworkConstants;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.ClientConnector;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.client.configuration.DefaultClientConfiguration;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiplayerController extends ControllerAdapter
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private final Multimap <PlayerPacket, PlayerInputRequestEvent> playerInputRequestEventCache = HashMultimap.create ();
  private final Multimap <PlayerPacket, PlayerInformEvent> playerInformEventCache = HashMultimap.create ();
  private final Map <String, Remote> playerJoinGameRequestCache = Collections.synchronizedMap (new HashMap <String, Remote> ());
  private final Set <Remote> clientsInServer = Collections.synchronizedSet (new HashSet <Remote> ());
  private final ClientPlayerMapping clientsToPlayers;
  private final ClientSpectatorMapping clientsToSpectators;
  private final ClientConnectorDaemon connectorDaemon = new ClientConnectorDaemon ();
  private final GameServerConfiguration gameServerConfig;
  private final ClientConnector clientConnector;
  private final PlayerCommunicator humanPlayerCommunicator;
  private final PlayerCommunicator aiPlayerCommunicator;
  private final SpectatorCommunicator spectatorCommunicator;
  private final CoreCommunicator coreCommunicator;
  private final MBassador <Event> eventBus;
  private NetworkEventHandler networkEventHandler = null;
  private boolean shouldShutDown = false;
  private int connectionTimeoutMillis = NetworkSettings.CLIENT_CONNECTION_TIMEOUT_MS;
  @Nullable
  private Remote host = null;
  // @formatter:on

  public MultiplayerController (final GameServerConfiguration gameServerConfig,
                                final ClientConnector clientConnector,
                                final PlayerCommunicator humanPlayerCommunicator,
                                final PlayerCommunicator aiPlayerCommunicator,
                                final SpectatorCommunicator spectatorCommunicator,
                                final CoreCommunicator coreCommunicator,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConnector, "clientConnector");
    Arguments.checkIsNotNull (humanPlayerCommunicator, "humanPlayerCommunicator");
    Arguments.checkIsNotNull (aiPlayerCommunicator, "aiPlayerCommunicator");
    Arguments.checkIsNotNull (spectatorCommunicator, "spectatorCommunicator");
    Arguments.checkIsNotNull (coreCommunicator, "coreCommunicator");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.gameServerConfig = gameServerConfig;
    this.clientConnector = clientConnector;
    this.humanPlayerCommunicator = humanPlayerCommunicator;
    this.aiPlayerCommunicator = aiPlayerCommunicator;
    this.spectatorCommunicator = spectatorCommunicator;
    this.coreCommunicator = coreCommunicator;
    this.eventBus = eventBus;

    clientsToPlayers = new ClientPlayerMapping (coreCommunicator, gameServerConfig.getTotalPlayerLimit ());
    clientsToSpectators = new ClientSpectatorMapping (gameServerConfig.getSpectatorLimit ());
  }

  @Override
  public void initialize ()
  {
    log.trace ("Initializing {} for game server '{}'", getClass ().getSimpleName (),
               gameServerConfig.getGameServerName ());

    eventBus.subscribe (this);
    eventBus.publish (new CreateGameEvent ());
    networkEventHandler = new ServerNetworkEventHandler (this, eventBus.getRegisteredErrorHandlers ());
  }

  @Override
  public boolean shouldShutDown ()
  {
    return shouldShutDown;
  }

  @Override
  public void shutDown ()
  {
    log.debug ("Shutting down...");

    eventBus.publish (new DestroyGameEvent ());
    eventBus.unsubscribe (this);
    connectorDaemon.threadPool.shutdown ();
    shouldShutDown = true;
  }

  public void setClientConnectTimeout (final int connectionTimeoutMillis)
  {
    Arguments.checkIsNotNegative (connectionTimeoutMillis, "connectionTimeoutMillis");

    this.connectionTimeoutMillis = connectionTimeoutMillis;
  }

  public GameConfiguration getGameConfiguration ()
  {
    return gameServerConfig;
  }

  public PersonLimits getPersonLimits ()
  {
    return gameServerConfig.getPersonLimits ();
  }

  public int getTotalPlayerLimit ()
  {
    return gameServerConfig.getTotalPlayerLimit ();
  }

  public int getPlayerLimitFor (final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (sentience, "sentience");

    return gameServerConfig.getPlayerLimitFor (sentience);
  }

  public int getSpectatorLimit ()
  {
    return gameServerConfig.getSpectatorLimit ();
  }

  public boolean isPlayerInGame (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return clientsToPlayers.clientFor (player).isPresent ();
  }

  public boolean isClientInServer (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    return clientsInServer.contains (client);
  }

  // ---------- inbound events from core module ---------- //

  @Handler (priority = Integer.MIN_VALUE)
  public void onEvent (final BroadcastEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    // PlayerJoinGameSuccessEvent requires special processing.
    if (event instanceof PlayerJoinGameSuccessEvent) return;

    log.trace ("Event received [{}]", event);

    sendToAllPlayersAndSpectators (event);
  }

  @Handler (priority = Integer.MIN_VALUE)
  public void onEvent (final DirectPlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    sendToPlayer (event.getPerson (), event);
  }

  @Handler
  public void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final String playerName = event.getPersonName ();

    // if no client mapping is available, silently ignore success event and tell core to remove this player
    // this shouldn't happen, since this would imply that core processed a player join game request without
    // server receiving one... so basically the result of a bug or a hack.
    if (!playerJoinGameRequestCache.containsKey (playerName))
    {
      log.warn ("No client join game request in cache for player: {}. Player will be removed.", playerName);
      coreCommunicator.notifyRemovePlayerFromGame (event.getPerson ());
      return;
    }

    // fetch and remove player name from request cache
    final Remote client = playerJoinGameRequestCache.remove (playerName);

    final PlayerPacket newPlayer = event.getPerson ();

    // only add a player/client mapping if the client still exists in the game server
    if (clientsInServer.contains (client))
    {
      final Optional <PlayerPacket> oldPlayer = clientsToPlayers.put (client, newPlayer);
      if (oldPlayer.isPresent ())
      {
        // this generally shouldn't happen... but if it does, log a warning message
        log.warn ("Overwrote previous player mapping for client [{}] | old player: [{}] | new player: [{}]", client,
                  oldPlayer.get (), newPlayer);
      }
    }
    else
    {
      // this should cover the case where the client disconnected before the successful join game request was processed.
      // core will be notified that the player has left. The subsequent PlayerLeaveGameEvent will be ignored since there
      // will be no client mapping for the disconnected player.
      log.warn ("Client [{}] for player [{}] is no longer connected to the server. Player will be removed.", client,
                event.getPersonName ());
      coreCommunicator.notifyRemovePlayerFromGame (event.getPerson ());
      return;
    }

    final Optional <UUID> playerServerId = clientsToPlayers.serverIdFor (newPlayer);
    if (!playerServerId.isPresent ())
    {
      Exceptions.throwIllegalState ("No server uuid found for player [{}].", newPlayer);
    }

    sendPlayerJoinGameSuccessEvent (event, new PlayerNotifyJoinGameEvent (newPlayer, playerServerId.get ()));
  }

  @Handler
  public void onEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final String playerName = event.getPlayerName ();

    // if no client mapping is available, silently ignore denied event
    // this is to prevent failure under cases such as client disconnecting while join request is being processed
    if (!playerJoinGameRequestCache.containsKey (playerName)) return;

    final Remote client = playerJoinGameRequestCache.get (playerName);

    if (client instanceof AiClient)
    {
      sendToAiClient (client, event);
      disconnectAi ((AiClient) client);
    }
    else
    {
      sendToHumanClient (client, event);
      disconnectHuman (client);
    }

    playerJoinGameRequestCache.remove (playerName);
  }

  @Handler
  public void onEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Optional <Remote> client = clientsToPlayers.clientFor (event.getPerson ());
    if (!client.isPresent ())
    {
      log.warn ("No client mapping for player in received event [{}].", event);
      return;
    }
    // remove client mapping
    remove (client.get ());

    playerInputRequestEventCache.removeAll (event.getPerson ());
    playerInformEventCache.removeAll (event.getPerson ());

    // let handler for broadcast events handle forwarding the event
  }

  @Handler
  public void onEvent (final PlayerLoseGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Optional <Remote> optionalClient = clientsToPlayers.clientFor (event.getPerson ());
    if (!optionalClient.isPresent ())
    {
      log.warn ("No client mapping for player in received event [{}].", event);
      return;
    }

    final Remote client = optionalClient.get ();

    // remove client/player mapping; keep client in server
    clientsToPlayers.remove (client);

    // Don't make AI spectators, just remove them completely.
    if (event.getPersonSentience () == PersonSentience.AI)
    {
      // This will eventually trigger a ClientDisconnectionEvent, as well as give AiApplication a chance to remove the
      // AiController instance. The ClientDisconnectionEvent handler in MultiplayerController will handle the details
      // of removing the AI client from the server.
      eventBus.publish (new AiDisconnectionEvent (event.getPersonName ()));
      return;
    }

    // Add client as a spectator, simulating the request coming from the client since we want to automate it
    // server-side. This will allow the networking system to correctly process the request to become a spectator,
    // instead of directly adding them, which would duplicate code and be extremely bug-prone.
    //
    // Also bypasses stage 1 of authenticating as a client via JoinGameServerRequestEvent because
    // the client already previously authenticated as a player, and is still connected to the server.
    eventBus.publish (new ClientCommunicationEvent (new SpectatorJoinGameRequestEvent (event.getPersonName ()),
            client));

    // let handler for broadcast events handle forwarding the event
  }

  @Handler
  public void onEvent (final PlayerInputRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final boolean wasAdded = playerInputRequestEventCache.put (event.getPerson (), event);
    assert wasAdded;

    // let handler for direct player event handle forwarding the event
  }

  @Handler
  public void onEvent (final PlayerInformEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final boolean wasAdded = playerInformEventCache.put (event.getPerson (), event);
    assert wasAdded;

    // let handler for direct player event handle forwarding the event
  }

  // ---------- remote inbound/outbound event communication ---------- //

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

    Optional <PlayerPacket> playerQuery = Optional.absent ();
    Optional <SpectatorPacket> spectatorQuery = Optional.absent ();
    try
    {
      playerQuery = clientsToPlayers.playerFor (client);
      spectatorQuery = clientsToSpectators.spectatorFor (client);
    }
    catch (final ClientPlayerMapping.RegisteredClientPlayerNotFoundException e)
    {
      log.error ("Error resolving client to player.", e);
    }
    catch (final RegisteredClientSpectatorNotFoundException e)
    {
      log.error ("Error resolving client to spectator.", e);
    }

    // client is a player
    if (playerQuery.isPresent ())
    {
      final PlayerPacket disconnectedPlayer = playerQuery.get ();
      coreCommunicator.notifyRemovePlayerFromGame (disconnectedPlayer);
      // let the leave game event handle removing the player
      return;
    }

    // if client is neither a player nor a spectator, log a warning
    if (!playerQuery.isPresent () && !spectatorQuery.isPresent ())
    {
      log.warn ("Client [{}] disconnected but did not exist as a player or spectator.", client);
    }

    remove (client);
  }

  @Handler
  void onEvent (final ClientCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    networkEventHandler.handle (event.getMessage (), event.getClient ());
  }

  // ---------- inbound client event callbacks from NetworkEventHandler ---------- //

  void handleEvent (final HumanJoinGameServerRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    log.trace ("Event received [{}]", event);
    log.info ("Received join game server request from {}", client);

    // Clients should not be able to join if they do not have a valid ip address.
    // This reeks of hacking...
    if (!NetworkConstants.isValidIpAddress (client.getAddress ()))
    {
      sendJoinGameServerDeniedToHumanClient (client, "Your IP address [" + client.getAddress () + "] is invalid.");
      return;
    }

    // Client cannot join if its external address matches that of the server.
    // Reason: Servers only allow clients to join on separate machines to help prevent
    // a single user from controlling multiple players to manipulate the game's outcome.
    // While clients could be on a LAN sharing an external address and actually be on separate machines, it
    // is required for LAN clients to join using the server's internal network address, which circumvents this problem.
    if (serverHasAddress () && client.getAddress ().equals (getServerAddress ()))
    {
      sendJoinGameServerDeniedToHumanClient (client, "You cannot join this game having the same IP address ["
              + client.getAddress () + "] as the game server.\nIf you are on the same network as the server, "
              + "you can join using the game server's internal IP address to play a LAN game.");
      return;
    }

    // check if client is already in server
    if (clientsInServer.contains (client))
    {
      sendJoinGameServerDeniedToHumanClient (client, "You have already joined this game server.");
      return;
    }

    // local host cannot join a dedicated server
    if (!isHostAndPlay () && isLocalHost (client))
    {
      sendJoinGameServerDeniedToHumanClient (client, "You cannot join a dedicated game server as localhost.");
      return;
    }

    // local host must join first in a host-and-play server
    if (isHostAndPlay () && !isHostConnected () && !isLocalHost (client))
    {
      sendJoinGameServerDeniedToHumanClient (client, "Waiting for the host to connect...");
      return;
    }

    // only one local host can join a host-and-play server
    if (isHostAndPlay () && isHostConnected () && isLocalHost (client))
    {
      sendJoinGameServerDeniedToHumanClient (client, "The host has already joined this game server.");
      return;
    }

    // local host has joined the host-and-play server
    if (isHostAndPlay () && !isHostConnected () && isLocalHost (client)) host = client;

    sendJoinGameServerSuccessToHumanClient (client);
  }

  void handleEvent (final AiJoinGameServerRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    // Check to make sure that this event is not coming from a real human client.
    // AI clients never have a valid address, while human clients must.
    if (NetworkConstants.isValidIpAddress (client.getAddress ()))
    {
      // It's a human client. Send the denial to the human client.
      sendJoinGameServerDeniedToHumanClient (client, Strings.format ("Invalid AI client: [{}]", client));
      return;
    }

    log.trace ("Event received [{}]", event);
    log.info ("Received join game server request from {}", client);

    // check if client is already in server
    if (clientsInServer.contains (client))
    {
      sendJoinGameServerDeniedToAiClient (client, "You have already joined this game server.");
      return;
    }

    sendJoinGameServerSuccessToAiClient (client);
  }

  void handleEvent (final PlayerJoinGameRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    log.trace ("Event received [{}]", event);

    // client is connected but not in game server; just ignore request event
    if (!clientsInServer.contains (client))
    {
      log.warn ("Ignoring join game request from player [{}] | REASON: unrecognized client [{}].",
                event.getPlayerName (), client);
      disconnect (client);
      return;
    }

    if (clientsToSpectators.existsSpectatorWith (event.getPlayerName ()))
    {
      final SpectatorPacket nameConflictSpectator = clientsToSpectators.spectatorWith (event.getPlayerName ()).get ();
      log.warn ("Rejecting {} from [{}] because a spectator client [{}] => [{}] already exists with that name.",
                event.getClass ().getSimpleName (), client,
                clientsToSpectators.clientFor (nameConflictSpectator).get (), nameConflictSpectator);
      // this will bypass core and immediately publish the event using the existing event handler in this class
      eventBus.publish (new PlayerJoinGameDeniedEvent (event.getPlayerName (),
              PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME));
      return;
    }

    // spam guard: if client request is already being processed, ignore new request event
    if (playerJoinGameRequestCache.containsKey (event.getPlayerName ())) return;

    log.trace ("Received [{}] from player [{}]", event, event.getPlayerName ());

    playerJoinGameRequestCache.put (event.getPlayerName (), client);

    eventBus.publish (event);
  }

  void handleEvent (final PlayerRejoinGameRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    if (NetworkConstants.isValidIpAddress (client.getAddress ()))
    {
      sendToClient (client, new PlayerRejoinGameDeniedEvent (PlayerRejoinGameDeniedEvent.Reason.INVALID_ADDRESS));
      return;
    }

    final UUID playerServerId = event.getPlayerSecretId ();
    if (playerServerId == null)
    {
      sendToClient (client, new PlayerRejoinGameDeniedEvent (PlayerRejoinGameDeniedEvent.Reason.INVALID_ID));
      return;
    }

    final Optional <PlayerPacket> mappedPlayer = clientsToPlayers.playerFor (playerServerId);
    if (!mappedPlayer.isPresent ())
    {
      sendToClient (client, new PlayerRejoinGameDeniedEvent (PlayerRejoinGameDeniedEvent.Reason.PLAYER_NOT_IN_GAME));
      return;
    }

  }

  void handleEvent (final SpectatorJoinGameRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    if (!clientsInServer.contains (client))
    {
      log.warn ("Ignoring join game request from spectator [{}] | REASON: unrecognized client [{}].",
                event.getSpectatorName (), client);
      disconnectHuman (client);
      return;
    }

    if (gameServerConfig.getSpectatorLimit () == 0)
    {
      sendSpectatorJoinGameDenied (client, event.getSpectatorName (),
                                   SpectatorJoinGameDeniedEvent.Reason.SPECTATING_DISABLED);
      return;
    }

    if (clientsToSpectators.spectatorCount () >= gameServerConfig.getSpectatorLimit ())
    {
      sendSpectatorJoinGameDenied (client, event.getSpectatorName (), SpectatorJoinGameDeniedEvent.Reason.GAME_IS_FULL);
      return;
    }

    final Result <SpectatorJoinGameDeniedEvent.Reason> validateName = validateSpectatorName (event.getSpectatorName ());
    if (validateName.failed ())
    {
      sendSpectatorJoinGameDenied (client, event.getSpectatorName (), validateName.getFailureReason ());
      return;
    }

    final SpectatorPacket spectator = createNewSpectatorFromValidName (event.getSpectatorName ());
    clientsToSpectators.put (client, spectator);

    sendSpectatorJoinGameSuccessEvent (spectator);
  }

  void handleEvent (final ChatMessageRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    log.debug ("Event received [{}]", event);

    final Optional <PlayerPacket> playerQuery;
    final Optional <SpectatorPacket> spectatorQuery;
    try
    {
      playerQuery = clientsToPlayers.playerFor (client);
      spectatorQuery = clientsToSpectators.spectatorFor (client);
    }
    catch (final ClientPlayerMapping.RegisteredClientPlayerNotFoundException e)
    {
      log.error ("Error resolving client to player.", e);
      return;
    }
    catch (final ClientSpectatorMapping.RegisteredClientSpectatorNotFoundException e)
    {
      log.error ("Error resolving client to spectator.", e);
      return;
    }

    final Author author;

    if (playerQuery.isPresent ())
    {
      author = playerQuery.get ();
    }
    else if (spectatorQuery.isPresent ())
    {
      author = spectatorQuery.get ();
    }
    else
    {
      log.warn ("Ignoring chat message [{}] from unrecognized client [{}]", event, client);
      return;
    }

    sendToAllPlayersAndSpectators (new ChatMessageSuccessEvent (
            new DefaultChatMessage (author, event.getMessageText ())));
  }

  void handleEvent (final PlayerRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    assert !(event instanceof InformRequestEvent);
    assert !(event instanceof ResponseRequestEvent);

    log.trace ("Event received [{}]", event);

    final Optional <PlayerPacket> playerQuery;
    try
    {
      playerQuery = clientsToPlayers.playerFor (client);
    }
    catch (final ClientPlayerMapping.RegisteredClientPlayerNotFoundException e)
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

    coreCommunicator.publishPlayerRequestEvent (player, event);
  }

  void handleEvent (final InformRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    assert !(event instanceof PlayerRequestEvent);
    assert !(event instanceof ResponseRequestEvent);

    log.trace ("Event received [{}]", event);

    final Optional <PlayerPacket> playerQuery;
    try
    {
      playerQuery = clientsToPlayers.playerFor (client);
    }
    catch (final ClientPlayerMapping.RegisteredClientPlayerNotFoundException e)
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

    if (!waitingForRequestToInformEventFromPlayer (event.getInformType (), player))
    {
      log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding server inform event of type [{}] was sent to that player.",
                event, player, event.getInformType ());
      return;
    }

    handlePlayerInformRequestFor (event.getInformType (), event, player);
  }

  void handleEvent (final ResponseRequestEvent event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    assert !(event instanceof PlayerRequestEvent);
    assert !(event instanceof InformRequestEvent);

    log.trace ("Event received [{}]", event);

    final Optional <PlayerPacket> playerQuery;
    try
    {
      playerQuery = clientsToPlayers.playerFor (client);
    }
    catch (final ClientPlayerMapping.RegisteredClientPlayerNotFoundException e)
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

    if (!waitingForResponseToInputEventFromPlayer (event.getRequestType (), player))
    {
      log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding server request of type [{}] was sent to that player.",
                event, player, event.getRequestType ());
      return;
    }

    handlePlayerResponseRequestTo (event.getRequestType (), event, player);
  }

  // ---------- outbound server responses requiring special processing ---------- //

  private static boolean isLocalHost (final Remote client)
  {
    return client.getAddress ().equals (NetworkConstants.LOCALHOST_ADDRESS);
  }

  private static ClientConfiguration createHumanClientConfig (final Remote client)
  {
    return new DefaultClientConfiguration (client.getAddress (), client.getPort ());
  }

  private static ClientConfiguration createAiClientConfig (final Remote client)
  {
    return new AiClientConfiguration (client.getAddress (), client.getPort ());
  }

  private void sendPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent event,
                                               final PlayerNotifyJoinGameEvent directNotifyEvent)
  {
    // Get updated players & spectators in game; don't use stale values from the event.
    // PlayerJoinGameSuccessEvent#getPlayersInGame could be outdated.
    // PlayerJoinGameSuccessEvent#getSpectatorsInGame will always be empty because core doesn't track spectators.
    final ImmutableSet <PlayerPacket> playersInGame = clientsToPlayers.players ();
    final ImmutableSet <SpectatorPacket> spectatorsInGame = clientsToSpectators.spectators ();

    final PlayerJoinGameSuccessEvent nonSelfEvent = new PlayerJoinGameSuccessEvent (event.getPerson (),
            PersonIdentity.NON_SELF, playersInGame, spectatorsInGame, event.getPersonLimits ());
    final PlayerJoinGameSuccessEvent selfEvent = new PlayerJoinGameSuccessEvent (event.getPerson (),
            PersonIdentity.SELF, playersInGame, spectatorsInGame, event.getPersonLimits ());

    sendToAllPlayersExcept (event.getPerson (), nonSelfEvent);
    sendToPlayer (event.getPerson (), selfEvent);
    sendToPlayer (event.getPerson (), directNotifyEvent);
    sendToAllSpectators (nonSelfEvent);
  }

  private void sendSpectatorJoinGameSuccessEvent (final SpectatorPacket spectator)
  {
    final ImmutableSet <PlayerPacket> playersInGame = clientsToPlayers.players ();
    final ImmutableSet <SpectatorPacket> spectatorsInGame = clientsToSpectators.spectators ();

    final SpectatorJoinGameSuccessEvent nonSelfEvent = new SpectatorJoinGameSuccessEvent (spectator,
            PersonIdentity.NON_SELF, playersInGame, spectatorsInGame, gameServerConfig.getPersonLimits ());

    final SpectatorJoinGameSuccessEvent selfEvent = new SpectatorJoinGameSuccessEvent (spectator, PersonIdentity.SELF,
            playersInGame, spectatorsInGame, gameServerConfig.getPersonLimits ());

    sendToSpectator (spectator, selfEvent);
    sendToAllPlayers (nonSelfEvent);
    sendToAllSpectatorsExcept (spectator, nonSelfEvent);
  }

  private void sendJoinGameServerSuccessToHumanClient (final Remote client)
  {
    final Event successEvent = new JoinGameServerSuccessEvent (gameServerConfig, createHumanClientConfig (client));

    sendToHumanClient (client, successEvent);
    clientsInServer.add (client);

    log.info ("Client [{}] successfully joined game server.", client);
  }

  private void sendJoinGameServerSuccessToAiClient (final Remote client)
  {
    final Event successEvent = new JoinGameServerSuccessEvent (gameServerConfig, createAiClientConfig (client));

    sendToAiClient (client, successEvent);
    clientsInServer.add (client);

    log.info ("Client [{}] successfully joined game server.", client);
  }

  // ---------- internal event utility methods and types ---------- //

  private void sendJoinGameServerDeniedToHumanClient (final Remote client, final String reason)
  {
    sendToHumanClient (client, new JoinGameServerDeniedEvent (createHumanClientConfig (client), reason));
    disconnectHuman (client);

    log.warn ("Denied [{}] from [{}]; REASON: {}", JoinGameServerRequestEvent.class.getSimpleName (), client, reason);
  }

  private void sendJoinGameServerDeniedToAiClient (final Remote client, final String reason)
  {
    sendToAiClient (client, new JoinGameServerDeniedEvent (createAiClientConfig (client), reason));

    log.warn ("Denied [{}] from [{}]; REASON: {}", JoinGameServerRequestEvent.class.getSimpleName (), client, reason);
  }

  private void sendSpectatorJoinGameDenied (final Remote client,
                                            final String name,
                                            final SpectatorJoinGameDeniedEvent.Reason reason)
  {
    sendToSpectator (client, new SpectatorJoinGameDeniedEvent (name, getSpectatorLimit (), reason));
    disconnectHuman (client);
  }

  private void disconnect (final Remote client)
  {
    if (client instanceof AiClient)
    {
      disconnectAi ((AiClient) client);
      return;
    }

    disconnectHuman (client);
  }

  private void disconnectHuman (final Remote client)
  {
    clientConnector.disconnect (client);
  }

  private void disconnectAi (final AiClient client)
  {
    eventBus.publish (new AiDisconnectionEvent (client.getPlayerName ()));
  }

  private boolean serverHasAddress ()
  {
    return !gameServerConfig.getServerAddress ().isEmpty ();
  }

  private String getServerAddress ()
  {
    return gameServerConfig.getServerAddress ();
  }

  private void sendToClient (final Remote client, final Event message)
  {
    if (client instanceof AiClient)
    {
      sendToAiClient (client, message);
    }
    else
    {
      sendToHumanClient (client, message);
    }
  }

  private void sendToHumanClient (final Remote client, final Event message)
  {
    humanPlayerCommunicator.sendTo (client, message);
  }

  private void sendToAiClient (final Remote client, final Event message)
  {
    aiPlayerCommunicator.sendTo (client, message);
  }

  private void sendToSpectator (final Remote client, final Event message)
  {
    spectatorCommunicator.sendTo (client, message);
  }

  private void sendToSpectator (final SpectatorPacket spectator, final Event message)
  {
    spectatorCommunicator.sendToSpectator (spectator, message, clientsToSpectators);
  }

  private void sendToPlayer (final PlayerPacket player, final Event message)
  {
    switch (player.getSentience ())
    {
      case HUMAN:
      {
        humanPlayerCommunicator.sendToPlayer (player, message, clientsToPlayers);
        break;
      }
      case AI:
      {
        aiPlayerCommunicator.sendToPlayer (player, message, clientsToPlayers);
        break;
      }
    }
  }

  private void sendToAllPlayersExcept (final PlayerPacket player, final Event message)
  {
    humanPlayerCommunicator.sendToAllPlayersExcept (player, message, clientsToPlayers);
    aiPlayerCommunicator.sendToAllPlayersExcept (player, message, clientsToPlayers);
  }

  private void sendToAllSpectatorsExcept (final SpectatorPacket spectator, final Event message)
  {
    spectatorCommunicator.sendToAllSpectatorsExcept (spectator, message, clientsToSpectators);
  }

  private void sendToAllPlayers (final Event message)
  {
    humanPlayerCommunicator.sendToAllPlayers (message, clientsToPlayers);
    aiPlayerCommunicator.sendToAllPlayers (message, clientsToPlayers);
  }

  private void sendToAllSpectators (final Event message)
  {
    spectatorCommunicator.sendToAllSpectators (message, clientsToSpectators);
  }

  private void sendToAllPlayersAndSpectators (final Event message)
  {
    sendToAllPlayers (message);
    sendToAllSpectators (message);
  }

  private void remove (final Remote client)
  {
    clientsInServer.remove (client);
    clientsToPlayers.remove (client); // remove from players, if client is a player
    clientsToSpectators.remove (client); // remove from spectators, if client is a spectator
  }

  private boolean isHostConnected ()
  {
    return host != null && clientsInServer.contains (host);
  }

  private boolean isHostAndPlay ()
  {
    return gameServerConfig.getGameServerType () == GameServerType.HOST_AND_PLAY;
  }

  private boolean waitingForResponseToInputEventFromPlayer (final Class <? extends ServerRequestEvent> requestClass,
                                                            final PlayerPacket player)
  {
    for (final PlayerInputRequestEvent request : playerInputRequestEventCache.get (player))
    {
      if (requestClass.isInstance (request)) return true;
    }

    return false;
  }

  private boolean waitingForRequestToInformEventFromPlayer (final Class <? extends PlayerInformEvent> informClass,
                                                            final PlayerPacket player)
  {
    for (final PlayerInformEvent informEvent : playerInformEventCache.get (player))
    {
      if (informClass.isInstance (informEvent)) return true;
    }

    return false;
  }

  private Result <SpectatorJoinGameDeniedEvent.Reason> validateSpectatorName (final String name)
  {
    if (clientsToPlayers.existsPlayerWith (name))
    {
      return Result.failure (SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_PLAYER_NAME);
    }

    if (clientsToSpectators.existsSpectatorWith (name))
    {
      return Result.failure (SpectatorJoinGameDeniedEvent.Reason.DUPLICATE_SPECTATOR_NAME);
    }

    if (!GameSettings.isValidHumanPlayerNameWithOptionalClanTag (name))
    {
      return Result.failure (SpectatorJoinGameDeniedEvent.Reason.INVALID_NAME);
    }

    return Result.success ();
  }

  // note: this method assumes that 'name' has already been validated
  private SpectatorPacket createNewSpectatorFromValidName (final String name)
  {
    return new DefaultSpectatorPacket (name, UUID.randomUUID ());
  }

  private void handlePlayerResponseRequestTo (final Class <? extends ServerRequestEvent> requestClass,
                                              final ResponseRequestEvent responseRequest,
                                              final PlayerPacket player)
  {
    for (final PlayerInputRequestEvent request : playerInputRequestEventCache.get (player))
    {
      if (requestClass.isInstance (request))
      {
        final boolean wasRemoved = playerInputRequestEventCache.remove (player, request);
        assert wasRemoved;
        coreCommunicator.publishPlayerResponseRequestEvent (player, responseRequest, request);
        return;
      }
    }

    log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding request of type [{}] was sent to that player.",
              responseRequest, player, requestClass);
  }

  private void handlePlayerInformRequestFor (final Class <? extends PlayerInformEvent> informClass,
                                             final InformRequestEvent informRequest,
                                             final PlayerPacket player)
  {
    for (final PlayerInformEvent informEvent : playerInformEventCache.get (player))
    {
      if (informClass.isInstance (informEvent))
      {
        final boolean wasRemoved = playerInformEventCache.remove (player, informEvent);
        assert wasRemoved;
        coreCommunicator.publishPlayerInformRequestEvent (player, informRequest, informEvent);
        return;
      }
    }

    log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding inform event of type [{}] was sent to that player.",
              informRequest, player, informClass);
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
          Thread.sleep (connectionTimeoutMillis);
        }
        catch (final InterruptedException ignored)
        {
        }

        if (clientsInServer.contains (client)) return;

        disconnect (client);

        log.info ("Client connection timed out [{}].", client.getAddress ());
      }
    }
  }
}
