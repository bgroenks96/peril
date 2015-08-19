package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.core.shared.application.DefaultApplication;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.Controller;

import de.matthiasmann.AsyncExecution;

public final class ClientApplication extends DefaultApplication
{
  private final AsyncExecution mainThreadExecutor;

  public ClientApplication (final AsyncExecution mainThreadExecutor, final Controller... controllers)
  {
    super (controllers);

    Arguments.checkIsNotNull (mainThreadExecutor, "mainThreadExecutor");

    this.mainThreadExecutor = mainThreadExecutor;
  }

  @Override
  public void initialize ()
  {
    Runtime.getRuntime ().addShutdownHook (new Thread (new Runnable ()
    {
      @Override
      public void run ()
      {
        mainThreadExecutor.invokeLater (new Runnable ()
        {
          @Override
          public void run ()
          {
            shutDown ();
          }
        });
      }
    }));

    super.initialize ();
  }

  @Override
  public void update ()
  {
    super.update ();

    if (shouldShutDown ()) Gdx.app.exit ();

    mainThreadExecutor.executeQueuedJobs ();
  }
}
