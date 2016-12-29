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
import com.forerunnergames.peril.common.net.dispatchers.NetworkEventDispatcher;
import com.forerunnergames.peril.common.net.events.client.interfaces.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerQuitGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerQuitGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.DirectPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputInformEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerDisconnectEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ResumeGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SuspendGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerInputCanceledEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerQuitGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.SpectatorJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultSpectatorPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.peril.core.events.EventRegistry;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.state.events.DestroyGameEvent;
import com.forerunnergames.peril.server.communicators.CoreCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.communicators.SpectatorCommunicator;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping.RegisteredClientPlayerNotFoundException;
import com.forerunnergames.peril.server.controllers.ClientSpectatorMapping.RegisteredClientSpectatorNotFoundException;
import com.forerunnergames.peril.server.controllers.MultiplayerControllerEventCache.PlayerInputEventTimeoutCallback;
import com.forerunnergames.peril.server.dispatchers.ClientRequestEventDispatchListener;
import com.forerunnergames.peril.server.dispatchers.ClientRequestEventDispatcher;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.NetworkConstants;
import com.forerunnergames.tools.net.NetworkTools;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.client.configuration.DefaultClientConfiguration;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.InformRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;
import com.forerunnergames.tools.net.server.remote.RemoteClient;
import com.forerunnergames.tools.net.server.remote.RemoteClientConnector;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiplayerController extends ControllerAdapter
        implements ClientRequestEventDispatchListener, PlayerInputEventTimeoutCallback
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private final MultiplayerControllerEventCache eventCache;
  private final Set <RemoteClient> clientsInServer = Collections.synchronizedSet (new HashSet <RemoteClient> ());
  private final ClientPlayerMapping clientsToPlayers;
  private final ClientSpectatorMapping clientsToSpectators;
  private final ClientConnectorDaemon connectorDaemon = new ClientConnectorDaemon ();
  private final GameServerConfiguration gameServerConfig;
  private final RemoteClientConnector clientConnector;
  private final PlayerCommunicator humanPlayerCommunicator;
  private final PlayerCommunicator aiPlayerCommunicator;
  private final SpectatorCommunicator spectatorCommunicator;
  private final CoreCommunicator coreCommunicator;
  private final EventRegistry eventRegistry;
  private final MBassador <Event> eventBus;
  private NetworkEventDispatcher networkEventDispatcher;
  private boolean shouldShutDown = false;
  private int connectionTimeoutMillis = NetworkSettings.CLIENT_CONNECTION_TIMEOUT_MS;
  @Nullable
  private RemoteClient host = null;
  // @formatter:on

  public MultiplayerController (final GameServerConfiguration gameServerConfig,
                                final RemoteClientConnector clientConnector,
                                final PlayerCommunicator humanPlayerCommunicator,
                                final PlayerCommunicator aiPlayerCommunicator,
                                final SpectatorCommunicator spectatorCommunicator,
                                final CoreCommunicator coreCommunicator,
                                final EventRegistry eventRegistry,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConnector, "clientConnector");
    Arguments.checkIsNotNull (humanPlayerCommunicator, "humanPlayerCommunicator");
    Arguments.checkIsNotNull (aiPlayerCommunicator, "aiPlayerCommunicator");
    Arguments.checkIsNotNull (spectatorCommunicator, "spectatorCommunicator");
    Arguments.checkIsNotNull (coreCommunicator, "coreCommunicator");
    Arguments.checkIsNotNull (eventRegistry, "eventRegistry");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.gameServerConfig = gameServerConfig;
    this.clientConnector = clientConnector;
    this.humanPlayerCommunicator = humanPlayerCommunicator;
    this.aiPlayerCommunicator = aiPlayerCommunicator;
    this.spectatorCommunicator = spectatorCommunicator;
    this.coreCommunicator = coreCommunicator;
    this.eventRegistry = eventRegistry;
    this.eventBus = eventBus;

    eventCache = new MultiplayerControllerEventCache (this,
            gameServerConfig.getServerRequestTimeout (TimeUnit.MILLISECONDS));
    clientsToPlayers = new ClientPlayerMapping (coreCommunicator, gameServerConfig.getTotalPlayerLimit ());
    clientsToSpectators = new ClientSpectatorMapping (gameServerConfig.getSpectatorLimit ());
  }

  @Override
  public void initialize ()
  {
    log.trace ("Initializing {} for game server '{}'", getClass ().getSimpleName (),
               gameServerConfig.getGameServerName ());

    networkEventDispatcher = new ClientRequestEventDispatcher (eventBus.getRegisteredErrorHandlers (), this);
    networkEventDispatcher.initialize ();

    eventBus.subscribe (this);
    eventBus.publish (new CreateGameEvent ());
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
    networkEventDispatcher.shutDown ();
    connectorDaemon.threadPool.shutdown ();
    shouldShutDown = true;
  }

  @Override
  public void onEventTimedOut (final PlayerInputEvent inputEvent)
  {
    coreCommunicator.notifyInputEventTimedOut (inputEvent);
  }

  // ---------- Remote inbound ClientRequestEvent callbacks from NetworkEventDispatcher ---------- //

  @Override
  public void handleEvent (final HumanJoinGameServerRequestEvent event, final RemoteClient client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    log.trace ("Event received [{}]", event);
    log.info ("Received join game server request from {}", client);

    // Clients should not be able to join if they do not have a valid ip address.
    // This reeks of hacking...
    if (!NetworkTools.isValidIpAddress (client.getAddress ()))
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

  @Override
  public void handleEvent (final AiJoinGameServerRequestEvent event, final RemoteClient client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    // Check to make sure that this event is not coming from a real human client.
    // AI clients never have a valid address, while human clients must.
    if (NetworkTools.isValidIpAddress (client.getAddress ()))
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

  @Override
  public void handleEvent (final PlayerJoinGameRequestEvent event, final RemoteClient client)
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
    if (eventCache.isPendingJoin (event.getPlayerName ())) return;

    log.trace ("Received [{}] from player [{}]", event, event.getPlayerName ());

    // add player to pending join request cache
    eventCache.addPendingPlayerJoin (event.getPlayerName (), client);

    // if the join request includes a player server id, process as a rejoin attempt;
    // otherwise, forward the request to core as a new join attempt
    if (event.hasPlayerSecretId ())
    {
      handlePlayerRejoinAttempt (client, event);
      return;
    }

    // forward event to Core
    eventBus.publish (event);
  }

  @Override
  public void handleEvent (final PlayerQuitGameRequestEvent event, final RemoteClient client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    Optional <PlayerPacket> playerMaybe = Optional.absent ();
    try
    {
      playerMaybe = clientsToPlayers.playerFor (client);
    }
    catch (final RegisteredClientPlayerNotFoundException e)
    {
      log.error ("Error resolving client to player.", e);
    }

    if (!playerMaybe.isPresent ())
    {
      sendToClient (client, new PlayerQuitGameDeniedEvent (PlayerQuitGameDeniedEvent.Reason.PLAYER_DOES_NOT_EXIST));
      return;
    }

    final PlayerPacket player = playerMaybe.get ();
    sendToAllPlayersAndSpectators (new PlayerQuitGameSuccessEvent (player, clientsToPlayers.players (),
            clientsToPlayers.unmappedPlayers ()));
    removeAndUnmapPlayer (client);
    disconnect (client);
  }

  @Override
  public void handleEvent (final SpectatorJoinGameRequestEvent event, final RemoteClient client)
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

  @Override
  public void handleEvent (final ChatMessageRequestEvent event, final RemoteClient client)
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

  @Override
  public void handleEvent (final PlayerRequestEvent event, final RemoteClient client)
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

    eventRegistry.registerTo (playerQuery.get (), event);

    publish (event);
  }

  @Override
  public void handleEvent (final PlayerResponseRequestEvent <?> event, final RemoteClient client)
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

    if (!waitingForResponseToInputEventFromPlayer (event.getQuestionType (), player))
    {
      log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding server request of type [{}] was sent to that player.",
                event, player, event.getQuestionType ());
      return;
    }

    eventRegistry.registerTo (player, event);

    handlePlayerResponseRequestTo (event.getQuestionType (), event, player);
  }

  @Override
  public void handleEvent (final PlayerInformRequestEvent <?> event, final RemoteClient client)
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

    if (!waitingForRequestToInformEventFromPlayer (event.getQuestionType (), player))
    {
      log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding server inform event of type [{}] was sent to that player.",
                event, player, event.getQuestionType ());
      return;
    }

    eventRegistry.registerTo (player, event);

    handlePlayerInformRequestFor (event.getQuestionType (), event, player);
  }

  // ---------- Public getters and setters ---------- //

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

  public boolean isClientInServer (final RemoteClient client)
  {
    Arguments.checkIsNotNull (client, "client");

    return clientsInServer.contains (client);
  }

  Optional <UUID> getPlayerServerId (final PlayerPacket player)
  {
    return clientsToPlayers.serverIdFor (player);
  }

  // ---------- Remote inbound client event communication ---------- //

  @Handler
  void onEvent (final ClientConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);
    log.info ("Client [{}] connected.", clientFrom (event));

    connectorDaemon.onConnect (clientFrom (event));
  }

  @Handler
  void onEvent (final ClientDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final RemoteClient client = clientFrom (event);

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

    final boolean isUnexpectedDisconnect = playerQuery.isPresent ();
    if (isUnexpectedDisconnect)
    {
      handlePlayerDisconnect (playerQuery.get (), client);
    }

    // NOTE: this is no longer necessary (I *think*) because this is an expected case when a player quits
    // voluntarily... processing of PlayerQuitGameRequestEvent will finish before this handler is executed
    //
    // if (!playerQuery.isPresent () && !spectatorQuery.isPresent ())
    // {
    // log.warn ("Client [{}] disconnected but did not exist as a player or spectator.", client);
    // }

    // removes the client from the server/mapping, if necessary
    remove (client);
  }

  @Handler
  void onEvent (final ClientCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    networkEventDispatcher.dispatch (event.getMessage (), event.getRemote ());
  }

  // ---------- Local inbound events from core module ---------- //

  @Handler
  void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final String playerName = event.getPersonName ();

    // if no client mapping is available, silently ignore success event and tell core to remove this player
    // this shouldn't happen, since this would imply that core processed a player join game request without
    // server receiving one... so basically the result of a bug or a hack.
    if (!eventCache.isPendingJoin (playerName))
    {
      log.warn ("No client join game request in cache for player: {}. Player will be removed.", playerName);
      // coreCommunicator.notifyRemovePlayerFromGame (event.getPerson ());
      return;
    }

    // fetch and remove player name from request cache
    final RemoteClient client = eventCache.removePendingPlayerJoin (playerName);

    final PlayerPacket newPlayer = event.getPerson ();

    // only add a player/client mapping if the client still exists in the game server
    if (clientsInServer.contains (client))
    {
      final Optional <PlayerPacket> oldPlayer = clientsToPlayers.bind (client, newPlayer);
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
      // coreCommunicator.notifyRemovePlayerFromGame (event.getPerson ());
      return;
    }

    final Optional <UUID> playerServerId = clientsToPlayers.serverIdFor (newPlayer);
    if (!playerServerId.isPresent ())
    {
      Exceptions.throwIllegalState ("No server uuid found for player [{}].", newPlayer);
    }

    sendPlayerJoinGameSuccessEvent (event);
  }

  @Handler
  void onEvent (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final String playerName = event.getPlayerName ();

    // if no client mapping is available, silently ignore denied event
    // this is to prevent failure under cases such as client disconnecting while join request is being processed
    if (!eventCache.isPendingJoin (playerName)) return;

    final RemoteClient client = eventCache.pendingClientFor (playerName);

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

    eventCache.removePendingPlayerJoin (playerName);
  }

  @Handler
  void onEvent (final PlayerLoseGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Optional <RemoteClient> optionalClient = clientsToPlayers.clientFor (event.getPerson ());
    if (!optionalClient.isPresent ())
    {
      log.warn ("No client mapping for player in received event [{}].", event);
      return;
    }

    final RemoteClient client = optionalClient.get ();

    // remove client/player mapping; keep client in server
    clientsToPlayers.unbind (client);

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
    // Also bypasses stage 1 of authenticating as a client via JoinGameServerRequestEvent because
    // the client already previously authenticated as a player, and is still connected to the server.
    eventBus.publish (new ClientCommunicationEvent (client,
            new SpectatorJoinGameRequestEvent (event.getPersonName ())));

    // let handler for broadcast events handle forwarding the event
  }

  @Handler
  void onEvent (final PlayerInputRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final boolean wasAdded = eventCache.add (event.getPerson (), event);
    assert wasAdded;

    // let handler for direct player event handle forwarding the event
  }

  @Handler
  void onEvent (final PlayerInputInformEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final boolean wasAdded = eventCache.add (event.getPerson (), event);
    assert wasAdded;

    // let handler for direct player event handle forwarding the event
  }

  @Handler
  void onEvent (final PlayerInputEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    if (clientsToPlayers.existsClientFor (event.getPerson ()))
    {
      return;
    }

    // publish suspend game event for new input events that come in while no client
    // is present for the player
    publish (new SuspendGameEvent (SuspendGameEvent.Reason.PLAYER_UNAVAILABLE));
  }

  void onEvent (final PlayerInputCanceledEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    eventCache.remove (event.getOriginalInputEvent ());

    // let direct player event handler publish the event
  }

  @Handler (priority = Integer.MIN_VALUE)
  void onEvent (final BroadcastEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    // PlayerJoinGameSuccessEvent requires special processing.
    if (event instanceof PlayerJoinGameSuccessEvent) return;

    log.trace ("Event received [{}]", event);

    sendToAllPlayersAndSpectators (event);
  }

  @Handler (priority = Integer.MIN_VALUE)
  void onEvent (final DirectPlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    sendToPlayer (event.getPerson (), event);
  }

  // ---------- Outbound server responses requiring special processing ---------- //

  private void sendPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent successEvent)
  {
    // Get updated players & spectators in game; don't use stale values from the event.
    // PlayerJoinGameSuccessEvent#getPlayersInGame could be outdated.
    // PlayerJoinGameSuccessEvent#getSpectatorsInGame will always be empty because core doesn't track spectators.
    final ImmutableSet <PlayerPacket> playersInGame = clientsToPlayers.players ();
    final ImmutableSet <SpectatorPacket> spectatorsInGame = clientsToSpectators.spectators ();

    final PlayerJoinGameSuccessEvent nonSelfEvent = new PlayerJoinGameSuccessEvent (successEvent.getPerson (),
            PersonIdentity.NON_SELF, playersInGame, spectatorsInGame, successEvent.getPersonLimits ());

    final Optional <UUID> selfServerId = clientsToPlayers.serverIdFor (successEvent.getPerson ());
    assert selfServerId.isPresent ();
    final PlayerJoinGameSuccessEvent selfEvent = new PlayerJoinGameSuccessEvent (successEvent.getPerson (),
            selfServerId.get (), playersInGame, spectatorsInGame, successEvent.getPersonLimits ());

    sendToAllPlayersExcept (successEvent.getPerson (), nonSelfEvent);
    sendToPlayer (successEvent.getPerson (), selfEvent);
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

  private void sendJoinGameServerSuccessToHumanClient (final RemoteClient client)
  {
    final Event successEvent = new JoinGameServerSuccessEvent (gameServerConfig, createHumanClientConfig (client));

    sendToHumanClient (client, successEvent);
    clientsInServer.add (client);

    log.info ("Client [{}] successfully joined game server.", client);
  }

  private void sendJoinGameServerSuccessToAiClient (final RemoteClient client)
  {
    final Event successEvent = new JoinGameServerSuccessEvent (gameServerConfig, createAiClientConfig (client));

    sendToAiClient (client, successEvent);
    clientsInServer.add (client);

    log.info ("Client [{}] successfully joined game server.", client);
  }

  private void sendJoinGameServerDeniedToHumanClient (final RemoteClient client, final String reason)
  {
    sendToHumanClient (client, new JoinGameServerDeniedEvent (createHumanClientConfig (client), reason));
    disconnectHuman (client);

    log.warn ("Denied [{}] from [{}]; REASON: {}", JoinGameServerRequestEvent.class.getSimpleName (), client, reason);
  }

  private void sendJoinGameServerDeniedToAiClient (final RemoteClient client, final String reason)
  {
    sendToAiClient (client, new JoinGameServerDeniedEvent (createAiClientConfig (client), reason));

    log.warn ("Denied [{}] from [{}]; REASON: {}", JoinGameServerRequestEvent.class.getSimpleName (), client, reason);
  }

  private void sendSpectatorJoinGameDenied (final RemoteClient client,
                                            final String name,
                                            final SpectatorJoinGameDeniedEvent.Reason reason)
  {
    sendToSpectator (client, new SpectatorJoinGameDeniedEvent (name, getSpectatorLimit (), reason));
    disconnectHuman (client);
  }

  // ---------- Internal event utility methods and types ---------- //

  private boolean isLocalHost (final RemoteClient client)
  {
    return client.getAddress ().equals (NetworkConstants.LOCALHOST_ADDRESS);
  }

  private ClientConfiguration createHumanClientConfig (final RemoteClient client)
  {
    return new DefaultClientConfiguration (client.getAddress (), client.getPort ());
  }

  private ClientConfiguration createAiClientConfig (final RemoteClient client)
  {
    return new AiClientConfiguration (client.getAddress (), client.getPort ());
  }

  private void disconnect (final RemoteClient client)
  {
    if (client instanceof AiClient)
    {
      disconnectAi ((AiClient) client);
      return;
    }

    disconnectHuman (client);
  }

  private void disconnectHuman (final RemoteClient client)
  {
    clientConnector.disconnect (client);
  }

  private void disconnectAi (final AiClient client)
  {
    eventBus.publish (new AiDisconnectionEvent (client.getPlayerName ()));
  }

  private boolean serverHasAddress ()
  {
    return !gameServerConfig.getAddress ().isEmpty ();
  }

  private String getServerAddress ()
  {
    return gameServerConfig.getAddress ();
  }

  private void sendToClient (final RemoteClient client, final Event message)
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

  private void sendToHumanClient (final RemoteClient client, final Event message)
  {
    humanPlayerCommunicator.sendTo (client, message);
  }

  private void sendToAiClient (final RemoteClient client, final Event message)
  {
    aiPlayerCommunicator.sendTo (client, message);
  }

  private void sendToSpectator (final RemoteClient client, final Event message)
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
      default:
        break;
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

  private void publish (final Event event)
  {
    eventBus.publish (event);
  }

  private void remove (final RemoteClient client)
  {
    clientsInServer.remove (client);
    clientsToPlayers.unbind (client); // unbind client from player, if client is a player
    clientsToSpectators.remove (client); // remove from spectators, if client is a spectator
  }

  private void removeAndUnmapPlayer (final RemoteClient client)
  {
    Optional <PlayerPacket> player = Optional.absent ();
    try
    {
      player = clientsToPlayers.playerFor (client);
    }
    catch (final RegisteredClientPlayerNotFoundException e)
    {
      log.error ("Error resolving client to player.", e);
    }

    remove (client);

    if (!player.isPresent ())
    {
      return;
    }

    clientsToPlayers.unmap (player.get ());
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
    for (final PlayerInputRequestEvent request : eventCache.inputRequestsFor (player))
    {
      if (requestClass.isInstance (request)) return true;
    }

    return false;
  }

  private boolean waitingForRequestToInformEventFromPlayer (final Class <? extends PlayerInputInformEvent> informClass,
                                                            final PlayerPacket player)
  {
    for (final PlayerInputInformEvent informEvent : eventCache.informEventsFor (player))
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

  private void handlePlayerDisconnect (final PlayerPacket player, final RemoteClient client)
  {
    final PlayerPacket disconnectedPlayer = player;
    log.warn ("Client [{}] for player [{}] disconnected unexpectedly!", disconnectedPlayer, client);
    final PlayerDisconnectEvent disconnectEvent = new PlayerDisconnectEvent (disconnectedPlayer);
    sendToAllPlayersExcept (disconnectedPlayer, disconnectEvent);
    sendToAllSpectators (disconnectEvent);

    if (!eventCache.hasPendingEvents (disconnectedPlayer))
    {
      return;
    }

    publish (new SuspendGameEvent (SuspendGameEvent.Reason.PLAYER_UNAVAILABLE));
  }

  private void handlePlayerRejoinAttempt (final RemoteClient client, final PlayerJoinGameRequestEvent event)
  {
    assert event.hasPlayerSecretId ();

    // check if address is valid or not
    if (!NetworkTools.isValidIpAddress (client.getAddress ()))
    {
      log.warn ("Client [{}] does not have a valid IP address for reconnection.", client);
      eventBus.publish (new PlayerJoinGameDeniedEvent (event.getPlayerName (),
              PlayerJoinGameDeniedEvent.Reason.INVALID_ADDRESS));
      return;
    }

    final UUID playerServerId = event.getPlayerSecretId ();
    final Optional <PlayerPacket> mappedPlayerMaybe = clientsToPlayers.playerFor (playerServerId);
    if (!mappedPlayerMaybe.isPresent ())
    {
      log.warn ("Received unrecognized server ID [{}] from client [{}]. Aborting reconnection attempt.", playerServerId,
                client);
      eventBus.publish (new PlayerJoinGameDeniedEvent (event.getPlayerName (),
              PlayerJoinGameDeniedEvent.Reason.INVALID_ID));
      return;
    }

    final PlayerPacket mappedPlayer = mappedPlayerMaybe.get ();
    if (!mappedPlayer.hasName (event.getPlayerName ()))
    {
      log.warn ("Unexpected name '{}' for player [{}] with server ID [{}]. Aborting reconnection attempt.",
                event.getPlayerName (), mappedPlayer, playerServerId);
      eventBus.publish (new PlayerJoinGameDeniedEvent (event.getPlayerName (),
              PlayerJoinGameDeniedEvent.Reason.NAME_MISMATCH));
      return;
    }

    clientsToPlayers.bind (client, mappedPlayer);

    PlayerPacket updatedPlayer;
    try
    {
      updatedPlayer = clientsToPlayers.playerFor (client).get ();
    }
    catch (final RegisteredClientPlayerNotFoundException e)
    {
      log.error ("Error resolving player for client.", e);
      clientsToPlayers.unbind (client);
      return;
    }

    final PlayerJoinGameSuccessEvent successEvent = new PlayerJoinGameSuccessEvent (updatedPlayer,
            clientsToPlayers.players (), gameServerConfig.getPersonLimits ());
    publish (successEvent);

    // send current game state to player
    coreCommunicator.requestSendGameStateTo (updatedPlayer);

    if (!eventCache.hasPendingEvents (updatedPlayer))
    {
      return;
    }

    // resume game and republish pending input events
    publish (new ResumeGameEvent ());
    resendPendingInputEventsFor (updatedPlayer);
  }

  private void handlePlayerResponseRequestTo (final Class <? extends ServerRequestEvent> requestClass,
                                              final PlayerResponseRequestEvent <?> responseRequest,
                                              final PlayerPacket player)
  {
    for (final PlayerInputRequestEvent request : eventCache.inputRequestsFor (player))
    {
      if (requestClass.isInstance (request))
      {
        final boolean wasRemoved = eventCache.remove (player, request);
        assert wasRemoved;
        publish (responseRequest);
        return;
      }
    }

    log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding request of type [{}] was sent to that player.",
              responseRequest, player, requestClass);
  }

  private void handlePlayerInformRequestFor (final Class <? extends PlayerInputInformEvent> informClass,
                                             final PlayerInformRequestEvent <?> informRequest,
                                             final PlayerPacket player)
  {
    for (final PlayerInputInformEvent informEvent : eventCache.informEventsFor (player))
    {
      if (informClass.isInstance (informEvent))
      {
        final boolean wasRemoved = eventCache.remove (player, informEvent);
        assert wasRemoved;
        publish (informRequest);
        return;
      }
    }

    log.warn ("Ignoring event [{}] from player [{}] because no prior corresponding inform event of type [{}] was sent to that player.",
              informRequest, player, informClass);
  }

  private void resendPendingInputEventsFor (final PlayerPacket player)
  {
    for (final PlayerInputEvent event : Sets.union (eventCache.informEventsFor (player),
                                                    eventCache.inputRequestsFor (player)))
    {
      sendToPlayer (player, event);
    }
  }

  private final class ClientConnectorDaemon
  {
    private final ExecutorService threadPool = Executors.newCachedThreadPool ();

    public void onConnect (final RemoteClient client)
    {
      Arguments.checkIsNotNull (client, "client");

      threadPool.execute (new WaitForConnectionTask (client));
    }

    private class WaitForConnectionTask implements Runnable
    {
      private final RemoteClient client;

      WaitForConnectionTask (final RemoteClient client)
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
