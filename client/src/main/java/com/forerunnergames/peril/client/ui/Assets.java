package com.forerunnergames.peril.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Use TextureAtlas.
// TODO: Use AssetManager.
public final class Assets
{
  public static BitmapFont defaultFont;
  public static TextureAtlas menuAtlas;
  public static Music menuMusic;
  public static Texture playScreenBackground;
  public static Texture playMapBackground;
  public static Pixmap playMapInputDetection;
  public static Music playScreenMusic;
  public static Pixmap menuNormalCursor;
  public static Pixmap playScreenNormalCursor;
  public static Texture armyMovementBackground;
  public static Texture armyMovementForegroundArrow;
  public static Texture armyMovementForegroundArrowText;
  public static Texture armyMovementOccupationTitle;
  public static Texture quitPopupBackground;
  public static TextureAtlas perilModeAtlas;
  public static NinePatch perilModeGridLines;
  public static ShaderProgram distanceFieldFontShader;
  public static ImmutableList <TextureAtlas> countryAtlases;
  public static Skin skin;
  private static final Logger log = LoggerFactory.getLogger (Assets.class);
  private static boolean isLoaded = false;

  public static void dispose ()
  {
    if (!isLoaded)
    {
      log.warn ("Cannot dispose assets: Assets are not loaded!");
      return;
    }

    defaultFont.dispose ();
    menuAtlas.dispose ();
    menuNormalCursor.dispose ();
    menuMusic.dispose ();
    playScreenBackground.dispose ();
    playMapBackground.dispose ();
    playMapInputDetection.dispose ();
    playScreenNormalCursor.dispose ();
    playScreenMusic.dispose ();
    armyMovementBackground.dispose ();
    armyMovementForegroundArrow.dispose ();
    armyMovementForegroundArrowText.dispose ();
    armyMovementOccupationTitle.dispose ();
    quitPopupBackground.dispose ();
    distanceFieldFontShader.dispose ();
    perilModeAtlas.dispose ();
    skin.dispose ();

    for (final TextureAtlas atlas : countryAtlases)
    {
      atlas.dispose ();
    }

    log.info ("Successfully disposed all assets.");

    isLoaded = false;
  }

  public static void load ()
  {
    if (isLoaded)
    {
      log.warn ("Cannot load assets: Assets are already loaded!");
      return;
    }

    final FileHandle destAssetsDir = Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY);
    final FileHandle sourceAssetsDir = Gdx.files.absolute (AssetSettings.ABSOLUTE_UPDATED_ASSETS_DIRECTORY);

    if (AssetSettings.UPDATE_ASSETS)
    {
      log.info ("Attempting to update assets in \"{}\" from \"{}\"...", destAssetsDir.file (), sourceAssetsDir);

      try
      {
        log.info ("Removing old assets...");

        destAssetsDir.deleteDirectory ();

        log.info ("Copying new assets...");

        sourceAssetsDir.copyTo (destAssetsDir);

        log.info ("Successfully updated assets.");
      }
      catch (final GdxRuntimeException e)
      {
        throw new RuntimeException ("Failed to update assets from: \""
                + AssetSettings.ABSOLUTE_UPDATED_ASSETS_DIRECTORY + "\".\n" + "Make sure that "
                + ClientApplicationProperties.UPDATED_ASSETS_DIRECTORY_KEY + " is properly set in \""
                + ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME + "\".\n" + "Also, "
                + ClientApplicationProperties.UPDATE_ASSETS_KEY
                + " must be set to true (in the same file) the first time you run the game.\n"
                + "If you already tried all of that, you can set " + ClientApplicationProperties.UPDATE_ASSETS_KEY
                + " to false.\nIn that case, you still need to make sure that you have a copy of all assets in "
                + destAssetsDir.file () + ".\n\n" + "Nerdy developer details:\n", e);
      }
    }
    else
    {
      log.warn ("Assets are not being updated.\nTo change this behavior, change {} in {} from false to true.\nMake sure to back up any customizations you made to any assets first, as your changes will be overwritten.",
                ClientApplicationProperties.UPDATE_ASSETS_KEY,
                ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME);
    }

