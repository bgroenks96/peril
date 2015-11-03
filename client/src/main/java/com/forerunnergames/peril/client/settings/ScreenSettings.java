package com.forerunnergames.peril.client.settings;

import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public final class ScreenSettings
{
  public static final int REFERENCE_SCREEN_WIDTH = 1920;
  public static final int REFERENCE_SCREEN_HEIGHT = 1080;
  public static final int SPLASH_SCREEN_REFERENCE_WIDTH = 900;
  public static final int SPLASH_SCREEN_REFERENCE_HEIGHT = 600;
  public static final int SPLASH_SCREEN_WINDOW_WIDTH = SPLASH_SCREEN_REFERENCE_WIDTH;
  public static final int SPLASH_SCREEN_WINDOW_HEIGHT = SPLASH_SCREEN_REFERENCE_HEIGHT;
  public static final boolean SPLASH_SCREEN_WINDOW_IS_FULLSCREEN = false;
  public static final boolean SPLASH_SCREEN_WINDOW_IS_DECORATED = false;
  private static final EnumSet <ScreenId> FILTERED_SCREEN_IDS = EnumSet.allOf (ScreenId.class);

  static
  {
    FILTERED_SCREEN_IDS.remove (ScreenId.NONE);
    FILTERED_SCREEN_IDS.remove (ScreenId.SPLASH);
    FILTERED_SCREEN_IDS.remove (ScreenId.MENU_TO_PLAY_LOADING);
    FILTERED_SCREEN_IDS.remove (ScreenId.PLAY_TO_MENU_LOADING);
  }

  public static final ImmutableSet <ScreenId> VALID_START_SCREENS = ImmutableSet.copyOf (FILTERED_SCREEN_IDS);
  public static ScreenId START_SCREEN = ScreenId.MAIN_MENU;

  public static boolean isValidStartScreen (final ScreenId screenId)
  {
    Arguments.checkIsNotNull (screenId, "screenId");

    return VALID_START_SCREENS.contains (screenId);
  }

  private ScreenSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
