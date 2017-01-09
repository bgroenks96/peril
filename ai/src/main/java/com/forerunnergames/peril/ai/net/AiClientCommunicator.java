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

package com.forerunnergames.peril.ai.net;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.remote.RemoteClient;
import com.forerunnergames.tools.net.server.remote.RemoteClientCommunicator;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AiClientCommunicator implements RemoteClientCommunicator
{
  private static final Logger log = LoggerFactory.getLogger (AiClientCommunicator.class);
  private final MBassador <Event> internalEventBus;

  public AiClientCommunicator (final MBassador <Event> internalEventBus)
  {
    Arguments.checkIsNotNull (internalEventBus, "internalEventBus");

    this.internalEventBus = internalEventBus;
  }

  @Override
  public void sendTo (final RemoteClient client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    send (object);
  }

  @Override
  public void sendToAll (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    send (object);
  }

  @Override
  public void sendToAllExcept (final RemoteClient client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    send (object);
  }

  private void send (final Object message)
  {
    if (!(message instanceof Event))
    {
      log.warn ("Ignoring attempt to send non-Event message [{}] to all AI clients.", message);
      return;
    }

    send ((Event) message);
  }

  private void send (final Event message)
  {
    log.debug ("Sending message [{}] to all AI clients.", message);

    internalEventBus.publish (message);
  }
}
