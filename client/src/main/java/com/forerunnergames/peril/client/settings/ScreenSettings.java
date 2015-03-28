package com.forerunnergames.peril.client.settings;

import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Classes;

public final class ScreenSettings
{
  // @formatter:off
  public static ScreenId START_SCREEN = ScreenId.PLAY;
  // @formatter:on

  private ScreenSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
