package com.forerunnergames.peril.client.settings;

import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public final class ScreenSettings
{
  private static final EnumSet <ScreenId> FILTERED_SCREEN_IDS = EnumSet.allOf (ScreenId.class);

  static
  {
    FILTERED_SCREEN_IDS.remove (ScreenId.LOADING_INITIAL);
    FILTERED_SCREEN_IDS.remove (ScreenId.LOADING);
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
