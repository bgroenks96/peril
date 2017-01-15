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

package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.core.events.internal.interfaces.InternalRequestEvent;
import com.forerunnergames.peril.core.events.internal.interfaces.InternalResponseEvent;
import com.forerunnergames.peril.core.events.internal.player.NotifyPlayerInputTimeoutEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import java.util.Set;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class DefaultCoreCommunicator implements CoreCommunicator
{
  private final Set <InternalResponseEvent> responses = Sets.newConcurrentHashSet ();
  private final MBassador <Event> eventBus;

  public DefaultCoreCommunicator (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;

    eventBus.subscribe (this);
  }

  @Override
  public void notifyInputEventTimedOut (final PlayerInputEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    eventBus.publish (new NotifyPlayerInputTimeoutEvent (event));
  }

  @Handler
  public void onInternalResponseEvent (final InternalResponseEvent response)
  {
    Arguments.checkIsNotNull (response, "response");

    responses.add (response);
  }

  private Optional <InternalResponseEvent> getResponseFor (final InternalRequestEvent requestEvent)
  {
    for (final InternalResponseEvent response : responses)
    {
      if (response.getRequestEventId ().is (requestEvent.getEventId ())) return Optional.of (response);
    }

    return Optional.absent ();
  }
}
