package com.forerunnergames.peril.core.model.settings;

import com.forerunnergames.tools.common.Classes;

public final class AssetSettings
{
  public static final String ABSOLUTE_EXTERNAL_ASSETS_DIRECTORY = System.getProperty ("user.home") + "/peril/assets/";

  public static final String ABSOLUTE_EXTERNAL_CLASSIC_MODE_DIRECTORY = ABSOLUTE_EXTERNAL_ASSETS_DIRECTORY
          + "screens/game/play/modes/classic/";

  public static final String ABSOLUTE_EXTERNAL_CLASSIC_MODE_MAPS_DIRECTORY = ABSOLUTE_EXTERNAL_CLASSIC_MODE_DIRECTORY
          + "maps/";

  public static final String ABSOLUTE_EXTERNAL_CLASSIC_MODE_CLASSIC_MAP_DIRECTORY = ABSOLUTE_EXTERNAL_CLASSIC_MODE_MAPS_DIRECTORY
          + "classic/";

  public static final String ABSOLUTE_EXTERNAL_CLASSIC_MODE_CLASSIC_MAP_COUNTRY_DATA_DIRECTORY = ABSOLUTE_EXTERNAL_CLASSIC_MODE_CLASSIC_MAP_DIRECTORY
          + "countries/data/";

  public static final String ABSOLUTE_EXTERNAL_CLASSIC_MODE_CLASSIC_MAP_CONTINENT_DATA_DIRECTORY = ABSOLUTE_EXTERNAL_CLASSIC_MODE_CLASSIC_MAP_DIRECTORY
          + "continents/data/";

  public static final String ABSOLUTE_EXTERNAL_CLASSIC_MODE_CARD_DATA_DIRECTORY = ABSOLUTE_EXTERNAL_CLASSIC_MODE_DIRECTORY
          + "cards/data/";

  public static final String COUNTRY_DATA_FILENAME = "countries.txt";
  public static final String CONTINENT_DATA_FILENAME = "continents.txt";

  private AssetSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
