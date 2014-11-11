package com.forerunnergames.peril.core.shared.application;

import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.CompositeController;
import com.forerunnergames.tools.common.controllers.Controller;

import org.bushe.swing.event.EventServiceExistsException;
import org.bushe.swing.event.EventServiceLocator;
import org.bushe.swing.event.ThreadSafeEventService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBasedApplication implements Application
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedApplication.class);
  private final CompositeController compositeController;

  public EventBasedApplication (final Controller... controllers)
  {
    Arguments.checkIsNotNull (controllers, "controllers");
    Arguments.checkHasNoNullElements (controllers, "controllers");

    compositeController = new CompositeController (controllers);
  }

  @Override
  public void initialize()
  {
    initializeEventBus();

    compositeController.initialize();
  }

  @Override
  public void add (final Controller controller)
  {
    Arguments.checkIsNotNull (controller, "controller");

    compositeController.add (controller);
  }

  @Override
  public void remove (final Controller controller)
  {
    Arguments.checkIsNotNull (controller, "controller");

    compositeController.remove (controller);
  }

  @Override
  public void update()
  {
    compositeController.update();
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
    compositeController.shutDown();
  }

  @Override
  public boolean shouldShutDown()
  {
    return compositeController.shouldShutDown();
  }
}
