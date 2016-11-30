/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.net.dispatchers;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.net.Remote;

import com.google.common.collect.Maps;

import java.util.Map;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses {@link MBassador} to resolve the runtime types of various events. It is the responsibility of implementors to
 * provide {@link Handler} methods for the interested events. Remembers the original remote client sender called via
 * {@link #dispatch(Event, Remote)}, providing a mechanism to resolve the sender, for implementors to use, via
 * {@link #senderOfDispatchedEvent(Event)}.
 */
public abstract class AbstractNetworkEventDispatcher implements NetworkEventDispatcher
{
  private static final int CALL_LAST = Integer.MIN_VALUE;
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final Map <Event, Remote> eventClientCache = Maps.newConcurrentMap ();
  private final MBassador <Event> internalEventBus;

  protected AbstractNetworkEventDispatcher (final Iterable <IPublicationErrorHandler> internalBusErrorHandlers)
  {
    Arguments.checkIsNotNull (internalBusErrorHandlers, "internalBusErrorHandlers");
    Arguments.checkHasNoNullElements (internalBusErrorHandlers, "internalBusErrorHandlers");

    internalEventBus = EventBusFactory.create (internalBusErrorHandlers);
  }

  @Override
  public final void initialize ()
  {
    internalEventBus.subscribe (this); // Subscribes this class and any children.
  }

  @Override
  public final void dispatch (final Event event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    eventClientCache.put (event, client);
    internalEventBus.publish (event);
  }

  @Override
  public void shutDown ()
  {
    internalEventBus.unsubscribe (this);
    internalEventBus.shutdown ();
    eventClientCache.clear ();
  }

  protected final Remote senderOfDispatchedEvent (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsTrue (eventClientCache.containsKey (event),
                               "Cannot resolve sender for event [{}] because event was never dispatched.", event);

    return eventClientCache.get (event);
  }

  @Handler (priority = CALL_LAST)
  private void afterEvent (final Event anyEvent)
  {
    assert anyEvent != null;
    eventClientCache.remove (anyEvent);
  }
}
