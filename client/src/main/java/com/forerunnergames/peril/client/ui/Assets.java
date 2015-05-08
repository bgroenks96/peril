package com.forerunnergames.peril.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Use TextureAtlas.
// TODO: Use AssetManager.
public final class Assets
{
  public static TextureAtlas menuAtlas;
  public static Music menuMusic;
  public static Texture playScreenBackground;
  public static Texture playMapBackground;
  public static Pixmap playMapInputDetection;
  public static Music playScreenMusic;
  public static Pixmap menuNormalCursor;
  public static Pixmap playScreenNormalCursor;
  public static BitmapFont armyCircleDistanceFieldFont;
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
  private static Texture armyCircleDistanceFieldFontTexture;
  private static boolean isLoaded = false;

  public static void dispose ()
  {
    if (!isLoaded)
    {
      log.warn ("Cannot dispose assets: Assets are not loaded!");
      return;
    }

    menuAtlas.dispose ();
    menuNormalCursor.dispose ();
    menuMusic.dispose ();
    playScreenBackground.dispose ();
    playMapBackground.dispose ();
    playMapInputDetection.dispose ();
    playScreenNormalCursor.dispose ();
    playScreenMusic.dispose ();
    armyCircleDistanceFieldFont.dispose ();
    armyCircleDistanceFieldFontTexture.dispose ();
    armyMovementBackground.dispose ();
    armyMovementForegroundArrow.dispose ();
    armyMovementForegroundArrowText.dispose ();
    armyMovementOccupationTitle.dispose ();
    quitPopupBackground.dispose ();
    distanceFieldFontShader.dispose ();
    perilModeAtlas.dispose ();
    skin.dispose ();

    for (TextureAtlas atlas : countryAtlases)
    {
      atlas.dispose ();
    }

    isLoaded = false;
  }

  // @formatter:off
  public static void load ()
  {
    if (isLoaded)
    {
      log.warn ("Cannot load assets: Assets are already loaded!");
      return;
    }

    ShaderProgram.pedantic = false;

    menuAtlas = new TextureAtlas (Gdx.files.internal ("screens/menus/menus.atlas"));
    menuMusic = Gdx.audio.newMusic (Gdx.files.internal ("music/menuScreens.mp3"));
    playScreenBackground = new Texture (Gdx.files.internal ("screens/game/play/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    playMapBackground = new Texture (Gdx.files.internal ("screens/game/play/map/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    playMapInputDetection = new Pixmap (Gdx.files.internal ("screens/game/play/map/inputDetection.png"));
    playScreenMusic = Gdx.audio.newMusic (Gdx.files.internal ("music/playScreen.mp3"));
    menuNormalCursor = new Pixmap (Gdx.files.internal ("mouse/normalCursor.png"));
    playScreenNormalCursor = new Pixmap (Gdx.files.internal ("mouse/normalCursor.png"));
    armyCircleDistanceFieldFontTexture = new Texture (Gdx.files.internal ("screens/game/play/map/fonts/armyCircleDigits.png"), false);
    armyCircleDistanceFieldFontTexture.setFilter (Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    armyCircleDistanceFieldFont = new BitmapFont (Gdx.files.internal ("screens/game/play/map/fonts/armyCircleDigits.fnt"), new TextureRegion (armyCircleDistanceFieldFontTexture), false);
    armyMovementBackground = new Texture (Gdx.files.internal ("widgets/popups/armymovement/shared/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    armyMovementForegroundArrow = new Texture (Gdx.files.internal ("widgets/popups/armymovement/shared/foreground.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    armyMovementForegroundArrowText = new Texture (Gdx.files.internal ("widgets/popups/armymovement/occupation/occupying.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    armyMovementOccupationTitle = new Texture (Gdx.files.internal ("widgets/popups/armymovement/occupation/title.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    quitPopupBackground = new Texture (Gdx.files.internal ("widgets/popups/quit/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    perilModeAtlas = new TextureAtlas (Gdx.files.internal ("screens/game/play/modes/peril/perilMode.atlas"));
    perilModeGridLines = perilModeAtlas.createPatch ("gridMiddle");
    skin = new Skin (Gdx.files.internal ("uiskin.json"));
    countryAtlases = ImmutableList.of (
            new TextureAtlas (Gdx.files.internal ("screens/game/play/map/countries/atlases/countries0.atlas")),
            new TextureAtlas (Gdx.files.internal ("screens/game/play/map/countries/atlases/countries1.atlas")),
            new TextureAtlas (Gdx.files.internal ("screens/game/play/map/countries/atlases/countries2.atlas")),
            new TextureAtlas (Gdx.files.internal ("screens/game/play/map/countries/atlases/countries3.atlas")),
            new TextureAtlas (Gdx.files.internal ("screens/game/play/map/countries/atlases/countries4.atlas")));

    distanceFieldFontShader = new ShaderProgram (Gdx.files.internal ("shaders/font.vert"), Gdx.files.internal ("shaders/font.frag"));

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
  // @formatter:on

  private static void setFilter (final GLTexture texture)
  {
    texture.setFilter (GraphicsSettings.TEXTURE_MINIFICATION_FILTER, GraphicsSettings.TEXTURE_MAGNIFICATION_FILTER);
  }

  private Assets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
