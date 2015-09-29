package com.forerunnergames.peril.client.settings;

import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public final class ScreenSettings
{
  private static final EnumSet <ScreenId> FILTERED_SCREEN_IDS = EnumSet.allOf (ScreenId.class);
  public static final boolean SPLASH_SCREEN_IS_RESIZABLE = false;

  static
  {
    FILTERED_SCREEN_IDS.remove (ScreenId.SPLASH);
    FILTERED_SCREEN_IDS.remove (ScreenId.LOADING);
  }

  public static final ImmutableSet <ScreenId> VALID_START_SCREENS = ImmutableSet.copyOf (FILTERED_SCREEN_IDS);
  public static final int SPLASH_SCREEN_WINDOW_WIDTH = 900;
  public static final int SPLASH_SCREEN_WINDOW_HEIGHT = 600;
  public static final boolean SPLASH_SCREEN_WINDOW_IS_FULLSCREEN = false;
  public static final boolean SPLASH_SCREEN_WINDOW_IS_DECORATED = false;
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
