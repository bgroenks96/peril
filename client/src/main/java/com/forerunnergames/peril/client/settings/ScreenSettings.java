package com.forerunnergames.peril.client.settings;

import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Classes;

public final class ScreenSettings
{
  // @formatter:off
  public static final ScreenId START_SCREEN = ScreenId.MAIN_MENU;
  // @formatter:on

  private ScreenSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
