package com.forerunnergames.peril.client.ui.screens.menus;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;

public final class MenuScreenWidgetFactory extends WidgetFactory
{
  private final ImageTextButton.ImageTextButtonStyle menuChoiceTextButtonStyle;
  private final Sprite topBackgroundShadowSprite;
  private final Sprite bottomBackgroundShadowSprite;
  private final TextureRegion leftMenuBarShadowTextureRegion;
  private final TextureRegion rightMenuBarShadowTextureRegion;

  public MenuScreenWidgetFactory (final Skin skin)
  {
    super (skin);

    menuChoiceTextButtonStyle = new ImageTextButton.ImageTextButtonStyle ();
    menuChoiceTextButtonStyle.over = new SpriteDrawable (Assets.menuAtlas.createSprite ("menuChoiceOver"));
    menuChoiceTextButtonStyle.font = skin.getFont ("default-font");

    topBackgroundShadowSprite = Assets.menuAtlas.createSprite ("topAndBottomShadow");
    bottomBackgroundShadowSprite = new Sprite (topBackgroundShadowSprite);
    bottomBackgroundShadowSprite.flip (false, true);

    leftMenuBarShadowTextureRegion = Assets.menuAtlas.findRegion ("leftAndRightMenuBarShadow");
    rightMenuBarShadowTextureRegion = new TextureRegion (leftMenuBarShadowTextureRegion);
    rightMenuBarShadowTextureRegion.flip (true, false);
  }

  public Actor createScreenBackgroundLeft ()
  {
    return new Image (Assets.menuAtlas.findRegion ("backgroundLeft"));
  }

  public Actor createScreenBackgroundRight ()
  {
    return new Image (Assets.menuAtlas.findRegion ("backgroundRight"));
  }

  public Actor createMenuBar ()
  {
    return new Image (new TiledDrawable (Assets.menuAtlas.findRegion ("menuBar")));
  }

  public Actor createRightBackgroundShadow ()
  {
    return new Image (Assets.menuAtlas.findRegion ("rightBackgroundShadow"));
  }

  public Actor createTopBackgroundShadow ()
  {
    return new Image (new TiledDrawable (topBackgroundShadowSprite));
  }

  public Actor createBottomBackgroundShadow ()
  {
    return new Image (new TiledDrawable (bottomBackgroundShadowSprite));
  }

  public Actor createLeftMenuBarShadow ()
  {
    return new Image (leftMenuBarShadowTextureRegion);
  }

  public Actor createRightMenuBarShadow ()
  {
    return new Image (rightMenuBarShadowTextureRegion);
  }

  public Actor createTitleBackground ()
  {
    return new Image (new NinePatchDrawable (Assets.menuAtlas.createPatch ("menuTitleBackground")));
  }

  public Actor createTitle (final String titleText, final int alignment)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");
    Arguments.checkIsNotNegative (alignment, "alignment");

    return createLabel (titleText, alignment);
  }

  public Actor createMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageTextButton menuChoiceButton = new ImageTextButton (choiceText, menuChoiceTextButtonStyle);
    final Stack singlePlayerButtonStack = new Stack ();
    singlePlayerButtonStack.add (new Container <> (menuChoiceButton.getLabel ()).left ().padLeft (60));
    singlePlayerButtonStack.add (menuChoiceButton.getImage ());
    menuChoiceButton.clearChildren ();
    menuChoiceButton.add (singlePlayerButtonStack).fill ().expand ();
    menuChoiceButton.addListener (listener);

    return menuChoiceButton;
  }
}
