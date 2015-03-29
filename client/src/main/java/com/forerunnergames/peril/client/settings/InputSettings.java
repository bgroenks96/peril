package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Translation2D;

public final class InputSettings
{
  // @formatter:off
  public static final Translation2D ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION = new Translation2D (0, -3);
  public static final Translation2D ACTUAL_SCREEN_SPACE_TO_ACTUAL_INPUT_SPACE_TRANSLATION = new Translation2D (0, 3);
  public static final Point2D       MENU_NORMAL_MOUSE_CURSOR_HOTSPOT                      = new Point2D (0, 0);
  public static final Point2D       PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT               = new Point2D (0, 0);
  // @formatter:on

  private InputSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
