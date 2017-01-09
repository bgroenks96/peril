/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class S3AssetUpdater implements AssetUpdater
{
  private static final Logger log = LoggerFactory.getLogger (S3AssetUpdater.class);
  private final ExecutorService executorService;
  private final String bucketName;
  private final TransferManager transferManager;
  @Nullable
  private Future <?> assetUpdatingFuture;
  @Nullable
  private volatile MultipleFileDownload downloadInProgress;

  S3AssetUpdater (final String bucketPath)
  {
    Arguments.checkIsNotNull (bucketPath, "bucketPath");
    Preconditions.checkIsTrue (AssetSettings.isValidS3BucketPath (bucketPath),
                               AssetSettings.VALID_S3_BUCKET_PATH_DESCRIPTION);

    bucketName = AssetSettings.getS3BucketName (bucketPath);
    executorService = Executors.newSingleThreadExecutor ();
    final ClientConfiguration clientConfig = new ClientConfiguration ().withMaxErrorRetry (10)
            .withConnectionTimeout (10_000).withSocketTimeout (10_000).withTcpKeepAlive (true);
    final AmazonS3 s3 = new AmazonS3Client (new ProfileCredentialsProvider (), clientConfig);
    transferManager = new TransferManager (s3);
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

        try
        {
          log.info ("Attempting to update assets in [{}] from [{}]...", destAssetsDir.file (),
                    AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION);

          if (Thread.currentThread ().isInterrupted ())
          {
            log.warn ("Asset updating was cancelled before beginning.");
            return;
          }

          log.info ("Removing old assets...");

          destAssetsDir.deleteDirectory ();

          if (Thread.currentThread ().isInterrupted ())
          {
            log.warn ("Asset updating was cancelled after removing old assets, but before downloading new assets.");
            return;
          }

          final File destinationDirectory = destAssetsDir.file ();

          log.info ("Downloading new assets to [{}]...", destinationDirectory);

          downloadInProgress = transferManager.downloadDirectory (bucketName,
                                                                  AssetSettings.INITIAL_S3_ASSETS_DOWNLOAD_SUBDIRECTORY,
                                                                  destinationDirectory);

          log.info ("Successfully updated assets.");
        }
        catch (final Exception e)
        {
          final String errorMessage = "Failed to update assets from: [" + bucketName + "].\n" + "Make sure that "
                  + ClientApplicationProperties.UPDATED_ASSETS_LOCATION_KEY + " is properly set in ["
                  + ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME + "].\n" + "Also, "
                  + ClientApplicationProperties.UPDATE_ASSETS_KEY
                  + " must be set to true (in the same file) the first time you run the game.\n"
                  + "If you already tried all of that, you can set " + ClientApplicationProperties.UPDATE_ASSETS_KEY
                  + " to false.\nIn that case, you still need to make sure that you have a copy of all assets in "
                  + destAssetsDir.file () + ".\n\n" + "Nerdy developer details:\n";

          log.error (errorMessage, e);

          throw new RuntimeException (errorMessage, e);
        }
      }
    });
  }

  @Override
  public boolean isFinished ()
  {
    if (downloadInProgress == null) return !AssetSettings.UPDATE_ASSETS;

    return downloadInProgress.getState () == Transfer.TransferState.Completed;
  }

  @Override
  public float getProgressPercent ()
  {
    if (downloadInProgress == null) return AssetSettings.UPDATE_ASSETS ? 0.0f : 1.0f;

    return (float) (downloadInProgress.getProgress ().getPercentTransferred () / 100.0);
  }

  @Override
  public void shutDown ()
  {
    if (assetUpdatingFuture != null && !assetUpdatingFuture.isDone ()) assetUpdatingFuture.cancel (true);

    if (downloadInProgress != null && (downloadInProgress.getState () == Transfer.TransferState.InProgress
            || downloadInProgress.getState () == Transfer.TransferState.Waiting))
    {
      try
      {
        log.warn ("Aborting download in progress.");
        downloadInProgress.abort ();
      }
      catch (final IOException e)
      {
        log.error ("There was a problem aborting a download in progress.", e);
      }
    }

    transferManager.shutdownNow ();
    executorService.shutdown ();
  }
}
