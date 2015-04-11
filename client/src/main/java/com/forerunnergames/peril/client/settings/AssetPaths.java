package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;

// @formatter:off
public final class AssetPaths
{
  // Asset paths are relative to the root of the JAR
  // All assets are copied automatically into the root of the JAR from the peril/android/assets/ directory.
  public static final String ROOT_ASSETS_PATH = "";

  // Play map asset paths
  public static final String PLAY_MAP_PATH                             = ROOT_ASSETS_PATH             + "screens/game/play/map/";
  public static final String PLAY_MAP_COUNTRIES_PATH                   = PLAY_MAP_PATH                + "countries/";
  public static final String PLAY_MAP_CONTINENTS_PATH                  = PLAY_MAP_PATH                + "continents/";
  public static final String PLAY_MAP_COUNTRY_DATA_PATH                = PLAY_MAP_COUNTRIES_PATH      + "data/";
  public static final String PLAY_MAP_CONTINENT_DATA_PATH              = PLAY_MAP_CONTINENTS_PATH     + "data/";
  public static final String PLAY_MAP_COUNTRY_NAME_TO_COLOR_FILENAME   = PLAY_MAP_COUNTRY_DATA_PATH   + "inputDetection.txt";
  public static final String PLAY_MAP_CONTINENT_NAME_TO_COLOR_FILENAME = PLAY_MAP_CONTINENT_DATA_PATH + "inputDetection.txt";
  public static final String PLAY_MAP_COUNTRY_IMAGE_DATA_FILENAME      = PLAY_MAP_COUNTRY_DATA_PATH   + "imageData.txt";

  private AssetPaths()
  {
    Classes.instantiationNotAllowed();
  }
}
