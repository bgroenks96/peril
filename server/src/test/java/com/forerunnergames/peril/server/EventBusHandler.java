package com.forerunnergames.peril.server;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public class EventBusHandler
{
  public final MBassador <Event> eventBus;

  private Event lastEvent;

  public EventBusHandler (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    eventBus.subscribe (this);

    this.eventBus = eventBus;
  }

  public synchronized <T> T lastEvent (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.cast (lastEvent);
  }

  public synchronized <T> boolean lastEventWasType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.isInstance (lastEvent);
  }

  public synchronized Class <?> lastEventType ()
  {
    return lastEvent.getClass ();
  }

  @Handler (priority = 10)
  public synchronized void onEvent (final Event event)
  {
    lastEvent = event;
  }
}
