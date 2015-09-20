package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiSourceAssetManager implements AssetManager
{
  private static final Logger log = LoggerFactory.getLogger (MultiSourceAssetManager.class);
  private final ImmutableSet <com.badlogic.gdx.assets.AssetManager> libGdxAssetManagers;
  private final Map <String, com.badlogic.gdx.assets.AssetManager> fileNamesToManagers = new HashMap <> ();

  public MultiSourceAssetManager (final com.badlogic.gdx.assets.AssetManager... libGdxAssetManagers)
  {
    Arguments.checkIsNotNullOrEmpty (libGdxAssetManagers, "libGdxAssetManagers");
    Arguments.checkHasNoNullElements (libGdxAssetManagers, "libGdxAssetManagers");

    this.libGdxAssetManagers = ImmutableSet.copyOf (libGdxAssetManagers);
  }

  @Override
  public synchronized <T> T get (final AssetDescriptor <T> descriptor)
  {
    Arguments.checkIsNotNull (descriptor, "descriptor");

    return get (descriptor.fileName, descriptor.type);
  }

  @Override
  public synchronized <T> T get (final String fileName, final Class <T> type)
  {
    Arguments.checkIsNotNull (fileName, "fileName");
    Arguments.checkIsNotNull (type, "type");

    final com.badlogic.gdx.assets.AssetManager manager = fileNamesToManagers.get (fileName);

    if (manager == null)
    {
      throw new IllegalStateException (Strings.format ("Cannot get asset [{}] because it was not loaded first by {}.",
                                                       fileName, MultiSourceAssetManager.class.getSimpleName ()));
    }

    return manager.get (fileName, type);
  }

  @Override
  public synchronized void load (final AssetDescriptor <?> descriptor)
  {
    Arguments.checkIsNotNull (descriptor, "descriptor");

    load (descriptor.fileName, descriptor.type, descriptor.params);
  }

  @Override
  public synchronized <T> void load (final String fileName, final Class <T> type)
  {
    load (fileName, type, null);
  }

  @Override
  public synchronized <T> void load (final String fileName,
                                     final Class <T> type,
                                     @Nullable final AssetLoaderParameters <T> parameters)
  {
    Arguments.checkIsNotNull (fileName, "fileName");
    Arguments.checkIsNotNull (type, "type");

    for (final com.badlogic.gdx.assets.AssetManager assetManager : libGdxAssetManagers)
    {
      String resolvedPathDescription = "?";

      try
      {
        final AssetLoader loader = assetManager.getLoader (type);

        if (loader == null) continue;

        final FileHandle fileHandle = loader.resolve (fileName);
        final File file = fileHandle.file ();

        if (fileHandle.type () == Files.FileType.Internal && !file.exists ())
        {
          resolvedPathDescription = "classpath:/" + fileHandle.path ();
        }
        else
        {
          resolvedPathDescription = file.getAbsolutePath ();
        }

        log.debug ("Queuing asset [{}] for loading as [{}]...", fileName, resolvedPathDescription);

        if (!fileHandle.exists ())
        {
          log.debug ("Failed (file doesn't exist).");
          continue;
        }

        assetManager.load (fileName, type, parameters);
        fileNamesToManagers.put (fileName, assetManager);

        log.debug ("Success.");

        return;
      }
      catch (final GdxRuntimeException e)
      {
        log.debug ("Cannot queue asset [{}] for loading as [{}]. Reason:\n\n{}", fileName, resolvedPathDescription,
                   Throwables.getStackTraceAsString (e));
      }
    }

    throw new RuntimeException (Strings.format ("Failed to queue asset [{}] for loading.", fileName));
  }

  @Override
  public synchronized void update ()
  {
    for (final com.badlogic.gdx.assets.AssetManager assetManager : libGdxAssetManagers)
    {
      assetManager.update ();
    }
  }

  @Override
  public synchronized float getProgressLoading ()
  {
    float averageDividend = 0.0f;
    final int averageDivisor = libGdxAssetManagers.size ();

    for (final com.badlogic.gdx.assets.AssetManager assetManager : libGdxAssetManagers)
    {
      averageDividend += assetManager.getProgress ();
    }

    return averageDividend / averageDivisor;
  }

  @Override
  public synchronized void finishLoading (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    final com.badlogic.gdx.assets.AssetManager manager = fileNamesToManagers.get (fileName);

    if (manager == null)
    {
      throw new IllegalStateException (
              Strings.format ("Cannot finish loading asset [{}] because it was not first loaded by {}.", fileName,
                              MultiSourceAssetManager.class.getSimpleName ()));
    }

    log.debug ("Finishing loading asset [{}]...", fileName);

    manager.finishLoadingAsset (fileName);

    log.debug ("Success.");
  }

  @Override
  public synchronized boolean isLoaded (final AssetDescriptor <?> descriptor)
  {
    Arguments.checkIsNotNull (descriptor, "descriptor");

    return isLoaded (descriptor.fileName, descriptor.type);
  }

  @Override
  public synchronized boolean isLoaded (final String fileName, final Class <?> type)
  {
    Arguments.checkIsNotNull (fileName, "fileName");
    Arguments.checkIsNotNull (type, "type");

    final com.badlogic.gdx.assets.AssetManager manager = fileNamesToManagers.get (fileName);

    return manager != null && manager.isLoaded (fileName, type);
  }

  @Override
  public synchronized boolean isLoaded (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    final com.badlogic.gdx.assets.AssetManager manager = fileNamesToManagers.get (fileName);

    return manager != null && manager.isLoaded (fileName);
  }

  @Override
  public synchronized void unload (final AssetDescriptor <?> descriptor)
  {
    Arguments.checkIsNotNull (descriptor, "descriptor");

    unload (descriptor.fileName);
  }

  @Override
  public synchronized void unload (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    final com.badlogic.gdx.assets.AssetManager manager = fileNamesToManagers.get (fileName);

    if (manager == null)
    {
      throw new IllegalStateException (
              Strings.format ("Cannot unload asset [{}] because it was not first loaded by {}.", fileName,
                              MultiSourceAssetManager.class.getSimpleName ()));
    }

    log.debug ("Unloading asset [{}]...", fileName);

    manager.unload (fileName);

    if (!manager.isLoaded (fileName)) fileNamesToManagers.remove (fileName);
  }

  @Override
  public synchronized void finishLoading (final AssetDescriptor <?> descriptor)
  {
    Arguments.checkIsNotNull (descriptor, "descriptor");

    finishLoading (descriptor.fileName);
  }

  @Override
  public synchronized void dispose ()
  {
    for (final com.badlogic.gdx.assets.AssetManager assetManager : libGdxAssetManagers)
    {
      assetManager.dispose ();
    }

    fileNamesToManagers.clear ();
  }
}