    ShaderProgram.pedantic = false;

    try
    {
      log.info ("Attempting to load assets from: \"{}\"...", destAssetsDir.file ());

      // @formatter:off
      defaultFont = new BitmapFont ();
      menuAtlas = new TextureAtlas (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/menus/shared/atlases/menus.atlas"));
      menuMusic = Gdx.audio.newMusic (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/menus/shared/music/peril.ogg"));
      menuNormalCursor = new Pixmap (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/shared/cursors/normalCursor.png"));
      playMapBackground = new Texture (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/maps/classic/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
      playMapInputDetection = new Pixmap (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/maps/classic/inputDetection.png"));
      playScreenBackground = new Texture (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
      playScreenMusic = Gdx.audio.newMusic (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/shared/music/emerald-gates.mp3"));
      playScreenNormalCursor = new Pixmap (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/shared/cursors/normalCursor.png"));
      armyMovementBackground = new Texture (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/popups/armymovement/shared/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
      armyMovementForegroundArrow = new Texture (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/popups/armymovement/shared/foreground.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
      armyMovementForegroundArrowText = new Texture (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/popups/armymovement/occupy/occupying.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
      armyMovementOccupationTitle = new Texture (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/popups/armymovement/occupy/title.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
      quitPopupBackground = new Texture (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/shared/popups/quit/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
      perilModeAtlas = new TextureAtlas (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/peril/atlases/perilMode.atlas"));
      perilModeGridLines = perilModeAtlas.createPatch ("gridMiddle");
      skin = new Skin (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/shared/skins/atlases/uiskin.json"));
      countryAtlases = ImmutableList.of (
              new TextureAtlas (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/maps/classic/countries/atlases/countries0.atlas")),
              new TextureAtlas (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/maps/classic/countries/atlases/countries1.atlas")),
              new TextureAtlas (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/maps/classic/countries/atlases/countries2.atlas")),
              new TextureAtlas (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/maps/classic/countries/atlases/countries3.atlas")),
              new TextureAtlas (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/game/play/modes/classic/maps/classic/countries/atlases/countries4.atlas")));

      distanceFieldFontShader = new ShaderProgram (Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/shared/skins/fonts/distancefield/shaders/font.vert"), Gdx.files.external (AssetSettings.RELATIVE_EXTERNAL_ASSETS_DIRECTORY + "/screens/shared/skins/fonts/distancefield/shaders/font.frag"));
      // @formatter:on

      log.info ("Successfully loaded assets from: \"{}\"...", destAssetsDir.file ());
    }
    catch (final GdxRuntimeException e)
    {
      throw new RuntimeException ("Failed to load assets from: \"" + destAssetsDir.file () + "\"\n" + "Make sure that "
              + ClientApplicationProperties.UPDATED_ASSETS_DIRECTORY_KEY + " is properly set in \""
              + ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME + "\"\n" + "Also, "
              + ClientApplicationProperties.UPDATE_ASSETS_KEY
              + " must be set to true (in the same file) the first time you run the game.\n\n"
              + "Nerdy developer details:\n", e);
    }

    if (!distanceFieldFontShader.isCompiled ())
    {
      Gdx.app.error ("distanceFieldFontShader", "Compilation failed:\n" + distanceFieldFontShader.getLog ());
    }

    setFilter (playScreenBackground);
    setFilter (playMapBackground);
    setFilter (armyMovementForegroundArrow);
    setFilter (armyMovementForegroundArrowText);
    setFilter (armyMovementOccupationTitle);
    setFilter (quitPopupBackground);

    isLoaded = true;
  }

  private static void setFilter (final GLTexture texture)
  {
    texture.setFilter (GraphicsSettings.TEXTURE_MINIFICATION_FILTER, GraphicsSettings.TEXTURE_MAGNIFICATION_FILTER);
  }

  private Assets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
