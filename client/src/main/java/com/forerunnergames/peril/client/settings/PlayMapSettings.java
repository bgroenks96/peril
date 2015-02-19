package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Scaling2D;
import com.forerunnergames.tools.common.geometry.Size2D;
import com.forerunnergames.tools.common.geometry.Translation2D;

// @formatter:off
public final class PlayMapSettings
{
  public static final int           REFERENCE_WIDTH                                               = 2048;
  public static final int           REFERENCE_HEIGHT                                              = 1024;
  public static final Size2D        REFERENCE_SIZE                                                = new Size2D (REFERENCE_WIDTH, REFERENCE_HEIGHT);
  public static final int           ACTUAL_WIDTH                                                  = 1800;
  public static final int           ACTUAL_HEIGHT                                                 = 788;
  public static final Size2D        ACTUAL_SIZE                                                   = new Size2D (ACTUAL_WIDTH, ACTUAL_HEIGHT);
  public static final Translation2D ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_SCREEN_SPACE_TRANSLATION   = new Translation2D (12, 12);
  public static final Translation2D REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION   = new Translation2D (-12, -12);
  public static final Scaling2D     ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_PLAY_MAP_SPACE_SCALING     = Geometry.divide (REFERENCE_SIZE, ACTUAL_SIZE);
  public static final Scaling2D     REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING     = Geometry.divide (ACTUAL_SIZE, REFERENCE_SIZE);
  public static final boolean       ENABLE_HOVER_EFFECTS                                          = false;
  public static final boolean       ENABLE_CLICK_EFFECTS                                          = false;

  private PlayMapSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
