package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.server.application.ServerApplicationFactory;
import com.forerunnergames.peril.server.settings.ServerSettings;
import com.forerunnergames.tools.common.Classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main
{
  private static final Logger log = LoggerFactory.getLogger (Main.class);

  public static void main (final String... args)
  {
    try
    {
      ServerApplicationFactory.create (args).initialize ();
    }
    catch (final Throwable e)
    {
      log.error (ServerSettings.CRASH_MESSAGE, e);

      System.exit (1);
    }
  }

  private Main ()
  {
    Classes.instantiationNotAllowed ();
  }
}
