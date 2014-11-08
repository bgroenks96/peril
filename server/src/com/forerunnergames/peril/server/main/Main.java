package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.server.application.ServerApplicationFactory;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

public final class Main
{
  private static final Logger log = LoggerFactory.getLogger (Main.class);

  public static void main (final String... args)
  {
    try
    {
      final CommandLineArgs jArgs = new CommandLineArgs();
      new JCommander (jArgs, args);
      final Application application = ServerApplicationFactory.create (jArgs.title, jArgs.tcpPort, jArgs.playerLimit);

      application.initialize();

      Runtime.getRuntime().addShutdownHook (new Thread (new Runnable()
      {
        @Override
        public void run()
        {
          application.shutDown();
        }
      }));
    }
    catch (final Throwable e)
    {
      log.error ("\n\n\nOh no! The server has crashed!\n\nFor help, please send this log, along with an explanation " +
              "of exactly what you were doing when it crashed, to support@forerunnergames.com.\n\nWe WILL get back " +
              "to you because we actually care ;-)\n\n\n", e);

      System.exit (1);
    }
  }

  private Main()
  {
    Classes.instantiationNotAllowed();
  }
}
