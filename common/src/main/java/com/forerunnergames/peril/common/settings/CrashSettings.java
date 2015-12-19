package com.forerunnergames.peril.common.settings;

import com.forerunnergames.tools.common.Classes;

import java.io.File;

public final class CrashSettings
{
  public static final String ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY = System.getProperty ("user.home") + File.separator
          + "peril" + File.separator + "crashes";

  private CrashSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
