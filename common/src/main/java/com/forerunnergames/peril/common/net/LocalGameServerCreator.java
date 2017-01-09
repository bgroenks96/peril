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

package com.forerunnergames.peril.common.net;

import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalGameServerCreator implements GameServerCreator
{
  private static final Logger log = LoggerFactory.getLogger (LocalGameServerCreator.class);
  @Nullable
  private Process serverProcess = null;
  private boolean isCreated = false;
  @Nullable
  private volatile String error = null;

  @Override
  public Result <String> create (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    if (isCreated)
    {
      log.warn ("Cannot launch local server because it is already running.");

      return Result.failure ("The server is already running.");
    }

    log.info ("Launching your local host & play server \"{}\" on port {} (TCP)...", config.getGameServerName (),
              config.getPort ());

    try
    {
      // @formatter:off
      serverProcess = new ProcessBuilder ("java",
                      "-jar",
                      "-ea", // TODO Production: Remove
                      NetworkSettings.SERVER_JAR_NAME, // TODO Specify the server jar name on the command line?
                      "--game-mode", config.getGameMode ().name(),
                      "--server-type", config.getGameServerType ().name (),
                      "--title", config.getGameServerName (),
                      "--port", String.valueOf (config.getPort ()),
                      "--human-players", String.valueOf (config.getPlayerLimitFor (PersonSentience.HUMAN)),
                      "--ai-players", String.valueOf (config.getPlayerLimitFor (PersonSentience.AI)),
                      "--spectators", String.valueOf (config.getSpectatorLimit ()),
                      "--win-percent", String.valueOf (config.getWinPercentage ()),
                      "--assignment", config.getInitialCountryAssignment ().name(),
                      "--map-name", config.getPlayMapName ())
                      .redirectErrorStream (true)
                      .start ();
      // @formatter:on

      addShutDownHook ();
      read (serverProcess.getInputStream ());

      try
      {
        // It takes a while for the server process to actually get up and running.
        // If we don't wait, there will be connection failures if attempting to connect immediately after creation.
        Thread.sleep (1000);
      }
      catch (final InterruptedException ignored)
      {
        Thread.currentThread ().interrupt ();
      }

      if (error != null) throw new IOException (error);

      isCreated = true;

      log.info ("Successfully launched your local host & play server \"{}\" on port {} (TCP)...",
                config.getGameServerName (), config.getPort ());

      return Result.success ();
    }
    catch (final IOException e)
    {
      destroyServerProcess ();

      log.warn ("Failed to launch local server on port [{}] (TCP).", config.getPort ());
      log.warn ("Failure reason: [{}]", Strings.toString (e));

      return Result.failure (Strings.toString (e));
    }
  }

  @Override
  public void destroy ()
  {
    if (!isCreated) return;

    log.info ("Destroying your local host & play server...");

    destroyServerProcess ();

    isCreated = false;
  }

  @Override
  public boolean isCreated ()
  {
    return isCreated;
  }

  private void read (final InputStream inputStream)
  {
    error = null;

    new Thread (new Runnable ()
    {
      @Override
      public void run ()
      {
        final Scanner scanner = new Scanner (inputStream);

        while (scanner.hasNextLine ())
        {
          final String line = scanner.nextLine ();

          System.out.println (Strings.format ("Server Process: {}", line));

          final String lowerCaseLine = line.toLowerCase ();

          if (lowerCaseLine.contains ("error") || lowerCaseLine.contains ("exception")
                  || lowerCaseLine.contains ("crash"))
          {
            synchronized (this)
            {
              if (error == null)
              {
                error = line;
              }
              else
              {
                error += "\n\n" + line;
              }
            }
          }
        }
      }
    }).start ();
  }

  private void addShutDownHook ()
  {
    Runtime.getRuntime ().addShutdownHook (new Thread (new Runnable ()
    {
      @Override
      public void run ()
      {
        destroyServerProcess ();
      }
    }));
  }

  private void destroyServerProcess ()
  {
    if (serverProcess != null) serverProcess.destroy ();
  }
}
