package com.forerunnergames.peril.common.eventbus;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.DeadMessage;
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
    return create (ImmutableSet.<IPublicationErrorHandler> of (), ImmutableSet.<DeadEventHandler> of ());
  }

  public static MBassador <Event> create (final Iterable <IPublicationErrorHandler> handlers)
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    return create (handlers, ImmutableSet.<DeadEventHandler> of ());
  }

  public static MBassador <Event> create (final DeadEventHandler... deadEventHandlers)
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    return create (ImmutableSet.<IPublicationErrorHandler> of (), ImmutableSet.copyOf (deadEventHandlers));
  }

  public static MBassador <Event> create (final Iterable <IPublicationErrorHandler> publicationErrorHandlers,
                                          final Iterable <DeadEventHandler> deadEventHandlers)
  {
    Arguments.checkIsNotNull (publicationErrorHandlers, "publicationErrorHandlers");
    Arguments.checkHasNoNullElements (publicationErrorHandlers, "publicationErrorHandlers");
    Arguments.checkIsNotNull (deadEventHandlers, "deadEventHandlers");
    Arguments.checkHasNoNullElements (deadEventHandlers, "deadEventHandlers");

    final MBassador <Event> eventBus = new MBassador <> (new PublicationErrorDispatcher (
            ImmutableSet.copyOf (publicationErrorHandlers)));

    eventBus.subscribe (new DeadMessageDispatcher (ImmutableSet.copyOf (deadEventHandlers)));

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
  private static final class DeadMessageDispatcher
  {
    private final ImmutableSet <DeadEventHandler> handlers;

    DeadMessageDispatcher (final ImmutableSet <DeadEventHandler> handlers)
    {
      Arguments.checkIsNotNull (handlers, "handlers");
      Arguments.checkHasNoNullElements (handlers, "handlers");

      this.handlers = handlers;
    }

    @Handler
    void onEvent (final DeadMessage deadMessage)
    {
      Arguments.checkIsNotNull (deadMessage, "deadMessage");

      // standard behavior; log the dead message
      log.warn ("Dead event detected, no handlers are registered to receive it. Event [{}]", deadMessage.getMessage ());

      // dispatch dead message to all registered handlers
      for (final DeadEventHandler handler : handlers)
      {
        handler.onDeadMessage (deadMessage);
      }
    }
  }
}
