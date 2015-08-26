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
  public static final String VALID_COUNTRY_ATLAS_FILENAME_DESCRIPTION =
          "1) Must begin with the word \'countries\' (all lowercase, without quotes)\n" +
          "2) The word \'countries\' must be followed by a consecutive, increasing, whole number index, starting from \'1\' for the first country atlas.\n" +
          "3) The index number must be followed by \'.atlas\' (all lowercase, without quotes)\n" +
          "4) Each .atlas file must be accompanied by a corresponding .png file with the same name, in the same folder.\n\n" +
          "Examples of valid .atlas filenames: countries1.atlas, countries2.atlas, countries3.atlas\n" +
          "Examples of valid .png filenames: countries1.png, countries2.png, countries3.png\n";

  public static boolean isValidCountryAtlasPackFileName (final String fileName, final int expectedAtlasIndex)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    if (expectedAtlasIndex < 1) return false;

    return ("countries" + expectedAtlasIndex + ".atlas").equals (fileName);
  }

  public static boolean isValidCountryAtlasImageFileName (final String fileName, final int expectedAtlasIndex)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    if (expectedAtlasIndex < 1) return false;

    return ("countries" + expectedAtlasIndex + ".png").equals (fileName);
  }

  public static String getValidCountryAtlasPackFileName (final int expectedAtlasIndex)
  {
    Arguments.checkLowerInclusiveBound (expectedAtlasIndex, 1, "expectedAtlasIndex");

    return "countries" + expectedAtlasIndex + ".atlas";
  }

  public static String getValidCountryAtlasImageFileName (final int expectedAtlasIndex)
  {
    Arguments.checkLowerInclusiveBound (expectedAtlasIndex, 1, "expectedAtlasIndex");

    return "countries" + expectedAtlasIndex + ".png";
  }

  public static boolean isAtlasPackFileType (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    return fileName.endsWith (".atlas");
  }

  public static boolean isAtlasImageFileType (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    return fileName.endsWith (".png");
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

  // Loading Screen
  public static final AssetDescriptor <Texture> LOADING_SCREEN_BACKGROUND_ASSET_DESCRIPTOR = new AssetDescriptor <> (
          "screens/loading/background.png", Texture.class, GENERAL_TEXTURE_PARAMETER);

  // Menu Screens
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
