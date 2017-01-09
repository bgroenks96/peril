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

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

public final class AssetController extends ControllerAdapter
{
  private final AssetManager assetManager;

  public AssetController (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public void initialize ()
  {
    for (final AssetDescriptor <?> descriptor : AssetSettings.LOAD_BEFORE_SPLASH_SCREEN_ASSET_DESCRIPTORS)
    {
      assetManager.load (descriptor);
      assetManager.finishLoading (descriptor);
    }
  }

  @Override
  public void update ()
  {
    assetManager.update ();
  }

  @Override
  public void shutDown ()
  {
    assetManager.dispose ();
  }
}
