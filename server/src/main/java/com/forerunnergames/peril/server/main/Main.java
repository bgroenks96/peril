/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.peril.server.application.ServerApplicationFactory;
import com.forerunnergames.peril.server.main.args.CommandLineArgs;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Classes;

import com.google.common.base.Throwables;

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

    final Application application = ServerApplicationFactory.create (jArgs);

    application.initialize ();

    // TODO Use a ScheduledExecutorService
    while (!application.shouldShutDown ())
    {
      application.update ();

      try
      {
        Thread.sleep (250);
      }
      catch (final InterruptedException e)
      {
        Thread.currentThread ().interrupt ();
      }
    }

    application.shutDown ();
  }

  private Main ()
  {
    Classes.instantiationNotAllowed ();
  }
}
