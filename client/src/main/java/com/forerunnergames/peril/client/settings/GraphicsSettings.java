package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

import com.forerunnergames.tools.common.Classes;

public final class GraphicsSettings
{
  // @formatter:off
  public static final int                   MIN_INITIAL_WINDOW_WIDTH          = 640;
  public static final int                   MIN_INITIAL_WINDOW_HEIGHT         = 480;
  public static final int                   REFERENCE_SCREEN_WIDTH            = 1920;
  public static final int                   REFERENCE_SCREEN_HEIGHT           = 1080;
  public static final Vector2               REFERENCE_SCREEN_SIZE             = new Vector2 (REFERENCE_SCREEN_WIDTH, REFERENCE_SCREEN_HEIGHT);
  public static final boolean               TEXTURE_MIPMAPPING                = true;
  public static final Texture.TextureFilter TEXTURE_MINIFICATION_FILTER       = Texture.TextureFilter.MipMapLinearNearest;
  public static final Texture.TextureFilter TEXTURE_MAGNIFICATION_FILTER      = Texture.TextureFilter.Nearest;
  public static final Texture.TextureFilter FONT_TEXTURE_MINIFICATION_FILTER  = Texture.TextureFilter.MipMapLinearLinear;
  public static final Texture.TextureFilter FONT_TEXTURE_MAGNIFICATION_FILTER = Texture.TextureFilter.Linear;
  public static final boolean               FONT_TEXTURE_MIPMAPPING           = true;
  public static final Scaling               VIEWPORT_SCALING                  = Scaling.stretch;

  public static int     INITIAL_WINDOW_WIDTH  = REFERENCE_SCREEN_WIDTH;
  public static int     INITIAL_WINDOW_HEIGHT = REFERENCE_SCREEN_HEIGHT;
  public static boolean IS_FULLSCREEN         = true;
  public static boolean IS_VSYNC_ENABLED      = true;
  public static boolean IS_WINDOW_RESIZABLE   = true;
  public static String  WINDOW_TITLE          = "Peril";
  // @formatter:on

  private GraphicsSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
