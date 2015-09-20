package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.client.io.CustomExternalFileHandleResolver;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class AssetManagerFactory
{
  public static AssetManager create (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    final com.badlogic.gdx.assets.AssetManager externalLibGdxAssetManager = new com.badlogic.gdx.assets.AssetManager (
            new CustomExternalFileHandleResolver ());

    externalLibGdxAssetManager.setErrorListener (new AssetErrorListener ()
    {
      @Override
      @SuppressWarnings ("rawtypes")
      public void error (final AssetDescriptor asset, final Throwable throwable)
      {
        eventBus.publish (new AssetLoadingErrorEvent (asset, throwable));
      }
    });

    final com.badlogic.gdx.assets.AssetManager internalLibGdxAssetManager = new com.badlogic.gdx.assets.AssetManager (
            new InternalFileHandleResolver ());

    internalLibGdxAssetManager.setErrorListener (new AssetErrorListener ()
    {
      @Override
      @SuppressWarnings ("rawtypes")
      public void error (final AssetDescriptor asset, final Throwable throwable)
      {
        eventBus.publish (new AssetLoadingErrorEvent (asset, throwable));
      }
    });

    return new MultiSourceAssetManager (externalLibGdxAssetManager, internalLibGdxAssetManager);
  }
}
