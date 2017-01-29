/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.server.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.eventbus.EventBusPipe;
import com.forerunnergames.peril.common.net.events.ipc.interfaces.InterProcessEvent;
import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.peril.server.application.ServerApplication;
import com.forerunnergames.peril.server.application.ServerApplicationFactory;
import com.forerunnergames.peril.server.main.args.CommandLineArgs;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main
{
  private static final Logger log = LoggerFactory.getLogger (Main.class);

  private static ServerInterProcessClient ipcClient = new ServerInterProcessClient ();

  private static boolean isInitialized = false;

  public static void main (final String... args)
  {
    Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler ()
    {
      @Override
      public void uncaughtException (final Thread t, final Throwable e)
      {
        log.error ("The server application has crashed!\n\nA crash file has been created in \"{}\".\n\n",
                   CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY, e);

        System.exit (1);
      }
    });

    final CommandLineArgs jArgs = new CommandLineArgs ();
    final JCommander jCommander = new JCommander (jArgs);
    jCommander.setProgramName ("java -jar " + NetworkSettings.SERVER_JAR_NAME);
    final StringBuilder usageStringBuilder = new StringBuilder ();

    try
    {
      jCommander.parse (args);
    }
    catch (final ParameterException e)
    {
      jCommander.usage (usageStringBuilder);
      log.info ("\n\n{}\n\nOptions with * are required\n\n{}", e.getMessage (), usageStringBuilder);
      System.exit (1);
    }

    if (jArgs.help)
    {
      jCommander.usage (usageStringBuilder);
      log.info ("\n\nOptions with * are required\n\n{}", usageStringBuilder);
      System.exit (0);
    }

    final EventBusPipe <InterProcessEvent, Event> pipe = ipcClient.createPipe ();
    final ServerApplication application = ServerApplicationFactory.create (jArgs, pipe);

    ipcClient.initialize (jArgs.callbackTcpPort);
    application.initialize ();

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool (1);
    executor.scheduleAtFixedRate (new Runnable ()
    {
      @Override
      public void run ()
      {
        application.update ();

        if (!isInitialized) finishInitialization (application);

        if (application.shouldShutDown ()) shutDown (application, executor);
      }
    }, 0, 250, TimeUnit.MILLISECONDS);
  }

  private static void finishInitialization (final ServerApplication application)
  {
    ipcClient.finishInitialize (application);
    isInitialized = true;
  }

  private static void shutDown (final ServerApplication application, final ExecutorService executor)
  {
    application.shutDown ();
    executor.shutdown ();
    ipcClient.disconnect ();
  }

  private Main ()
  {
    Classes.instantiationNotAllowed ();
  }
}
