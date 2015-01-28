package com.forerunnergames.peril.server.settings;

import com.forerunnergames.tools.common.Classes;

public final class ServerSettings
{
  public static final String CRASH_MESSAGE = "\n\n\nOh no! The server has crashed!\n\nFor help, please send this "
                  + "log, along with an explanation of exactly what you were doing when it crashed, to "
                  + "support@forerunnergames.com.\n\nWe WILL get back to you because we actually " + "care ;-)\n\n\n";

  private ServerSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
