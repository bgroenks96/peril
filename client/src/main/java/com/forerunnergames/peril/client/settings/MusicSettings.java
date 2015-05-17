package com.forerunnergames.peril.client.settings;

public final class MusicSettings
{
  public static final float MIN_VOLUME = 0.0f;
  public static final float MAX_VOLUME = 1.0f;
  public static boolean IS_ENABLED = true;
  public static float INITIAL_VOLUME = MAX_VOLUME;

  public static boolean isEnabled ()
  {
    return IS_ENABLED;
  }

  public static float getMinVolume ()
  {
    return MIN_VOLUME;
  }

  public static float getMaxVolume ()
  {
    return MAX_VOLUME;
  }

  public static float getInitialVolume ()
  {
    return INITIAL_VOLUME;
  }
}
