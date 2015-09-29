package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;

public final class MusicSettings
{
  public static final float MIN_VOLUME = 0.0f;
  public static final float MAX_VOLUME = 1.0f;
  public static boolean IS_ENABLED = true;
  public static float INITIAL_VOLUME = MAX_VOLUME;

  private MusicSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
