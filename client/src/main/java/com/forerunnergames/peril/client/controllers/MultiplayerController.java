package com.forerunnergames.peril.client.controllers;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.*;
import static com.forerunnergames.tools.common.net.events.EventFluency.answerFrom;
import static com.forerunnergames.tools.common.net.events.EventFluency.serverFrom;

import com.forerunnergames.peril.core.shared.net.events.denied.JoinMultiplayerServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.OpenMultiplayerServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.JoinMultiplayerServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.OpenMultiplayerServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.CloseMultiplayerServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;
import com.forerunnergames.tools.common.net.ServerCommunicator;
import com.forerunnergames.tools.common.net.ServerConnector;
import com.forerunnergames.tools.common.net.ServerCreator;
import com.forerunnergames.tools.common.net.events.RequestEvent;
import com.forerunnergames.tools.common.net.events.ServerCommunicationEvent;
import com.forerunnergames.tools.common.net.events.ServerConnectionEvent;
import com.forerunnergames.tools.common.net.events.ServerDisconnectionEvent;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facilitates communication between the server and the client UI logic.
 *
 * This is accomplished in the following manner:
 *
 * 1) Subscribe to *RequestEvent's from the client UI logic.
 * 2) Send *RequestEvent's to the server wrapped in ClientCommunicationEvent's.
 * 3) Listen for ServerCommunicationEvent's from the server.
 * 4) Unwrap ServerCommunicationEvent's and publish the *AnswerEvent's (*SuccessEvent or *DeniedEvent) to the
 *    client UI logic via the event bus, so that the UI can update it's state to accurately reflect the current state
 *    of the server.
 */
public final class MultiplayerController extends ControllerAdapter
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private final ServerCreator serverCreator;
  private final ServerConnector serverConnector;
  private final ServerCommunicator serverCommunicator;
  private final int CALL_FIRST = -1;
  private final int CALL_LAST = 1;

  public MultiplayerController (final ServerCreator serverCreator,
                                final ServerConnector serverConnector,
                                final ServerCommunicator serverCommunicator)
  {
    Arguments.checkIsNotNull (serverCreator, "serverCreator");
    Arguments.checkIsNotNull (serverConnector, "serverConnector");
    Arguments.checkIsNotNull (serverCommunicator, "serverCommunicator");

    this.serverCreator = serverCreator;
    this.serverConnector = serverConnector;
    this.serverCommunicator = serverCommunicator;
  }

  @Override
  public void initialize()
  {
    AnnotationProcessor.process (this);
  }

  @Override
  public void shutDown()
  {
    serverConnector.disconnect();
    destroyServer();
  }

  @EventSubscriber
  public void onEvent (final ServerConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);
    log.info ("Successfully connected to server [{}].", serverFrom (event));
  }

  @EventSubscriber
  public void onEvent (final ServerDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);
    log.info ("Disconnected from server [{}].", serverFrom (event));
  }

  @EventSubscriber
  public void onEvent (final ServerCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    EventBus.publish (answerFrom (event));
  }

  @EventSubscriber (priority = CALL_FIRST)
  public void onEvent (final OpenMultiplayerServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    final Result <String> result = openMultiplayerServer (withNameFrom (event), withTcpPortFrom (event));

    if (result.isFailure()) openMultiplayerServerDenied (event, result.getFailureReason());
  }

  @EventSubscriber (priority = CALL_FIRST)
  public void onEvent (final JoinMultiplayerServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    final Result <String> result = joinMultiplayerServer (withAddressFrom (event), withTcpPortFrom (event));

    if (result.isFailure()) joinMultiplayerServerDenied (event, result.getFailureReason());
  }

  @EventSubscriber (priority = CALL_LAST)
  public void onEvent (final RequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    sendToServer (event);
  }

  @EventSubscriber
  public void onEvent (final CloseMultiplayerServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received.", event);

    destroyServer();
  }

  private void sendToServer (final RequestEvent event)
  {
    if (! serverConnector.isConnected())
    {
      log.warn ("Prevented sending request [{}] to the server while disconnected.", event);
      return;
    }

    log.debug ("Sending request [{}] to the server.", event);

    serverCommunicator.send (event);
  }

  private Result <String> openMultiplayerServer (final String name, final int tcpPort)
  {
    Result <String> result = createServer (name, tcpPort);

    if (result.isFailure()) return result;

    result = connectToServer (resolveServerAddress(), tcpPort);

    if (result.isFailure()) destroyServer();

    return result;
  }

  private void openMultiplayerServerDenied (final OpenMultiplayerServerRequestEvent event, final String reason)
  {
    destroyServer();
    EventBus.publish (new OpenMultiplayerServerDeniedEvent (event, reason));
  }

  private Result <String> joinMultiplayerServer (final String address, final int tcpPort)
  {
    return connectToServer (address, tcpPort);
  }

  private void joinMultiplayerServerDenied (final JoinMultiplayerServerRequestEvent event, final String reason)
  {
    EventBus.publish (new JoinMultiplayerServerDeniedEvent (event, reason));
  }

  private Result <String> createServer (final String name, final int tcpPort)
  {
    return serverCreator.create (name, tcpPort);
  }

  private void destroyServer()
  {
    serverCreator.destroy();
  }

  private String resolveServerAddress()
  {
    return serverCreator.resolveAddress();
  }

  private Result <String> connectToServer (final String address, final int tcpPort)
  {
    return connectToServer (address, tcpPort, NetworkSettings.CONNECTION_TIMEOUT_MS, NetworkSettings.MAX_CONNECTION_ATTEMPTS);
  }

  private Result <String> connectToServer (final String address, final int tcpPort, final int timeoutMs, final int maxAttempts)
  {
    return serverConnector.connect (address, tcpPort, timeoutMs, maxAttempts);
  }
}
