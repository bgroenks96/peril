package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;

public final class AssetSettings
{
  // External assets directory is relative to:
  //   - the home directory of the current user on desktop (Windows / OS X / Linux)
  //   - the SD card root on mobile (Android / iOS)
  // and is accessed via Gdx.files.external
  public static final String RELATIVE_EXTERNAL_ASSETS_DIRECTORY = "peril/assets";
  public static String ABSOLUTE_UPDATED_ASSETS_DIRECTORY = "";
  public static boolean UPDATE_ASSETS = true;

  private AssetSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
