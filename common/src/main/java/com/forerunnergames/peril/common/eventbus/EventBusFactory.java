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

package com.forerunnergames.peril.common.eventbus;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.PublicationEvent;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBusFactory
{
  private static final Logger log = LoggerFactory.getLogger (EventBusFactory.class);

  public static MBassador <Event> create ()
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    return create (ImmutableSet.<IPublicationErrorHandler> of (), ImmutableSet.<UnhandledEventHandler> of ());
  }

  public static MBassador <Event> create (final Iterable <IPublicationErrorHandler> handlers)
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    return create (handlers, ImmutableSet.<UnhandledEventHandler> of ());
  }

  public static MBassador <Event> create (final UnhandledEventHandler... unhandledEventHandlers)
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    return create (ImmutableSet.<IPublicationErrorHandler> of (), ImmutableSet.copyOf (unhandledEventHandlers));
  }

  public static MBassador <Event> create (final Iterable <IPublicationErrorHandler> publicationErrorHandlers,
                                          final Iterable <UnhandledEventHandler> unhandledEventHandlers)
  {
    Arguments.checkIsNotNull (publicationErrorHandlers, "publicationErrorHandlers");
    Arguments.checkHasNoNullElements (publicationErrorHandlers, "publicationErrorHandlers");
    Arguments.checkIsNotNull (unhandledEventHandlers, "unhandledEventHandlers");
    Arguments.checkHasNoNullElements (unhandledEventHandlers, "unhandledEventHandlers");

    final MBassador <Event> eventBus = new MBassador<> (
            new PublicationErrorDispatcher (ImmutableSet.copyOf (publicationErrorHandlers)));

    eventBus.subscribe (new UnhandledEventDispatcher (ImmutableSet.copyOf (unhandledEventHandlers)));

    return eventBus;
  }

  private EventBusFactory ()
  {
    Classes.instantiationNotAllowed ();
  }

  private static class PublicationErrorDispatcher implements IPublicationErrorHandler
  {
    private final ImmutableSet <IPublicationErrorHandler> registeredHandlers;

    PublicationErrorDispatcher (final ImmutableSet <IPublicationErrorHandler> registeredHandlers)
    {
      Arguments.checkIsNotNull (registeredHandlers, "registeredHandlers");
      Arguments.checkHasNoNullElements (registeredHandlers, "registeredHandlers");

      this.registeredHandlers = registeredHandlers;
    }

    @Override
    public void handleError (final PublicationError error)
    {
      Arguments.checkIsNotNull (error, "error");

      // standard behavior; log the error message
      log.error (error.toString (), error.getCause ());

      // dispatch error to all registered handlers
      for (final IPublicationErrorHandler handler : registeredHandlers)
      {
        handler.handleError (error);
      }
    }
  }

  @Listener (references = References.Strong)
  private static final class UnhandledEventDispatcher
  {
    private final ImmutableSet <UnhandledEventHandler> handlers;

    UnhandledEventDispatcher (final ImmutableSet <UnhandledEventHandler> handlers)
    {
      Arguments.checkIsNotNull (handlers, "handlers");
      Arguments.checkHasNoNullElements (handlers, "handlers");

      this.handlers = handlers;
    }

    @Handler
    void onEvent (final PublicationEvent unhandledEvent)
    {
      Arguments.checkIsNotNull (unhandledEvent, "unhandledEvent");

      // standard behavior; log the unhandled event
      log.warn ("Unhandled event detected, no handlers are registered to receive it. Event [{}]",
                unhandledEvent.getMessage ());

      // dispatch unhandled event to all registered handlers
      for (final UnhandledEventHandler handler : handlers)
      {
        handler.onUnhandledEvent (unhandledEvent);
      }
    }
  }
}
