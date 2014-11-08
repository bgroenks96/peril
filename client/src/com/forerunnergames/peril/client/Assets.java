package com.forerunnergames.peril.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;

import com.forerunnergames.tools.common.Classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Use TextureAtlas.
public final class Assets
{
  private static final Logger log = LoggerFactory.getLogger (Assets.class);
  public static Texture menuBackground;
  public static NinePatch rightMenuBackgroundShadow;
  public static Texture mainMenuText;
  public static Texture topMenuBarExtensionShadow;
  public static Texture bottomMenuBarExtensionShadow;
  public static Texture leftMenuBarShadow;
  public static Texture rightMenuBarShadow;
  private static Texture menuRightBackgroundShadowTexture;
  private static boolean isLoaded = false;

  public static void load()
  {
    if (isLoaded)
    {
      log.warn ("Cannot load assets: Assets are already loaded!");
      return;
    }

    menuBackground = new Texture (Gdx.files.internal ("ui/screens/menus/shared/background.png"));
    menuRightBackgroundShadowTexture = new Texture (Gdx.files.internal ("ui/screens/menus/shared/rightBackgroundShadow.png"));
    rightMenuBackgroundShadow = new NinePatch (menuRightBackgroundShadowTexture);
    mainMenuText = new Texture (Gdx.files.internal ("ui/screens/menus/main/text.png"));
    topMenuBarExtensionShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/topMenuBarExtensionShadow.png"));
    bottomMenuBarExtensionShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/bottomMenuBarExtensionShadow.png"));
    leftMenuBarShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/leftMenuBarShadow.png"));
    rightMenuBarShadow = new Texture (Gdx.files.internal ("ui/screens/menus/shared/rightMenuBarShadow.png"));

    isLoaded = true;
  }

  public static void dispose()
  {
    if (! isLoaded)
    {
      log.warn ("Cannot dispose assets: Assets are not loaded!");
      return;
    }

    menuBackground.dispose();
    menuRightBackgroundShadowTexture.dispose();
    mainMenuText.dispose();
    topMenuBarExtensionShadow.dispose();
    bottomMenuBarExtensionShadow.dispose();
    leftMenuBarShadow.dispose();
    rightMenuBarShadow.dispose();

    isLoaded = false;
  }

  private Assets()
  {
    Classes.instantiationNotAllowed();
  }
}
