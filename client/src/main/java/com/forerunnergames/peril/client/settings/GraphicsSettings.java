package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;

public final class GraphicsSettings
{
  // @formatter:off
  public static final int     REFERENCE_RESOLUTION_WIDTH  = 1920;
  public static final int     REFERENCE_RESOLUTION_HEIGHT = 1080;
  public static final boolean IS_FULLSCREEN               = false;
  public static final boolean IS_VSYNC_ENABLED            = true;
  public static final boolean IS_WINDOW_RESIZABLE         = true;
  public static final String  WINDOW_TITLE                = "Peril";
  // @formatter:on

  private GraphicsSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
