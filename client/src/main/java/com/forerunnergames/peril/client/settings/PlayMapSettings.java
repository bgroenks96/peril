package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.geometry.Size2D;
import com.forerunnergames.tools.common.geometry.Translation2D;

// @formatter:off
public final class PlayMapSettings
{
  public static final int           REFERENCE_WIDTH                                        = 1704;
  public static final int           REFERENCE_HEIGHT                                       = 804;
  public static final Size2D        REFERENCE_SIZE                                         = new Size2D (REFERENCE_WIDTH, REFERENCE_HEIGHT);
  public static final int           REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_X = 14;
  public static final int           REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_Y = 14;
  public static final Translation2D REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION   = new Translation2D (REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_X, REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_Y);
  public static final int           REFERENCE_SCREEN_SPACE_TO_PLAY_MAP_SPACE_TRANSLATION_X = -REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_X;
  public static final int           REFERENCE_SCREEN_SPACE_TO_PLAY_MAP_SPACE_TRANSLATION_Y = -REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_Y;
  public static final Translation2D REFERENCE_SCREEN_SPACE_TO_PLAY_MAP_SPACE_TRANSLATION   = new Translation2D (REFERENCE_SCREEN_SPACE_TO_PLAY_MAP_SPACE_TRANSLATION_X, REFERENCE_SCREEN_SPACE_TO_PLAY_MAP_SPACE_TRANSLATION_Y);
  public static final boolean       ENABLE_HOVER_EFFECTS                                   = false;
  public static final boolean       ENABLE_CLICK_EFFECTS                                   = false;

  private PlayMapSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
