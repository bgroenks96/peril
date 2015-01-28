package com.forerunnergames.peril.client;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerCreator;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalGameServerCreator implements GameServerCreator
{
  private static final Logger log = LoggerFactory.getLogger (LocalGameServerCreator.class);
  private Process serverProcess;
  private boolean isCreated = false;

  @Override
  public Result <String> create (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    if (isCreated)
    {
      log.warn ("Cannot launch local server because it is already running.");

      return Result.failure ("The server is already running.");
    }

    log.info ("Launching your local host & play server \"{}\" on port {} (TCP)...", config.getServerName (),
                    config.getServerTcpPort ());

    try
    {
      // @formatter:off
      serverProcess = new ProcessBuilder ("java",
                      "-jar",
                      "-ea", // TODO Remove -ea in production?
                      NetworkSettings.SERVER_JAR_NAME,
                      "--game-mode", config.getGameMode ().name(),
                      "--countries", String.valueOf (config.getTotalCountryCount ()),
                      "--title", config.getServerName (),
                      "--port", String.valueOf (config.getServerTcpPort ()),
                      "--players", String.valueOf (config.getPlayerLimit ()),
                      "--win-percent", String.valueOf (config.getWinPercentage ()),
                      "--assignment", config.getInitialCountryAssignment ().name())
                      .redirectErrorStream (true)
                      .inheritIO ()
                      .start ();
      // @formatter:on

      addShutDownHook ();

      isCreated = true;

      return Result.success ();
    }
    catch (IOException e)
    {
      destroyServerProcess ();

      log.warn ("Failed to launch local server on port [{}] (TCP).", config.getServerTcpPort ());
      log.warn ("Failure reason: [{}]", Strings.toString (e));

      return Result.failure (Strings.toString (e));
    }
  }

  @Override
  public void destroy ()
  {
    if (!isCreated) return;

    log.info ("Destroying your local host & play server...");

    serverProcess.destroy ();

    isCreated = false;
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