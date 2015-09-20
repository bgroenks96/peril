package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Array;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class S3AssetUtils
{
  private static final Logger log = LoggerFactory.getLogger (S3AssetUtils.class);

  public static ImmutableList <S3AssetMetadata> getAssetMetadatas (final AssetDescriptor <?> descriptor,
                                                                   final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (descriptor, "descriptor");
    Arguments.checkIsNotNull (assetManager, "assetManager");

    final ImmutableList.Builder <S3AssetMetadata> metaDatasBuilder = ImmutableList.builder ();

    metaDatasBuilder.add (new DefaultS3AssetMetadata (getAssetKey (descriptor.fileName),
            getAssetFile (descriptor, assetManager)));

    for (final String childAssetFileName : getChildAssetFileNames (descriptor.fileName, assetManager))
    {
      metaDatasBuilder.add (new DefaultS3AssetMetadata (getAssetKey (childAssetFileName),
              getAssetFile (childAssetFileName, assetManager)));
    }

    return metaDatasBuilder.build ();
  }

  public static ImmutableList <String> getAssetKeys (final AssetDescriptor <?> descriptor,
                                                     final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (descriptor, "descriptor");
    Arguments.checkIsNotNull (assetManager, "assetManager");

    final ImmutableList.Builder <String> keysBuilder = ImmutableList.builder ();

    keysBuilder.add (getAssetKey (descriptor.fileName));

    for (final String childAssetFileName : getChildAssetFileNames (descriptor.fileName, assetManager))
    {
      keysBuilder.add (getAssetKey (childAssetFileName));
    }

    return keysBuilder.build ();
  }

  public static File getAssetFile (final String assetKey, final FileHandleResolver resolver)
  {
    Arguments.checkIsNotNull (assetKey, "assetKey");
    Arguments.checkIsNotNull (resolver, "resolver");

    return resolver.resolve (assetKey).file ();
  }

  private static File getAssetFile (final AssetDescriptor <?> descriptor, final AssetManager assetManager)
  {
    if (descriptor.file != null) return descriptor.file.file ();

    return assetManager.getLoader (descriptor.type, descriptor.fileName).resolve (descriptor.fileName).file ();
  }

  private static File getAssetFile (final String fileName, final AssetManager assetManager)
  {
    return assetManager.getLoader (assetManager.getAssetType (fileName), fileName).resolve (fileName).file ();
  }

  private static ImmutableCollection <String> getChildAssetFileNames (final String parentAssetFileName,
                                                                      final AssetManager assetManager)
  {
    final ImmutableList.Builder <String> childrenBuilder = ImmutableList.builder ();
    final Array <String> children = assetManager.getDependencies (parentAssetFileName);

    if (children == null) return childrenBuilder.build ();

    childrenBuilder.addAll (children);

    for (final String child : children)
    {
      childrenBuilder.addAll (getChildAssetFileNames (child, assetManager));
    }

    return childrenBuilder.build ();
  }

  private static String getAssetKey (final String fileName)
  {
    String userDir = "";

    try
    {
      userDir = System.getProperty ("user.dir");
    }
    catch (final SecurityException ignored)
    {
      log.warn ("Could not resolve system property \"user.dir\" to strip from fileName [{}] to create key.", fileName);
    }

    String userHome = "";

    try
    {
      userHome = System.getProperty ("user.home");
    }
    catch (final SecurityException ignored)
    {
      log.warn ("Could not resolve system property \"user.home\" to strip from fileName [{}] to create key.", fileName);
    }

    return fileName.replaceAll (userDir, "").replaceFirst (userHome, "")
            .replaceAll (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY, "");
  }

  private S3AssetUtils ()
  {
    Classes.instantiationNotAllowed ();
  }
}
