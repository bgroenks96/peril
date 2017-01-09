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

package com.forerunnergames.peril.client.net;

import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;
import static com.forerunnergames.tools.net.events.EventFluency.messageFrom;
import static com.forerunnergames.tools.net.events.EventFluency.serverFrom;

import com.forerunnergames.peril.client.events.ConnectToServerDeniedEvent;
import com.forerunnergames.peril.client.events.ConnectToServerRequestEvent;
import com.forerunnergames.peril.client.events.ConnectToServerSuccessEvent;
import com.forerunnergames.peril.client.events.CreateGameServerDeniedEvent;
import com.forerunnergames.peril.client.events.CreateGameServerRequestEvent;
import com.forerunnergames.peril.client.events.CreateGameServerSuccessEvent;
import com.forerunnergames.peril.client.events.DisconnectFromServerDebugEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.RejoinGameErrorEvent;
import com.forerunnergames.peril.client.io.CachedGameSessionManager;
import com.forerunnergames.peril.client.ui.screens.menus.modes.classic.joingame.HumanJoinGameServerHandler;
import com.forerunnergames.peril.common.JoinGameServerHandler;
import com.forerunnergames.peril.common.JoinGameServerListenerAdapter;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerCreator;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.client.remote.RemoteServerCommunicator;
import com.forerunnergames.tools.net.client.remote.RemoteServerConnector;
import com.forerunnergames.tools.net.events.local.ServerCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ServerConnectionEvent;
import com.forerunnergames.tools.net.events.local.ServerDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.base.Throwables;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @formatter:off
/**
 * Facilitates communication between the server and the client UI logic.
 *
 * This is accomplished in the following manner:
 *
 * 1) Subscribe to ClientRequestEvent's from the client UI logic.
 * 2) Send ClientRequestEvent's to the server wrapped in ClientCommunicationEvent's.
 * 3) Listen for ServerCommunicationEvent's from the server.
 * 4) Unwrap ServerCommunicationEvent's and publish the ServerEvent's to the client UI logic via the event bus,
 * so that the UI can update it's state to accurately reflect the current state of the server.
 */
