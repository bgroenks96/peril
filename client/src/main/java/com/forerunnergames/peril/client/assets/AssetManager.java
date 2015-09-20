package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;

public interface AssetManager
{
  <T> T get (final AssetDescriptor <T> descriptor);

  <T> T get (final String fileName, final Class <T> type);

  void load (final AssetDescriptor <?> descriptor);

  <T> void load (final String fileName, final Class <T> type);

  <T> void load (final String fileName, final Class <T> type, final AssetLoaderParameters <T> parameters);

  void update ();

  float getProgressLoading ();

  void finishLoading (final String fileName);

  boolean isLoaded (final AssetDescriptor <?> descriptor);

  boolean isLoaded (final String fileName, final Class <?> type);

  boolean isLoaded (final String fileName);

  void unload (final AssetDescriptor <?> descriptor);

  void unload (final String fileName);

  void finishLoading (final AssetDescriptor<?> descriptor);

  void dispose ();
}
