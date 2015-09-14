package com.forerunnergames.peril.common.eventbus;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBusFactory
{
  private static final Logger log = LoggerFactory.getLogger (EventBusFactory.class);

  public static MBassador <Event> create ()
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    return create (ImmutableSet.<IPublicationErrorHandler> of (), new LoggingDeadEventHandler ());
  }

  public static MBassador <Event> create (final Iterable <IPublicationErrorHandler> handlers)
  {
    return create (handlers, new LoggingDeadEventHandler ());
  }

  public static MBassador <Event> create (final DeadEventHandler... deadEventHandlers)
  {
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    return create (ImmutableSet.<IPublicationErrorHandler> of (), deadEventHandlers);
  }

  public static MBassador <Event> create (final Iterable <IPublicationErrorHandler> publicationErrorHandlers,
                                          final DeadEventHandler... deadEventHandlers)
  {
    Arguments.checkIsNotNull (publicationErrorHandlers, "publicationErrorHandlers");
    Arguments.checkHasNoNullElements (publicationErrorHandlers, "publicationErrorHandlers");
    Arguments.checkIsNotNull (deadEventHandlers, "deadEventHandlers");
    Arguments.checkHasNoNullElements (deadEventHandlers, "deadEventHandlers");

    final MBassador <Event> eventBus = new MBassador <> (new BusConfiguration ()
            .addFeature (Feature.SyncPubSub.Default ()).addFeature (Feature.AsynchronousHandlerInvocation.Default ())
            .addFeature (Feature.AsynchronousMessageDispatch.Default ())
            .setProperty (Properties.Handler.PublicationError,
                          new PublicationErrorDispatcher (ImmutableSet.copyOf (publicationErrorHandlers))));

    for (final DeadEventHandler handler : deadEventHandlers)
    {
      eventBus.subscribe (handler);
    }

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
}
