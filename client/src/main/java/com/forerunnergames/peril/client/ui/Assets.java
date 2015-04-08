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
public final class Assets
{
  public static Texture menuBackground;
  public static NinePatch rightMenuBackgroundShadow;
  public static Texture mainMenuText;
  public static Texture topMenuBarExtensionShadow;
  public static Texture bottomMenuBarExtensionShadow;
  public static Texture leftMenuBarShadow;
  public static Texture rightMenuBarShadow;
  public static Music menuMusic;
  public static Texture playScreenBackground;
  public static Texture playScreenMapBackground;
  public static Pixmap playScreenMapInputDetection;
  public static Music playScreenMusic;
  public static Pixmap menuNormalCursor;
  public static Pixmap playScreenNormalCursor;
  public static BitmapFont aurulentSans16;
  public static BitmapFont droidSansMono18;
  public static BitmapFont armyCircleDistanceFieldFont;
  public static Texture armyMovementBackground;
  public static Texture armyMovementForegroundArrow;
  public static Texture armyMovementForegroundArrowText;
  public static Texture armyMovementOccupationTitle;
  public static TextureAtlas perilModeAtlas;
  public static NinePatch perilModeGridLines;
  public static ShaderProgram distanceFieldFontShader;
  public static ImmutableList <TextureAtlas> countryAtlases;
  public static Skin skin;
  private static final Logger log = LoggerFactory.getLogger (Assets.class);
  private static Texture menuRightBackgroundShadowTexture;
  private static Texture aurulentSans16Texture;
  private static Texture droidSansMono18Texture;
  private static Texture armyCircleDistanceFieldFontTexture;
  private static boolean isLoaded = false;

  public static void dispose ()
  {
    if (!isLoaded)
    {
      log.warn ("Cannot dispose assets: Assets are not loaded!");
      return;
    }

    menuBackground.dispose ();
    menuRightBackgroundShadowTexture.dispose ();
    mainMenuText.dispose ();
    topMenuBarExtensionShadow.dispose ();
    bottomMenuBarExtensionShadow.dispose ();
    leftMenuBarShadow.dispose ();
    rightMenuBarShadow.dispose ();
    playScreenBackground.dispose ();
    playScreenMapBackground.dispose ();
    playScreenMapInputDetection.dispose ();
    menuNormalCursor.dispose ();
    playScreenNormalCursor.dispose ();
    aurulentSans16.dispose ();
    aurulentSans16Texture.dispose ();
    droidSansMono18.dispose ();
    droidSansMono18Texture.dispose ();
    armyCircleDistanceFieldFont.dispose ();
    armyCircleDistanceFieldFontTexture.dispose ();
    armyMovementBackground.dispose ();
    armyMovementForegroundArrow.dispose ();
    armyMovementForegroundArrowText.dispose ();
    armyMovementOccupationTitle.dispose ();
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

    menuBackground = new Texture (Gdx.files.internal ("screens/menus/shared/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    menuRightBackgroundShadowTexture = new Texture (Gdx.files.internal ("screens/menus/shared/rightBackgroundShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    rightMenuBackgroundShadow = new NinePatch (menuRightBackgroundShadowTexture);
    mainMenuText = new Texture (Gdx.files.internal ("screens/menus/main/text.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    topMenuBarExtensionShadow = new Texture (Gdx.files.internal ("screens/menus/shared/topMenuBarExtensionShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    bottomMenuBarExtensionShadow = new Texture (Gdx.files.internal ("screens/menus/shared/bottomMenuBarExtensionShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    leftMenuBarShadow = new Texture (Gdx.files.internal ("screens/menus/shared/leftMenuBarShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    rightMenuBarShadow = new Texture (Gdx.files.internal ("screens/menus/shared/rightMenuBarShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    menuMusic = Gdx.audio.newMusic (Gdx.files.internal ("music/menuScreens.mp3"));
    playScreenBackground = new Texture (Gdx.files.internal ("screens/game/play/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    playScreenMapBackground = new Texture (Gdx.files.internal ("screens/game/play/map/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    playScreenMapInputDetection = new Pixmap (Gdx.files.internal ("screens/game/play/map/inputDetection.png"));
    playScreenMusic = Gdx.audio.newMusic (Gdx.files.internal ("music/playScreen.mp3"));
    menuNormalCursor = new Pixmap (Gdx.files.internal ("mouse/normalCursor.png"));
    playScreenNormalCursor = new Pixmap (Gdx.files.internal ("mouse/normalCursor.png"));
    aurulentSans16Texture = new Texture (Gdx.files.internal ("fonts/aurulentsans/aurulent-sans-16.png"), GraphicsSettings.FONT_TEXTURE_MIPMAPPING);
    aurulentSans16Texture.setFilter (GraphicsSettings.FONT_TEXTURE_MINIFICATION_FILTER, GraphicsSettings.FONT_TEXTURE_MAGNIFICATION_FILTER);
    aurulentSans16 = new BitmapFont (Gdx.files.internal ("fonts/aurulentsans/aurulent-sans-16.fnt"), new TextureRegion (aurulentSans16Texture), false);
    droidSansMono18Texture = new Texture (Gdx.files.internal ("fonts/droidsans/mono/droid-sans-mono-18.png"), GraphicsSettings.FONT_TEXTURE_MIPMAPPING);
    droidSansMono18Texture.setFilter (GraphicsSettings.FONT_TEXTURE_MINIFICATION_FILTER, GraphicsSettings.FONT_TEXTURE_MAGNIFICATION_FILTER);
    droidSansMono18 = new BitmapFont (Gdx.files.internal ("fonts/droidsans/mono/droid-sans-mono-18.fnt"), new TextureRegion (droidSansMono18Texture), false);
    armyCircleDistanceFieldFontTexture = new Texture (Gdx.files.internal ("screens/game/play/map/fonts/armyCircleDigits.png"), GraphicsSettings.FONT_TEXTURE_MIPMAPPING);
    armyCircleDistanceFieldFontTexture.setFilter (GraphicsSettings.FONT_TEXTURE_MINIFICATION_FILTER, GraphicsSettings.FONT_TEXTURE_MAGNIFICATION_FILTER);
    armyCircleDistanceFieldFont = new BitmapFont (Gdx.files.internal ("screens/game/play/map/fonts/armyCircleDigits.fnt"), new TextureRegion (armyCircleDistanceFieldFontTexture), false);
    armyMovementBackground = new Texture (Gdx.files.internal ("widgets/popups/armymovement/shared/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    armyMovementForegroundArrow = new Texture (Gdx.files.internal ("widgets/popups/armymovement/shared/foreground.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    armyMovementForegroundArrowText = new Texture (Gdx.files.internal ("widgets/popups/armymovement/occupation/occupying.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    armyMovementOccupationTitle = new Texture (Gdx.files.internal ("widgets/popups/armymovement/occupation/title.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
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

    setFilter (menuBackground);
    setFilter (menuRightBackgroundShadowTexture);
    setFilter (mainMenuText);
    setFilter (topMenuBarExtensionShadow);
    setFilter (bottomMenuBarExtensionShadow);
    setFilter (leftMenuBarShadow);
    setFilter (rightMenuBarShadow);
    setFilter (playScreenBackground);
    setFilter (playScreenMapBackground);
    setFilter (armyMovementForegroundArrow);
    setFilter (armyMovementForegroundArrowText);
    setFilter (armyMovementOccupationTitle);

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
