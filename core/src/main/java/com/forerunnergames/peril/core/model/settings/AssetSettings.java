package com.forerunnergames.peril.core.model.settings;

import com.forerunnergames.tools.common.Classes;

public final class AssetSettings
{
  public static final String COUNTRY_DATA_FILENAME = "countries.txt";
  public static final String CONTINENT_DATA_FILENAME = "continents.txt";

  private AssetSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
