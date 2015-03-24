package com.forerunnergames.peril.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.tools.common.Classes;

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
  public static Texture armyMovementBackground;
  public static Texture armyMovementOccupationTitle;
  public static Skin skin;
  private static final Logger log = LoggerFactory.getLogger (Assets.class);
  private static Texture menuRightBackgroundShadowTexture;
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
    menuNormalCursor.dispose();
    playScreenNormalCursor.dispose();
    aurulentSans16.dispose ();
    armyMovementBackground.dispose ();
    armyMovementOccupationTitle.dispose ();
    skin.dispose ();

    isLoaded = false;
  }

  public static void load ()
  {
    if (isLoaded)
    {
      log.warn ("Cannot load assets: Assets are already loaded!");
      return;
    }

    // @formatter:off
    menuBackground = new Texture (Gdx.files.internal ("ui/screens/menus/shared/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    menuRightBackgroundShadowTexture = new Texture (Gdx.files.internal ("ui/screens/menus/shared/rightBackgroundShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    rightMenuBackgroundShadow = new NinePatch (menuRightBackgroundShadowTexture);
    mainMenuText = new Texture (Gdx.files.internal ("ui/screens/menus/main/text.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    topMenuBarExtensionShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/topMenuBarExtensionShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    bottomMenuBarExtensionShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/bottomMenuBarExtensionShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    leftMenuBarShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/leftMenuBarShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    rightMenuBarShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/rightMenuBarShadow.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    menuMusic = Gdx.audio.newMusic (Gdx.files.internal ("ui/music/menuScreens.mp3"));
    playScreenBackground = new Texture (Gdx.files.internal ("ui/screens/game/play/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    playScreenMapBackground = new Texture (Gdx.files.internal ("ui/screens/game/play/map/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    playScreenMapInputDetection = new Pixmap (Gdx.files.internal ("ui/screens/game/play/map/inputDetection.png"));
    playScreenMusic = Gdx.audio.newMusic (Gdx.files.internal ("ui/music/playScreen.mp3"));
    menuNormalCursor = new Pixmap (Gdx.files.internal ("ui/mouse/normalCursor.png"));
    playScreenNormalCursor = new Pixmap (Gdx.files.internal ("ui/mouse/normalCursor.png"));
    aurulentSans16 = new BitmapFont (Gdx.files.internal ("ui/fonts/aurulentsans/aurulent-sans-16.fnt"));
    armyMovementBackground = new Texture (Gdx.files.internal ("ui/widgets/popups/armymovement/shared/background.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    armyMovementOccupationTitle = new Texture (Gdx.files.internal ("ui/widgets/popups/armymovement/occupation/title.png"), GraphicsSettings.TEXTURE_MIPMAPPING);
    skin = new Skin (Gdx.files.internal ("ui/uiskin.json"));
    // @formatter:on

    setFilter (menuBackground);
    setFilter (menuRightBackgroundShadowTexture);
    setFilter (mainMenuText);
    setFilter (topMenuBarExtensionShadow);
    setFilter (bottomMenuBarExtensionShadow);
    setFilter (leftMenuBarShadow);
    setFilter (rightMenuBarShadow);
    setFilter (playScreenBackground);
    setFilter (playScreenMapBackground);
    setFilter (armyMovementBackground);
    setFilter (armyMovementOccupationTitle);

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
