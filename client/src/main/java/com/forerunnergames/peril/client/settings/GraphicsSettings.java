/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Scaling;

import com.forerunnergames.tools.common.Classes;

public final class GraphicsSettings
{
  public static final int MIN_INITIAL_WINDOW_WIDTH = 640;
  public static final int MIN_INITIAL_WINDOW_HEIGHT = 480;
  public static final boolean TEXTURE_MIPMAPPING = true;
  public static final Texture.TextureFilter TEXTURE_MINIFICATION_FILTER = Texture.TextureFilter.MipMapLinearNearest;
  public static final Texture.TextureFilter TEXTURE_MAGNIFICATION_FILTER = Texture.TextureFilter.Nearest;
  public static final Texture.TextureFilter FONT_TEXTURE_MINIFICATION_FILTER = Texture.TextureFilter.MipMapLinearLinear;
  public static final Texture.TextureFilter FONT_TEXTURE_MAGNIFICATION_FILTER = Texture.TextureFilter.Linear;
  public static final boolean FONT_TEXTURE_MIPMAPPING = true;
  public static final Scaling VIEWPORT_SCALING = Scaling.stretch;
  public static final int SPRITES_IN_BATCH = 5460;
  public static int INITIAL_WINDOW_WIDTH = ScreenSettings.REFERENCE_SCREEN_WIDTH;
  public static int INITIAL_WINDOW_HEIGHT = ScreenSettings.REFERENCE_SCREEN_HEIGHT;
  public static boolean IS_FULLSCREEN = true;
  public static boolean IS_VSYNC_ENABLED = true;
  public static boolean USE_OPENGL_CORE_PROFILE = true;
  public static boolean USE_HIGH_DPI = true;
  public static boolean IS_WINDOW_RESIZABLE = true;
  public static boolean IS_WINDOW_DECORATED = true;
  public static String WINDOW_TITLE = "Peril";

  private GraphicsSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
