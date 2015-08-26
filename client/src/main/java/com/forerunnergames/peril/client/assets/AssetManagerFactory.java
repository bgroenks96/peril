package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;

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

    final AssetManager assetManager = new AssetManager (new CustomExternalFileHandleResolver ());

    assetManager.setErrorListener (new AssetErrorListener ()
    {
      @Override
      public void error (final AssetDescriptor asset, final Throwable throwable)
      {
        eventBus.publish (new AssetLoadingErrorEvent (asset, throwable));
      }
    });

    return assetManager;
  }
}
