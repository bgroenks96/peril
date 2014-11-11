package com.forerunnergames.peril.core.shared.application;

import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.CompositeController;
import com.forerunnergames.tools.common.Controller;

import org.bushe.swing.event.EventServiceExistsException;
import org.bushe.swing.event.EventServiceLocator;
import org.bushe.swing.event.ThreadSafeEventService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBasedApplication implements Application
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedApplication.class);
  private final Controller controller;

  public EventBasedApplication (final Controller... controllers)
  {
    Arguments.checkIsNotNull (controllers, "controllers");
    Arguments.checkHasNoNullElements (controllers, "controllers");

    controller = new CompositeController (controllers);
  }

  @Override
  public void initialize()
  {
    initializeEventBus();

    controller.initialize();
  }

  private void initializeEventBus()
  {
    try
    {
      EventServiceLocator.setEventService (EventServiceLocator.SERVICE_NAME_SWING_EVENT_SERVICE, new ThreadSafeEventService());
    }
    catch (final EventServiceExistsException e)
    {
      log.error ("Could not initialize EventBus.", e);
    }
  }

  @Override
  public void shutDown()
  {
    controller.shutDown();
  }

  public boolean shouldShutDown()
  {
    return controller.shouldShutDown();
  }
}
