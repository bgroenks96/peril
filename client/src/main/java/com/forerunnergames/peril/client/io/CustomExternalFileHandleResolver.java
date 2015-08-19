package com.forerunnergames.peril.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import com.forerunnergames.peril.client.settings.AssetSettings;

public final class CustomExternalFileHandleResolver implements FileHandleResolver
{
  @Override
  public FileHandle resolve (final String fileName)
  {
    return Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + fileName);
  }
}
