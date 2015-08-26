package com.forerunnergames.peril.client.ui.screens.menus;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;

import javax.annotation.Nullable;

public final class MenuScreenWidgetFactory extends WidgetFactory
{
  @Nullable
  private Sprite topBackgroundShadowSprite = null;
  @Nullable
  private Sprite bottomBackgroundShadowSprite = null;
  @Nullable
  private TextureRegion leftMenuBarShadowTextureRegion = null;
  @Nullable
  private TextureRegion rightMenuBarShadowTextureRegion = null;

  public MenuScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  public Actor createMenuBar ()
  {
    return new Image (new TiledDrawable (findAtlasRegion ("menuBar")));
  }

  public Actor createRightBackgroundShadow ()
  {
    return new Image (findAtlasRegion ("rightBackgroundShadow"));
  }

  public Actor createTitleBackground ()
  {
    return new Image (new NinePatchDrawable (createNinePatchFromAtlasRegion ("menuTitleBackground")));
  }

  public Actor createScreenBackgroundLeft ()
  {
    return new Image (findAtlasRegion ("backgroundLeft"));
  }

  public Actor createScreenBackgroundRight ()
  {
    return new Image (findAtlasRegion ("backgroundRight"));
  }

  public Actor createTopBackgroundShadow ()
  {
    return new Image (new TiledDrawable (getTopBackgroundShadowSprite ()));
  }

  public Actor createBottomBackgroundShadow ()
  {
    return new Image (new TiledDrawable (getBottomBackgroundShadowSprite ()));
  }

  public Actor createLeftMenuBarShadow ()
  {
    return new Image (getLeftMenuBarShadowTextureRegion ());
  }

  public Actor createRightMenuBarShadow ()
  {
    return new Image (getRightMenuBarShadowTextureRegion ());
  }

  public Actor createTitle (final String titleText, final int alignment)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");

    return createLabel (titleText, alignment, "menu-title");
  }

  public Actor createSubTitle (final String titleText, final int alignment)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");

    return createLabel (titleText, alignment, "menu-subtitle");
  }

  public Actor createMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageTextButton menuChoiceButton = new ImageTextButton (choiceText,
            new ImageTextButton.ImageTextButtonStyle (getSkinStyle ("menu-choice", TextButton.TextButtonStyle.class)));

    final Stack singlePlayerButtonStack = new Stack ();
    singlePlayerButtonStack.add (new Container <> (menuChoiceButton.getLabel ()).left ().padLeft (60));
    singlePlayerButtonStack.add (menuChoiceButton.getImage ());
    menuChoiceButton.clearChildren ();
    menuChoiceButton.add (singlePlayerButtonStack).fill ().expand ();
    menuChoiceButton.addListener (listener);

    return menuChoiceButton;
  }

  public Actor createMenuSettingSectionTitleText (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, "menu-settings-section-title");
  }

  public Actor createMenuSettingText (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, "menu-settings-label");
  }

  private Sprite getTopBackgroundShadowSprite ()
  {
    if (topBackgroundShadowSprite == null) initializeTopBackgroundShadowSprite ();

    return topBackgroundShadowSprite;
  }

  private void initializeTopBackgroundShadowSprite ()
  {
    topBackgroundShadowSprite = createSpriteFromAtlasRegion ("topAndBottomShadow");
  }

  private Sprite getBottomBackgroundShadowSprite ()
  {
    if (bottomBackgroundShadowSprite == null) initializeBottomBackgroundShadowSprite ();

    return bottomBackgroundShadowSprite;
  }

  private void initializeBottomBackgroundShadowSprite ()
  {
    bottomBackgroundShadowSprite = new Sprite (getTopBackgroundShadowSprite ());
    bottomBackgroundShadowSprite.flip (false, true);
  }

  private TextureRegion getLeftMenuBarShadowTextureRegion ()
  {
    if (leftMenuBarShadowTextureRegion == null) initializeLeftMenuBarShadowTextureRegion ();

    return leftMenuBarShadowTextureRegion;
  }

  private void initializeLeftMenuBarShadowTextureRegion ()
  {
    leftMenuBarShadowTextureRegion = findAtlasRegion ("leftAndRightMenuBarShadow");
  }

  private TextureRegion getRightMenuBarShadowTextureRegion ()
  {
    if (rightMenuBarShadowTextureRegion == null) initializeRightMenuBarShadowTextureRegion ();

    return rightMenuBarShadowTextureRegion;
  }

  private void initializeRightMenuBarShadowTextureRegion ()
  {
    rightMenuBarShadowTextureRegion = new TextureRegion (getLeftMenuBarShadowTextureRegion ());
    rightMenuBarShadowTextureRegion.flip (true, false);
  }

  private TextureAtlas getMenuAtlas ()
  {
    return getAsset (AssetSettings.MENU_ATLAS_ASSET_DESCRIPTOR);
  }

  private TextureAtlas.AtlasRegion findAtlasRegion (final String regionName)
  {
    return getMenuAtlas ().findRegion (regionName);
  }

  private Sprite createSpriteFromAtlasRegion (final String regionName)
  {
    return getMenuAtlas ().createSprite (regionName);
  }

  private NinePatch createNinePatchFromAtlasRegion (final String regionName)
  {
    return getMenuAtlas ().createPatch (regionName);
  }
}
