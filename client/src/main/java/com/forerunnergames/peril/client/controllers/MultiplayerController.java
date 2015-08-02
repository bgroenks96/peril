package com.forerunnergames.peril.client.controllers;

import static com.forerunnergames.peril.client.events.EventFluency.selectedCountryNameFrom;
import static com.forerunnergames.peril.client.events.EventFluency.withGameServerConfigurationFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withServerConfigurationFrom;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;
import static com.forerunnergames.tools.net.events.EventFluency.messageFrom;
import static com.forerunnergames.tools.net.events.EventFluency.serverFrom;

import com.forerunnergames.peril.client.events.CreateGameDeniedEvent;
import com.forerunnergames.peril.client.events.CreateGameRequestEvent;
import com.forerunnergames.peril.client.events.CreateGameSuccessEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.SelectCountryEvent;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerCreator;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DestroyGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.net.client.UnknownClientConfiguration;
import com.forerunnergames.tools.net.events.local.ServerCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ServerConnectionEvent;
import com.forerunnergames.tools.net.events.local.ServerDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.RequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;
import com.forerunnergames.tools.net.server.ServerCommunicator;
import com.forerunnergames.tools.net.server.ServerConfiguration;
import com.forerunnergames.tools.net.server.ServerConnector;

import java.util.ArrayList;
import java.util.Collection;

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
  private final Collection <ServerRequestEvent> serverRequestEventCache = new ArrayList <> ();
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
    disconnectFromServer ();
    destroyServer ();
  }

  @Handler
  public static void onEvent (final ServerConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);
    log.info ("Successfully connected to server [{}].", serverFrom (event));
  }

  @Handler
  public void onEvent (final ServerDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);
    log.info ("Disconnected from server [{}].", serverFrom (event));

    eventBus.publish (new QuitGameEvent ());
  }

  @Handler
  public void onEvent (final DestroyGameServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    destroyServer ();
  }

  @Handler
  public void onEvent (final CreateGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    final Result <String> result = createGameServer (withGameServerConfigurationFrom (event));

    if (result.isFailure ())
    {
      createGameDenied (event, failureReasonFrom (result));
      return;
    }

    eventBus.publish (new CreateGameSuccessEvent (event));
  }

  @Handler (priority = CALL_FIRST)
  public void onEvent (final JoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    final Result <String> result = connectToServer (withServerConfigurationFrom (event));

    if (result.isFailure ()) joinGameServerDenied (event, failureReasonFrom (result));
  }

  @Handler (priority = CALL_LAST)
  public void onEvent (final ClientRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    sendToServer (event);
  }

  @Handler
  public void onEvent (final ServerCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    eventBus.publish (messageFrom (event));
  }

  @Handler
  public void onEvent (final ServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    serverRequestEventCache.add (event);
  }

  @Handler
  public void onEvent (final SelectCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    if (!waitingForResponseTo (PlayerSelectCountryRequestEvent.class))
    {
      log.warn ("Ignoring local event [{}] because no prior corresponding server request of type [{}] was received.",
                event, PlayerSelectCountryRequestEvent.class);
      return;
    }

    respondToServerRequest (PlayerSelectCountryRequestEvent.class, new PlayerSelectCountryResponseRequestEvent (selectedCountryNameFrom (event)));
  }

  @Handler
  public void onEvent (final QuitGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    if (!serverConnector.isConnected ()) return;

    disconnectFromServer ();
  }

  private Result <String> connectToServer (final ServerConfiguration config)
  {
    return connectToServer (config.getServerAddress (), config.getServerTcpPort (),
                            NetworkSettings.SERVER_CONNECTION_TIMEOUT_MS,
                            NetworkSettings.MAX_SERVER_CONNECTION_ATTEMPTS);
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

  private void disconnectFromServer ()
  {
    serverConnector.disconnect ();
  }

  private void destroyServer ()
  {
    gameServerCreator.destroy ();
  }

  private void joinGameServerDenied (final JoinGameServerRequestEvent event, final String reason)
  {
    eventBus.publish (new JoinGameServerDeniedEvent (event, new UnknownClientConfiguration (), reason));
  }

  private void createGameDenied (final CreateGameRequestEvent event, final String reason)
  {
    destroyServer ();

    eventBus.publish (new CreateGameDeniedEvent (event, reason));
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

  private boolean waitingForResponseTo (final Class <? extends ServerRequestEvent> serverRequestEventClass)
  {
    for (final ServerRequestEvent request : serverRequestEventCache)
    {
      if (serverRequestEventClass.isInstance (request)) return true;
    }

    return false;
  }

  private void respondToServerRequest (final Class <? extends ServerRequestEvent> serverRequestEventClass,
                                       final ResponseRequestEvent response)
  {
    for (final ServerRequestEvent request : serverRequestEventCache)
    {
      if (serverRequestEventClass.isInstance (request))
      {
        sendToServer (response);
        serverRequestEventCache.remove (request);
        return;
      }
    }

    log.warn ("Ignoring invalid client response [{}] because no prior corresponding server request of type [{}] was received.",
              response, serverRequestEventClass);
  }
}
