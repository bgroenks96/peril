package com.forerunnergames.peril.client.ui.screens.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.QuitPopup;
import com.forerunnergames.tools.common.Arguments;

public final class MenuScreenWidgetFactory
{
  private final Skin skin;
  private final ImageTextButton.ImageTextButtonStyle buttonStyle;
  private final Sprite topBackgroundShadowSprite;
  private final Sprite bottomBackgroundShadowSprite;
  private final TextureRegion leftMenuBarShadowTextureRegion;
  private final TextureRegion rightMenuBarShadowTextureRegion;

  public MenuScreenWidgetFactory (final Skin skin)
  {
    Arguments.checkIsNotNull (skin, "skin");

    this.skin = skin;

    buttonStyle = new ImageTextButton.ImageTextButtonStyle ();
    buttonStyle.over = new SpriteDrawable (Assets.menuAtlas.createSprite ("menuChoiceOver"));
    buttonStyle.font = Assets.droidSansMono18;

    topBackgroundShadowSprite = Assets.menuAtlas.createSprite ("topAndBottomBackgroundShadow");
    bottomBackgroundShadowSprite = new Sprite (topBackgroundShadowSprite);
    bottomBackgroundShadowSprite.flip (true, false);

    leftMenuBarShadowTextureRegion = Assets.menuAtlas.findRegion ("leftAndRightMenuBarShadow");
    rightMenuBarShadowTextureRegion = new TextureRegion (leftMenuBarShadowTextureRegion);
    rightMenuBarShadowTextureRegion.flip (true, false);
  }

  public Actor createScreenBackground ()
  {
    return new Image (Assets.menuAtlas.findRegion ("menuBackground"));
  }

  public Actor createRightBackgroundShadow ()
  {
    return new Image (Assets.menuAtlas.findRegion ("rightBackgroundShadow"));
  }

  public Actor createTopBackgroundShadow ()
  {
    return new Image (new SpriteDrawable (topBackgroundShadowSprite));
  }

  public Actor createBottomBackgroundShadow ()
  {
    return new Image (new SpriteDrawable (bottomBackgroundShadowSprite));
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

  public Actor createTitle (final String titleText)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");

    return new Label (titleText, new Label.LabelStyle (Assets.skyHookMono31, Color.WHITE));
  }

  public Actor createSubTitle (final String titleText)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");

    return new Label (titleText, new Label.LabelStyle (Assets.aurulentSans16, Color.WHITE));
  }

  public Button createMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageTextButton menuChoiceButton = new ImageTextButton (choiceText, buttonStyle);
    final Stack singlePlayerButtonStack = new Stack ();
    singlePlayerButtonStack.add (new Container <> (menuChoiceButton.getLabel ()).left ().padLeft (60));
    singlePlayerButtonStack.add (menuChoiceButton.getImage ());
    menuChoiceButton.clearChildren ();
    menuChoiceButton.add (singlePlayerButtonStack).fill ().expand ();
    menuChoiceButton.addListener (listener);

    return menuChoiceButton;
  }

  public Popup createQuitPopup (final String message, final Stage stage, final PopupListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new QuitPopup (skin, message, stage, listener);
  }

  public Actor createBackButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final TextButton backButton = new TextButton ("BACK", skin);
    backButton.addListener (listener);

    return backButton;
  }
}
