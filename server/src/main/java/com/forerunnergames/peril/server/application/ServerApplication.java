package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.core.shared.application.EventBasedApplication;
import com.forerunnergames.tools.common.controllers.Controller;

public final class ServerApplication extends EventBasedApplication
{
  public ServerApplication (final Controller... controllers)
  {
    super (controllers);
  }

  @Override
  public void initialize()
  {
    Runtime.getRuntime ().addShutdownHook (new Thread (new Runnable ()
    {
      @Override
      public void run ()
      {
        shutDown ();
      }
    }));

    super.initialize ();
  }
}
