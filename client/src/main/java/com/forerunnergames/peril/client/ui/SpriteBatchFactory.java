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

package com.forerunnergames.peril.client.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class SpriteBatchFactory
{
  public static SpriteBatch create (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    if (GraphicsSettings.USE_OPENGL_CORE_PROFILE)
    {
      return new SpriteBatch (GraphicsSettings.SPRITES_IN_BATCH,
              assetManager.get (AssetSettings.SPRITE_BATCH_SHADER_PROGRAM_ASSET_DESCRIPTOR));
    }
    else
    {
      return new SpriteBatch (GraphicsSettings.SPRITES_IN_BATCH);
    }
  }

  private SpriteBatchFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
