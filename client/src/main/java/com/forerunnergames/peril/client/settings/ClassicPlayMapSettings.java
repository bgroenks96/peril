package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Classes;

public final class ClassicPlayMapSettings
{
  // @formatter:off
  public static final float     REFERENCE_WIDTH                                             = 2048;
  public static final float     REFERENCE_HEIGHT                                            = 1024;
  public static final float     ACTUAL_WIDTH                                                = 1800;
  public static final float     ACTUAL_HEIGHT                                               = 788;
  public static final Vector2   REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION = new Vector2 (-12, -12);
  public static final Vector2   ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_PLAY_MAP_SPACE_SCALING   = new Vector2 (REFERENCE_WIDTH / ACTUAL_WIDTH, REFERENCE_HEIGHT / ACTUAL_HEIGHT);
  public static final Vector2   REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING   = new Vector2 (ACTUAL_WIDTH / REFERENCE_WIDTH, ACTUAL_HEIGHT / REFERENCE_HEIGHT);
  public static final Vector2   COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE           = new Vector2 (32, 30);
  public static final Vector2   COUNTRY_ARMY_CIRCLE_SIZE_ACTUAL_PLAY_MAP_SPACE              = new Vector2 (COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE).scl (REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
  public static final boolean   ENABLE_HOVER_EFFECTS                                        = true;
  public static final boolean   ENABLE_CLICK_EFFECTS                                        = false;

  // Asset paths
  private static final String CLASSIC_PLAY_MAP_ASSETS_PATH = "screens/game/play/modes/classic/maps/classic/";
  private static final String CONTINENTS_ASSETS_PATH = CLASSIC_PLAY_MAP_ASSETS_PATH + "continents/";
  private static final String CONTINENT_DATA_ASSETS_PATH = CONTINENTS_ASSETS_PATH + "data/";
  private static final String COUNTRY_ASSETS_PATH = CLASSIC_PLAY_MAP_ASSETS_PATH + "countries/";
  private static final String COUNTRY_DATA_ASSETS_PATH = COUNTRY_ASSETS_PATH + "data/";

  // Asset filenames
  public static final String CONTINENT_NAME_TO_COLOR_FILENAME = CONTINENT_DATA_ASSETS_PATH + "inputDetection.txt";
  public static final String COUNTRY_IMAGE_DATA_FILENAME = COUNTRY_DATA_ASSETS_PATH + "imageData.txt";
  public static final String COUNTRY_NAME_TO_COLOR_FILENAME = COUNTRY_DATA_ASSETS_PATH + "inputDetection.txt";
  // @formatter:on

  private ClassicPlayMapSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
