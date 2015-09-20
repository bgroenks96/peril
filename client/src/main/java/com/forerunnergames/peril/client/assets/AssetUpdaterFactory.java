package com.forerunnergames.peril.client.assets;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AssetUpdaterFactory
{
  public static AssetUpdater create ()
  {
    if (AssetSettings.isValidS3BucketPath (AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION))
    {
      return new S3AssetUpdater (AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION);
    }

    final Path path;

    try
    {
      path = Paths.get (AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION);
    }
    catch (final InvalidPathException e)
    {
      throw new RuntimeException (
              Strings.format ("Absolute updated assets directory [{}] is not a valid filesystem path, nor a valid Amazon S3 bucket path.\n\n{}",
                              AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION, Throwables.getStackTraceAsString (e)));
    }

    if (!Files.isDirectory (path))
    {
      throw new RuntimeException (
              Strings.format ("Absolute updated assets directory [{}] is not a valid filesystem directory, nor a valid Amazon S3 bucket path.",
                              AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION));
    }

    return new LocalAssetUpdater ();
  }

  private AssetUpdaterFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
