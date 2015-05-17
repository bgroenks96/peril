package com.forerunnergames.peril.client.controllers;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withGameServerConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withServerConfigurationFrom;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;
import static com.forerunnergames.tools.net.events.EventFluency.messageFrom;
import static com.forerunnergames.tools.net.events.EventFluency.serverFrom;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerCreator;
import com.forerunnergames.peril.core.shared.net.events.client.request.CreateGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.CreateGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DestroyGameServerEvent;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.ServerCommunicator;
import com.forerunnergames.tools.net.ServerConfiguration;
import com.forerunnergames.tools.net.ServerConnector;
import com.forerunnergames.tools.net.events.RequestEvent;
import com.forerunnergames.tools.net.events.ServerCommunicationEvent;
import com.forerunnergames.tools.net.events.ServerConnectionEvent;
import com.forerunnergames.tools.net.events.ServerDisconnectionEvent;

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
 * 1) Subscribe to *RequestEvent's from the client UI logic.
 * 2) Send *RequestEvent's to the server wrapped in ClientCommunicationEvent's.
 * 3) Listen for ServerCommunicationEvent's from the server.
 * 4) Unwrap ServerCommunicationEvent's and publish the *AnswerEvent's (*SuccessEvent or *DeniedEvent) to the client UI
 * logic via the event bus, so that the UI can update it's state to accurately reflect the current state of the server.
 */
// @formatter:on
public final class MultiplayerController extends ControllerAdapter
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private static final int CALL_FIRST = 10;
  private static final int CALL_LAST = 0;
  private final GameServerCreator gameServerCreator;
  private final ServerConnector serverConnector;
  private final ServerCommunicator serverCommunicator;
  private final MBassador <Event> eventBus;

  public MultiplayerController (final GameServerCreator gameServerCreator,
                                final ServerConnector serverConnector,
                                final ServerCommunicator serverCommunicator,
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
    serverConnector.disconnect ();
    destroyServer ();
  }

  @Handler
  public void onDestroyGameServerEvent (final DestroyGameServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    destroyServer ();
  }

  @Handler (priority = CALL_FIRST)
  public void onJoinGameServerRequestEvent (final JoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    final Result <String> result = joinGameServer (withServerConfigurationFrom (event));

    if (result.isFailure ()) joinGameServerDenied (event, failureReasonFrom (result));
  }

  @Handler (priority = CALL_FIRST)
  public void onCreateGameServerRequestEvent (final CreateGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    final Result <String> result = createAndJoinGameServer (withGameServerConfigurationFrom (event));

    if (result.isFailure ()) createGameServerDenied (event, failureReasonFrom (result));
  }

  @Handler (priority = CALL_LAST)
  public void onRequestEvent (final RequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    sendToServer (event);
  }

  @Handler
  public void onServerCommunicationEvent (final ServerCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    eventBus.publish (messageFrom (event));
  }

  @Handler
  public static void onServerDisconnectEvent (final ServerDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);
    log.info ("Disconnected from server [{}].", serverFrom (event));
  }

  @Handler
  public static void onSeverConnectionEvent (final ServerConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);
    log.info ("Successfully connected to server [{}].", serverFrom (event));
  }

  private Result <String> joinGameServer (final ServerConfiguration config)
  {
    return connectToServer (config.getServerAddress (), config.getServerTcpPort (),
                            NetworkSettings.CONNECTION_TIMEOUT_MS, NetworkSettings.MAX_CONNECTION_ATTEMPTS);
  }

  private Result <String> connectToServer (final String address,
                                           final int tcpPort,
                                           final int timeoutMs,
                                           final int maxAttempts)
  {
    return serverConnector.connect (address, tcpPort, timeoutMs, maxAttempts);
  }

  private Result <String> createGameServer (final GameServerConfiguration config)
  {
    return gameServerCreator.create (config);
  }

  private void destroyServer ()
  {
    gameServerCreator.destroy ();
  }

  private void joinGameServerDenied (final JoinGameServerRequestEvent event, final String reason)
  {
    eventBus.publish (new JoinGameServerDeniedEvent (event, reason));
  }

  private Result <String> createAndJoinGameServer (final GameServerConfiguration config)
  {
    Result <String> result = createGameServer (config);

    if (result.isFailure ()) return result;

    result = joinGameServer (config);

    if (result.isFailure ()) destroyServer ();

    return result;
  }

  private void createGameServerDenied (final CreateGameServerRequestEvent event, final String reason)
  {
    destroyServer ();

    eventBus.publish (new CreateGameServerDeniedEvent (event, reason));
  }

  private void sendToServer (final RequestEvent event)
  {
    if (!serverConnector.isConnected ())
    {
      log.warn ("Prevented sending request [{}] to the server while disconnected.", event);
      return;
    }

    log.debug ("Sending request [{}] to the server.", event);

    serverCommunicator.send (event);
  }
}
