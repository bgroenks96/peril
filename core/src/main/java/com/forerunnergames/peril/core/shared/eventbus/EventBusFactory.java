package com.forerunnergames.peril.core.shared.eventbus;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

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
    final MBassador <Event> eventBus =
            new MBassador <> (new BusConfiguration ().addFeature (Feature.SyncPubSub.Default ())
                    .addFeature (Feature.AsynchronousHandlerInvocation.Default ())
                    .addFeature (Feature.AsynchronousMessageDispatch.Default ())
                    .setProperty (Properties.Handler.PublicationError, new IPublicationErrorHandler ()
                    {
                      @Override
                      public void handleError (final PublicationError error)
                      {
                        log.error (error.toString (), error.getCause ());
                      }
                    }));

    eventBus.subscribe (new DeadEventHandler ());

    return eventBus;
  }

  private EventBusFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
