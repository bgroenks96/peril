package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.packets.person.ObserverPacket;
import com.forerunnergames.peril.server.controllers.ClientObserverMapping;
import com.forerunnergames.tools.net.client.ClientCommunicator;

public interface ObserverCommunicator extends ClientCommunicator
{
  void sendToObserver (final ObserverPacket observer, final Object msg, final ClientObserverMapping mapping);

  void sendToAllObservers (final Object msg, final ClientObserverMapping mapping);

  void sendToAllObserversExcept (final ObserverPacket observer, final Object msg, final ClientObserverMapping mapping);
}
