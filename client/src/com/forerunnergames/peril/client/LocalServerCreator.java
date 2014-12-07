package com.forerunnergames.peril.client;

import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.net.ServerCreator;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalServerCreator implements ServerCreator
{
  private static final Logger log = LoggerFactory.getLogger (LocalServerCreator.class);
  private Process serverProcess;
  private boolean isCreated = false;

  @Override
  public Result <String> create (final String name, final int tcpPort)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (tcpPort, "tcpPort");

    if (isCreated)
    {
      log.warn ("Cannot launch local server because it is already running.");

      return Result.failure ("The server is already running.");
    }

    log.info ("Launching your local host & play server \"{}\" on port {} (TCP)...", name, tcpPort);

    try
    {
      serverProcess = new ProcessBuilder (
              "java",
              "-jar",
              "-ea", // TODO Remove -ea in production?
              NetworkSettings.SERVER_JAR_NAME,
              "-title", name,
              "-port", String.valueOf (tcpPort),
              "-players", String.valueOf (0))
              .redirectErrorStream (true)
              .inheritIO()
              .start();

      addShutDownHook();

      // TODO Java 7: Use ProcessBuilder#inheritIO() to redirect subprocess io to the parent process.
      //redirectProcessOutput (serverProcess.getInputStream());

      isCreated = true;

      return Result.success();
    }
    catch (IOException e)
    {
      destroyServerProcess();

      log.warn ("Failed to launch local server on port [{}] (TCP).", tcpPort);
      log.warn ("Failure reason: [{}]", Strings.toString (e));

      return Result.failure (Strings.toString (e));
    }
  }

  private void addShutDownHook()
  {
    Runtime.getRuntime().addShutdownHook (new Thread (new Runnable()
    {
      @Override
      public void run()
      {
        destroyServerProcess();
      }
    }));
  }

  private void destroyServerProcess()
  {
    if (serverProcess != null) serverProcess.destroy();
  }

  /*
  private void redirectProcessOutput (final InputStream processOutput)
  {
    new Thread (new Runnable()
    {
      @Override
      public void run()
      {
        Scanner scanner = new Scanner (processOutput);

        while (scanner.hasNextLine())
        {
          System.out.println (scanner.nextLine());
        }
      }
    }).start();
  }
  */

  @Override
  public String resolveAddress()
  {
    // TODO Resolve external ip address instead of using localhost.

    return NetworkSettings.LOCALHOST_ADDRESS;
  }

  @Override
  public void destroy()
  {
    if (! isCreated) return;

    log.info ("Destroying your local host & play server...");

    serverProcess.destroy();

    isCreated = false;
  }
}
