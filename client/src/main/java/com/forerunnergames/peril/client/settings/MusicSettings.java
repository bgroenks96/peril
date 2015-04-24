package com.forerunnergames.peril.client.settings;

public final class MusicSettings
{
  public static final float MIN_VOLUME = 0.0f;
  public static final float MAX_VOLUME = 1.0f;
  public static boolean IS_ENABLED = true;
  public static float INITIAL_VOLUME = MAX_VOLUME;

  public boolean isEnabled ()
  {
    return IS_ENABLED;
  }

  public float getMinVolume ()
  {
    return MIN_VOLUME;
  }

  public float getMaxVolume ()
  {
    return MAX_VOLUME;
  }

  public float getInitialVolume ()
  {
    return INITIAL_VOLUME;
  }
}
