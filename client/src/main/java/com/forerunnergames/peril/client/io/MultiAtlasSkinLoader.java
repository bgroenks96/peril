package com.forerunnergames.peril.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableList;

public final class MultiAtlasSkinLoader extends AsynchronousAssetLoader <Skin, MultiAtlasSkinLoader.SkinParameter>
{
  private Skin skin;

  public MultiAtlasSkinLoader (final FileHandleResolver resolver)
  {
    super (resolver);
  }

  @Override
  public void loadAsync (final AssetManager manager,
                         final String fileName,
                         final FileHandle file,
                         final SkinParameter parameter)
  {
    skin = new Skin ();

    for (final AssetDescriptor <TextureAtlas> descriptor : parameter.getTextureAtlasAssetDescriptors ())
    {
      skin.addRegions (manager.get (descriptor));
    }
  }

  @Override
  public Skin loadSync (final AssetManager manager,
                        final String fileName,
                        final FileHandle file,
                        final SkinParameter parameter)
  {
    Arguments.checkIsNotNull (manager, "manager");
    Arguments.checkIsNotNull (fileName, "fileName");
    Arguments.checkIsNotNull (file, "file");
    Arguments.checkIsNotNull (parameter, "parameter");

    skin.load (file);

    return skin;
  }

  @Override
  @SuppressWarnings ("rawtypes")
  public Array <AssetDescriptor> getDependencies (final String fileName,
                                                  final FileHandle file,
                                                  final SkinParameter parameter)
  {
    Arguments.checkIsNotNull (fileName, "fileName");
    Arguments.checkIsNotNull (file, "file");

    final Array <AssetDescriptor> dependencies = new Array <> ();

    for (final AssetDescriptor <TextureAtlas> descriptor : parameter.getTextureAtlasAssetDescriptors ())
    {
      dependencies.add (descriptor);
    }

    return dependencies;
  }

  public static final class SkinParameter extends AssetLoaderParameters <Skin>
  {
    private final ImmutableList <AssetDescriptor <TextureAtlas>> textureAtlasAssetDescriptors;

    @SafeVarargs
    public SkinParameter (final AssetDescriptor <TextureAtlas>... textureAtlasAssetDescriptors)
    {
      Arguments.checkIsNotNullOrEmpty (textureAtlasAssetDescriptors, "textureAtlasAssetDescriptors");
      Arguments.checkHasNoNullElements (textureAtlasAssetDescriptors, "textureAtlasAssetDescriptors");

      this.textureAtlasAssetDescriptors = ImmutableList.copyOf (textureAtlasAssetDescriptors);
    }

    public ImmutableList <AssetDescriptor <TextureAtlas>> getTextureAtlasAssetDescriptors ()
    {
      return textureAtlasAssetDescriptors;
    }
  }
}
