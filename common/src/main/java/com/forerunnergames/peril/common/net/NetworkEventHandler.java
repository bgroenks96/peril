package com.forerunnergames.peril.common.net;

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

public abstract class NetworkEventHandler
{
  protected static final int CALL_LAST = -1;
  private final Map <Event, Remote> eventClientCache = Maps.newConcurrentMap ();
  private final MBassador <Event> internalEventBus;

  protected NetworkEventHandler (final Iterable <IPublicationErrorHandler> internalBusErrorHandlers)
  {
    Arguments.checkIsNotNull (internalBusErrorHandlers, "persistedErrorHandlers");
    Arguments.checkHasNoNullElements (internalBusErrorHandlers, "persistedErrorHandlers");

    internalEventBus = EventBusFactory.create (internalBusErrorHandlers);
  }

  protected abstract void subscribe (final MBassador <Event> eventBus);

  public final void handle (final Event event, final Remote client)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (client, "client");

    eventClientCache.put (event, client);
    internalEventBus.publish (event);
  }

  protected final void initialize ()
  {
    internalEventBus.subscribe (this);
    subscribe (internalEventBus);
  }

  protected final Remote clientFor (final Event event)
  {
    Preconditions.checkIsTrue (eventClientCache.containsKey (event), String.format ("%s not registered.", event));

    return eventClientCache.get (event);
  }

  @Handler (priority = CALL_LAST)
  private void afterEvent (final Event anyEvent)
  {
    eventClientCache.remove (anyEvent);
  }
}
