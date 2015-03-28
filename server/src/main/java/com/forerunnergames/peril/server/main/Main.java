package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.server.application.ServerApplicationFactory;
import com.forerunnergames.tools.common.Classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main
{
  private static final Logger log = LoggerFactory.getLogger (Main.class);

  public static void main (final String... args)
  {
    Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler ()
    {
      @Override
      public void uncaughtException (final Thread thread, final Throwable throwable)
      {
        log.error ("The server application has crashed!", throwable);

        System.exit (1);
      }
    });

    ServerApplicationFactory.create (args).initialize ();
  }

  private Main ()
  {
    Classes.instantiationNotAllowed ();
  }
}