// @formatter:on
public final class MultiplayerController extends ControllerAdapter
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private final GameServerCreator gameServerCreator;
  private final RemoteServerConnector serverConnector;
  private final RemoteServerCommunicator serverCommunicator;
  private final MBassador <Event> eventBus;
  private final JoinGameServerHandler rejoinGameServerHandler;
  private final AtomicBoolean shouldRejoinGame = new AtomicBoolean ();
  @Nullable
  private ServerConfiguration serverConfig;

  public MultiplayerController (final GameServerCreator gameServerCreator,
                                final RemoteServerConnector serverConnector,
                                final RemoteServerCommunicator serverCommunicator,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (gameServerCreator, "gameServerCreator");
    Arguments.checkIsNotNull (serverConnector, "serverConnector");
    Arguments.checkIsNotNull (serverCommunicator, "serverCommunicator");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.gameServerCreator = gameServerCreator;
    this.serverConnector = serverConnector;
    this.serverCommunicator = serverCommunicator;
    this.eventBus = eventBus;
    rejoinGameServerHandler = new HumanJoinGameServerHandler (eventBus);
  }

  @Override
  public void initialize ()
  {
    eventBus.subscribe (this);
  }

  @Override
  public void shutDown ()
  {
    eventBus.unsubscribe (this);
    shutDownServer ();
  }

  @Handler
  public void onEvent (final ServerConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.info ("Successfully connected to server [{}].", serverFrom (event));
  }

  @Handler
  public void onEvent (final ServerDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event [{}] received.", event);
    log.info ("Disconnected from server [{}].", serverFrom (event));

    if (shouldRejoinGame.get ()) rejoinGame ();
  }

  @Handler
  public void onEvent (final CreateGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final Result <String> result = createGameServer (event.getGameServerConfiguration ());

    if (result.isFailure ())
    {
      createGameServerDenied (event, failureReasonFrom (result));
      return;
    }

    eventBus.publish (new CreateGameServerSuccessEvent (event));
  }

  // TODO Production: Remove.
  @Handler
  public void onEvent (final DisconnectFromServerDebugEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    disconnectFromServer ();
  }

  @Handler
  public void onEvent (final ConnectToServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final Result <String> result = connectToServer (event.getServerConfiguration ());

    if (result.isFailure ())
    {
      connectToServerDenied (event, failureReasonFrom (result));
      return;
    }

    eventBus.publish (new ConnectToServerSuccessEvent (event));
  }

  @Handler
  public void onEvent (final ClientRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    sendToServer (event);
  }

  @Handler
  public void onEvent (final ServerCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    eventBus.publish (messageFrom (event));
  }

  @Handler
  public void onEvent (final QuitGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (CachedGameSessionManager.existsSession ()) CachedGameSessionManager.deleteSession ();
    shutDownServer ();
  }

  @Handler
  public void onEvent (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    serverConfig = event.getGameServerConfiguration ();
  }

  @Handler
  public void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (!event.hasIdentity (PersonIdentity.SELF) || !event.hasSelfPlayerSecretId ()) return;

    if (serverConfig == null)
    {
      log.warn ("Cannot save game session for player: [{}] because there is no server configuration. Event: [{}]",
                event.getPerson (), event);
      return;
    }

    CachedGameSessionManager.saveSession (event.getPersonName (), event.getSelfPlayerSecretId (), serverConfig);

    shouldRejoinGame.set (true);
  }

  private void rejoinGame ()
  {
    final DataResult <CachedGameSessionManager.CachedGameSession, Exception> result = CachedGameSessionManager
            .loadSession ();
    CachedGameSessionManager.deleteSession ();

    if (result.failed ())
    {
      // @formatter:off
      final String errorMessage =
              Strings.format ("An error occurred while attempting to rejoin the game.\n\n"
                      + "Problem:\n\n{}\n\nDetails:\n\n{}",
                              Throwables.getRootCause (result.getFailureReason ()).getMessage (),
                              Strings.toString (result.getFailureReason ()));
      // @formatter:on
      log.warn (errorMessage);
      eventBus.publish (new RejoinGameErrorEvent (errorMessage));
      return;
    }

    final CachedGameSessionManager.CachedGameSession gameSession = result.getReturnValue ();

    rejoinGameServerHandler.join (gameSession.getPlayerName (), gameSession.getServerAddress (),
                                  new ReconnectionHandler (gameSession.getPlayerSecretId (), eventBus));
  }

  private Result <String> connectToServer (final ServerConfiguration config)
  {
    return serverConnector.connectNow (config, NetworkSettings.SERVER_CONNECTION_TIMEOUT_MS,
                                       NetworkSettings.MAX_SERVER_CONNECTION_ATTEMPTS);
  }

  private Result <String> createGameServer (final GameServerConfiguration config)
  {
    return gameServerCreator.create (config);
  }

  private void disconnectFromServer ()
  {
    serverConnector.disconnect ();
  }

  private void destroyGameServer ()
  {
    gameServerCreator.destroy ();
  }

  private void connectToServerDenied (final ConnectToServerRequestEvent event, final String reason)
  {
    disconnectFromServer ();

    eventBus.publish (new ConnectToServerDeniedEvent (event, reason));
  }

  private void createGameServerDenied (final CreateGameServerRequestEvent event, final String reason)
  {
    destroyGameServer ();

    eventBus.publish (new CreateGameServerDeniedEvent (event, reason));
  }

  private void sendToServer (final ClientRequestEvent event)
  {
    if (!serverConnector.isConnected ())
    {
      log.warn ("Prevented sending request [{}] to the server while disconnected.", event);
      return;
    }

    log.debug ("Sending request [{}] to the server.", event);

    serverCommunicator.send (event);
  }

  private void shutDownServer ()
  {
    shouldRejoinGame.set (false);
    disconnectFromServer ();
    destroyGameServer ();
  }

  private static final class ReconnectionHandler extends JoinGameServerListenerAdapter
  {
    private final UUID playerSecretId;
    private final MBassador <Event> eventBus;

    @Override
    public void onJoinStart (final String playerName, final ServerConfiguration config)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (config, "config");

      eventBus.publishAsync (new ConnectToServerRequestEvent (config));
    }

    @Override
    public void onJoinGameServerSuccess (final String playerName, final JoinGameServerSuccessEvent event)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (event, "event");

      log.debug ("Attempting to rejoin game as player: [{}] with secret ID: [{}]...", playerName, playerSecretId);

      eventBus.publishAsync (new HumanPlayerJoinGameRequestEvent (playerName, playerSecretId));
    }

    private ReconnectionHandler (final UUID playerSecretId, final MBassador <Event> eventBus)
    {
      this.playerSecretId = playerSecretId;
      this.eventBus = eventBus;
    }
  }
}
