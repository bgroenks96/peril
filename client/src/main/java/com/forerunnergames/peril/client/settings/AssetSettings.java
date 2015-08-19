package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class AssetSettings
{
  // @formatter:off

  private static final TextureLoader.TextureParameter GENERAL_TEXTURE_PARAMETER = new TextureLoader.TextureParameter ();
  private static final TextureLoader.TextureParameter FONT_TEXTURE_PARAMETER = new TextureLoader.TextureParameter ();
  private static final SkinLoader.SkinParameter SKIN_PARAMETER = new SkinLoader.SkinParameter ("screens/shared/skins/atlases/uiskin.atlas");

  static
  {
    GENERAL_TEXTURE_PARAMETER.genMipMaps = GraphicsSettings.TEXTURE_MIPMAPPING;
    GENERAL_TEXTURE_PARAMETER.minFilter = GraphicsSettings.TEXTURE_MINIFICATION_FILTER;
    GENERAL_TEXTURE_PARAMETER.magFilter = GraphicsSettings.TEXTURE_MAGNIFICATION_FILTER;

    FONT_TEXTURE_PARAMETER.genMipMaps = GraphicsSettings.FONT_TEXTURE_MIPMAPPING;
    FONT_TEXTURE_PARAMETER.minFilter = GraphicsSettings.FONT_TEXTURE_MINIFICATION_FILTER;
    FONT_TEXTURE_PARAMETER.magFilter = GraphicsSettings.FONT_TEXTURE_MAGNIFICATION_FILTER;
  }

  // External assets directory is relative to:
  // - the home directory of the current user on desktop (Windows / OS X / Linux)
  // - the SD card root on mobile (Android / iOS)
  // and is accessed via Gdx.files.external
  public static final String RELATIVE_EXTERNAL_ASSETS_DIRECTORY = "peril/assets/";

  // Prepend: Relative countries directory (compile-time)
  // Append: Country atlases (runtime)
  public static final String RELATIVE_COUNTRY_ATLASES_DIRECTORY = "atlases/";

  // Map resource files
  public static final String MAP_BACKGROUND_IMAGE_FILENAME = "background.png";
  public static final Class <Texture> MAP_BACKGROUND_IMAGE_TYPE = Texture.class;
  public static final TextureLoader.TextureParameter MAP_BACKGROUND_IMAGE_PARAMETER = GENERAL_TEXTURE_PARAMETER;
  public static final String MAP_INPUT_DETECTION_IMAGE_FILENAME = "inputDetection.png";
  public static final Class <Pixmap> MAP_INPUT_DETECTION_IMAGE_TYPE = Pixmap.class;
  public static final String CONTINENT_INPUT_DETECTION_DATA_FILENAME = "inputDetection.txt";
  public static final String COUNTRY_IMAGE_DATA_FILENAME = "imageData.txt";
  public static final String COUNTRY_INPUT_DETECTION_DATA_FILENAME = "inputDetection.txt";

  public static boolean isValidCountryAtlasFileName (final String fileName, final int expectedAtlasNumber)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    if (expectedAtlasNumber < 0) return false;

    return ("countries" + expectedAtlasNumber + ".atlas").equals (fileName);
  }

  public static boolean isAtlasFileType (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    return fileName.endsWith (".atlas");
  }

  public static String ABSOLUTE_UPDATED_ASSETS_DIRECTORY = "";
  public static boolean UPDATE_ASSETS = true;

  // Shared
  public static final AssetDescriptor <Texture> QUIT_POPUP_BACKGROUND_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/shared/popups/quit/background.png", Texture.class, GENERAL_TEXTURE_PARAMETER);
  public static final AssetDescriptor <Pixmap> NORMAL_CURSOR_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/shared/cursors/normalCursor.png", Pixmap.class);
  public static final AssetDescriptor <Skin> SKIN_JSON_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/shared/skins/atlases/uiskin.json", Skin.class, SKIN_PARAMETER);

  // Menus
  public static final AssetDescriptor <TextureAtlas> MENU_ATLAS_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/menus/shared/atlases/menus.atlas", TextureAtlas.class);
  public static final AssetDescriptor <Music> MENU_MUSIC_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/menus/shared/music/peril.ogg", Music.class);

  // Play Screen
  public static final AssetDescriptor <Music> PLAY_SCREEN_MUSIC_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/shared/music/emerald-gates.mp3", Music.class);

  // Classic Mode Play Screen
  public static final AssetDescriptor <Texture> CLASSIC_MODE_PLAY_SCREEN_BACKGROUND_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/background.png", Texture.class, GENERAL_TEXTURE_PARAMETER);
  public static final AssetDescriptor <Texture> CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_POPUP_BACKGROUND_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/popups/armymovement/shared/background.png", Texture.class, GENERAL_TEXTURE_PARAMETER);
  public static final AssetDescriptor <Texture> CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_POPUP_FOREGROUND_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/popups/armymovement/shared/foreground.png", Texture.class, GENERAL_TEXTURE_PARAMETER);
  public static final AssetDescriptor <Texture> CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_FOREGROUND_ARROW_TEXT_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/popups/armymovement/occupy/occupying.png", Texture.class, GENERAL_TEXTURE_PARAMETER);
  public static final AssetDescriptor <Texture> CLASSIC_MODE_PLAY_SCREEN_ARMY_MOVEMENT_OCCUPATION_TITLE_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/classic/popups/armymovement/occupy/title.png", Texture.class, GENERAL_TEXTURE_PARAMETER);

  // Peril Mode Play Screen
  public static final AssetDescriptor <TextureAtlas> PERIL_MODE_ATLAS_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/game/play/modes/peril/atlases/perilMode.atlas", TextureAtlas.class);
  public static final String PERIL_MODE_GRIDLINES_ATLAS_NINEPATCH_NAME = "gridMiddle";

  // Distance Field Font Shaders
  public static final String DISTANCE_FIELD_FONT_VERTEX_SHADER_FILENAME =
          "screens/shared/skins/fonts/distancefield/shaders/font.vert";
  public static final String DISTANCE_FIELD_FONT_FRAGMENT_SHADER_FILENAME =
          "screens/shared/skins/fonts/distancefield/shaders/font.frag";

  // @formatter:on

  private AssetSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
