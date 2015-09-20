package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.settings.AssetSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalAssetUpdater implements AssetUpdater
{
  private static final Logger log = LoggerFactory.getLogger (LocalAssetUpdater.class);

  LocalAssetUpdater ()
  {
  }

  @Override
  public void updateAssets ()
  {
    final FileHandle destAssetsDir = Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY);
    final FileHandle sourceAssetsDir = Gdx.files.absolute (AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION);

    if (AssetSettings.UPDATE_ASSETS)
    {
      log.info ("Attempting to update assets in [{}] from [{}]...", destAssetsDir.file (), sourceAssetsDir);

      try
      {
        log.info ("Removing old assets...");

        destAssetsDir.deleteDirectory ();

        log.info ("Copying new assets...");

        sourceAssetsDir.copyTo (destAssetsDir);

        log.info ("Successfully updated assets.");
      }
      catch (final GdxRuntimeException e)
      {
        throw new GdxRuntimeException ("Failed to update assets from: ["
                + AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION + "].\n" + "Make sure that "
                + ClientApplicationProperties.UPDATED_ASSETS_LOCATION_KEY + " is properly set in ["
                + ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME + "].\n" + "Also, "
                + ClientApplicationProperties.UPDATE_ASSETS_KEY
                + " must be set to true (in the same file) the first time you run the game.\n"
                + "If you already tried all of that, you can set " + ClientApplicationProperties.UPDATE_ASSETS_KEY
                + " to false.\nIn that case, you still need to make sure that you have a copy of all assets in "
                + destAssetsDir.file () + ".\n\n" + "Nerdy developer details:\n", e);
      }
    }
    else
    {
      log.warn ("Assets are not being updated.\nTo change this behavior, change {} in {} from false to true.\nMake sure to back up any customizations you made to any assets first, as your changes will be overwritten.",
                ClientApplicationProperties.UPDATE_ASSETS_KEY,
                ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME);
    }
  }

  @Override
  public boolean isFinished ()
  {
    return true;
  }

  @Override
  public float getProgressPercent ()
  {
    return 1.0f;
  }

  @Override
  public void shutDown ()
  {
  }
}
