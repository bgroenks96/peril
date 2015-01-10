package com.forerunnergames.peril.core.shared.application;

import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.CompositeController;
import com.forerunnergames.tools.common.controllers.Controller;

public class EventBasedApplication implements Application
{
  private final CompositeController compositeController;

  public EventBasedApplication (final Controller... controllers)
  {
    Arguments.checkIsNotNull (controllers, "controllers");
    Arguments.checkHasNoNullElements (controllers, "controllers");

    compositeController = new CompositeController (controllers);
  }

  @Override
  public void initialize ()
  {
    compositeController.initialize ();
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
  public void update ()
  {
    compositeController.update ();
  }

  @Override
  public void shutDown ()
  {
    compositeController.shutDown ();
  }

  @Override
  public boolean shouldShutDown ()
  {
    return compositeController.shouldShutDown ();
  }
}
