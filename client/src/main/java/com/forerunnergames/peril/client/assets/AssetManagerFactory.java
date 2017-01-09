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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.client.io.CustomExternalFileHandleResolver;
import com.forerunnergames.peril.client.io.MultiAtlasSkinLoader;
import com.forerunnergames.peril.client.io.ShaderProgramLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class AssetManagerFactory
{
  public static AssetManager create (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    final FileHandleResolver externalResolver = new CustomExternalFileHandleResolver ();

    final com.badlogic.gdx.assets.AssetManager externalLibGdxAssetManager = new com.badlogic.gdx.assets.AssetManager (
            externalResolver);

    externalLibGdxAssetManager.setErrorListener (new AssetErrorListener ()
    {
      @Override
      @SuppressWarnings ("rawtypes")
      public void error (final AssetDescriptor asset, final Throwable throwable)
      {
        eventBus.publish (new AssetLoadingErrorEvent (asset, throwable));
      }
    });

    externalLibGdxAssetManager.setLoader (Skin.class, new MultiAtlasSkinLoader (externalResolver));
    externalLibGdxAssetManager.setLoader (ShaderProgram.class, new ShaderProgramLoader (externalResolver));

    final FileHandleResolver internalResolver = new InternalFileHandleResolver ();

    final com.badlogic.gdx.assets.AssetManager internalLibGdxAssetManager = new com.badlogic.gdx.assets.AssetManager (
            internalResolver);

    internalLibGdxAssetManager.setLoader (Skin.class, new MultiAtlasSkinLoader (internalResolver));
    internalLibGdxAssetManager.setLoader (ShaderProgram.class, new ShaderProgramLoader (internalResolver));

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
