package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.packets.person.ObserverPacket;
import com.forerunnergames.peril.server.controllers.ClientObserverMapping;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.ClientCommunicator;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultObserverCommunicator implements ObserverCommunicator
{
  private static final Logger log = LoggerFactory.getLogger (DefaultObserverCommunicator.class);
  private final ClientCommunicator clientCommunicator;

  public DefaultObserverCommunicator (final ClientCommunicator clientCommunicator)
  {
    Arguments.checkIsNotNull (clientCommunicator, "clientCommunicator");

    this.clientCommunicator = clientCommunicator;
  }

  @Override
  public void sendTo (final Remote client, final Object msg)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (msg, "msg");

    clientCommunicator.sendTo (client, msg);
  }

  @Override
  public void sendToAll (final Object msg)
  {
    Arguments.checkIsNotNull (msg, "msg");

    clientCommunicator.sendToAll (msg);
  }

  @Override
  public void sendToAllExcept (final Remote client, final Object msg)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (msg, "msg");

    clientCommunicator.sendToAllExcept (client, msg);
  }

  @Override
  public void sendToObserver (final ObserverPacket observer, final Object msg, final ClientObserverMapping mapping)
  {
    Arguments.checkIsNotNull (observer, "observer");
    Arguments.checkIsNotNull (msg, "msg");
    Arguments.checkIsNotNull (mapping, "mapping");

    final Optional <Remote> clientQuery = mapping.clientFor (observer);

    if (!clientQuery.isPresent ())
    {
      log.warn ("Ignoring attempt to send [{}] to disconnected observer [{}].");
      return;
    }

    sendTo (clientQuery.get (), msg);
  }

  @Override
  public void sendToAllObservers (final Object msg, final ClientObserverMapping mapping)
  {
    Arguments.checkIsNotNull (msg, "msg");
    Arguments.checkIsNotNull (mapping, "mapping");

    for (final ObserverPacket next : mapping.observers ())
    {
      sendToObserver (next, msg, mapping);
    }
  }

  @Override
  public void sendToAllObserversExcept (final ObserverPacket observer, final Object msg, final ClientObserverMapping mapping)
  {
    Arguments.checkIsNotNull (observer, "observer");
    Arguments.checkIsNotNull (msg, "msg");
    Arguments.checkIsNotNull (mapping, "mapping");

    for (final ObserverPacket next : mapping.observersExcept (observer))
    {
      sendToObserver (next, msg, mapping);
    }
  }
}
