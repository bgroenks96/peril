package com.forerunnergames.peril.client.settings;

import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Classes;

public final class ScreenSettings
{
  public static final ScreenId START_SCREEN = ScreenId.MAIN_MENU;

  private ScreenSettings()
  {
    Classes.instantiationNotAllowed();
  }
}
