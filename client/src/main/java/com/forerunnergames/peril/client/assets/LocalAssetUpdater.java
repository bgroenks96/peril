/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.settings.AssetSettings;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalAssetUpdater implements AssetUpdater
{
  private static final Logger log = LoggerFactory.getLogger (LocalAssetUpdater.class);
  private final ExecutorService executorService;
  @Nullable
  private Future <?> assetUpdatingFuture;

  LocalAssetUpdater ()
  {
    executorService = Executors.newSingleThreadExecutor ();
  }

  @Override
  public void updateAssets ()
  {
    if (!AssetSettings.UPDATE_ASSETS)
    {
      log.warn ("Assets are not being updated.\nTo change this behavior, change {} in {} from false to true.\n"
                        + "Make sure to back up any customizations you made to any assets first, as your changes "
                        + "will be overwritten.", ClientApplicationProperties.UPDATE_ASSETS_KEY,
                ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME);
      return;
    }

    assetUpdatingFuture = executorService.submit (new Runnable ()
    {
      @Override
      public void run ()
      {
        final FileHandle destAssetsDir = Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY);
        FileInputStream fileInputStream = null;
        FileLock lock = null;

        try
        {
          final FileHandle sourceAssetsDir = Gdx.files.absolute (AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION);

          log.info ("Attempting to update assets in [{}] from [{}]...", destAssetsDir.file (), sourceAssetsDir);

          fileInputStream = new FileInputStream (destAssetsDir.file ());
          lock = fileInputStream.getChannel ().lock ();

          if (Thread.currentThread ().isInterrupted ())
          {
            log.warn ("Asset updating was cancelled before beginning.");
            return;
          }

          log.info ("Removing old assets...");

          destAssetsDir.deleteDirectory ();

          if (Thread.currentThread ().isInterrupted ())
          {
            log.warn ("Asset updating was cancelled after removing old assets, but before copying new assets.");
            return;
          }

          log.info ("Copying new assets...");

          sourceAssetsDir.copyTo (destAssetsDir);

          log.info ("Successfully updated assets.");
        }
        catch (final Exception e)
        {
          throw new RuntimeException ("Failed to update assets from: ["
                  + AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION + "].\n" + "Make sure that "
                  + ClientApplicationProperties.UPDATED_ASSETS_LOCATION_KEY + " is properly set in ["
                  + ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME + "].\n" + "Also, "
                  + ClientApplicationProperties.UPDATE_ASSETS_KEY
                  + " must be set to true (in the same file) the first time you run the game.\n"
                  + "If you already tried all of that, you can set " + ClientApplicationProperties.UPDATE_ASSETS_KEY
                  + " to false.\nIn that case, you still need to make sure that you have a copy of all assets in "
                  + destAssetsDir.file () + ".\n\n" + "Nerdy developer details:\n", e);
        }
        finally
        {
          if (lock != null) try
          {
            lock.release ();
          }
          catch (final IOException e)
          {
            log.error ("Could not release lock on: [{}].", destAssetsDir.file (), e);
          }

          if (fileInputStream != null) try
          {
            fileInputStream.close ();
          }
          catch (final IOException e)
          {
            log.error ("Could not close {} on: [{}].", FileInputStream.class.getSimpleName (), destAssetsDir.file (), e);
          }
        }
      }
    });
  }

  @Override
  public boolean isFinished ()
  {
    if (assetUpdatingFuture == null) return !AssetSettings.UPDATE_ASSETS;

    return assetUpdatingFuture.isDone ();
  }

  @Override
  public float getProgressPercent ()
  {
    if (assetUpdatingFuture == null) return AssetSettings.UPDATE_ASSETS ? 0.0f : 1.0f;

    return assetUpdatingFuture.isDone () ? 1.0f : 0.0f;
  }

  @Override
  public void shutDown ()
  {
    if (assetUpdatingFuture != null && !assetUpdatingFuture.isDone ()) assetUpdatingFuture.cancel (true);

    executorService.shutdown ();
  }
}
