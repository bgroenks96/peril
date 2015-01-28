package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;

public final class MusicSettings
{
  public static boolean IS_ENABLED = false;
  public static float INITIAL_VOLUME = 1.0f;

  private MusicSettings()
  {
    Classes.instantiationNotAllowed();
  }
}
