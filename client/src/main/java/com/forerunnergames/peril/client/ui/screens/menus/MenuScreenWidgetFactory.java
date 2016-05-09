/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.menus;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.NetworkConstants;

public final class MenuScreenWidgetFactory extends AbstractWidgetFactory
{
  public MenuScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
  {
    return AssetSettings.MENU_SCREEN_SKIN_ASSET_DESCRIPTOR;
  }

  public Image createMenuBar ()
  {
    return new Image (createMenuBarDrawable ());
  }

  public Drawable createMenuBarDrawable ()
  {
    return new TiledDrawable (createTextureRegion ("menu-bar"));
  }

  public Image createRightBackgroundShadow ()
  {
    return new Image (createRightBackgroundShadowDrawable ());
  }

  public Drawable createRightBackgroundShadowDrawable ()
  {
    return new TextureRegionDrawable (createTextureRegion ("right-background-shadow"));
  }

  public Image createTopBackgroundShadow ()
  {
    return new Image (createTopBackgroundShadowDrawable ());
  }

  public Drawable createTopBackgroundShadowDrawable ()
  {
    return new TiledDrawable (createTopBackgroundShadowSprite ());
  }

  public Image createBottomBackgroundShadow ()
  {
    return new Image (createBottomBackgroundShadowDrawable ());
  }

  public Drawable createBottomBackgroundShadowDrawable ()
  {
    return new TiledDrawable (createBottomBackgroundShadowSprite ());
  }

  public Image createTitleBackground ()
  {
    return new Image (createTitleBackgroundDrawable ());
  }

  public Drawable createTitleBackgroundDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion ("menu-title-background"));
  }

  public Image createScreenBackgroundLeft ()
  {
    return new Image (createScreenBackgroundLeftDrawable ());
  }

  public Drawable createScreenBackgroundLeftDrawable ()
  {
    return new TextureRegionDrawable (createTextureRegion ("background-left"));
  }

  public Image createScreenBackgroundRight ()
  {
    return new Image (createScreenBackgroundRightDrawable ());
  }

  public Drawable createScreenBackgroundRightDrawable ()
  {
    return new TextureRegionDrawable (createTextureRegion ("background-right"));
  }

  public Image createLeftMenuBarShadow ()
  {
    return new Image (createLeftMenuBarShadowDrawable ());
  }

  public Drawable createLeftMenuBarShadowDrawable ()
  {
    return new TextureRegionDrawable (createLeftMenuBarShadowTextureRegion ());
  }

  public Image createRightMenuBarShadow ()
  {
    return new Image (createRightMenuBarShadowDrawable ());
  }

  public Drawable createRightMenuBarShadowDrawable ()
  {
    return new TextureRegionDrawable (createRightMenuBarShadowTextureRegion ());
  }

  public Label createTitle (final String titleText, final int alignment)
  {
    Arguments.checkIsNotNull (titleText, "titleText");

    return createLabel (titleText, alignment, createTitleStyle ());
  }

  public Label.LabelStyle createTitleStyle ()

  {
    return getSkinResource ("menu-title", Label.LabelStyle.class);
  }

  public Label createSubTitle (final String titleText, final int alignment)
  {
    Arguments.checkIsNotNull (titleText, "titleText");

    return createLabel (titleText, alignment, createSubTitleStyle ());
  }

  public Label.LabelStyle createSubTitleStyle ()
  {
    return getSkinResource ("menu-subtitle", Label.LabelStyle.class);
  }

  public ImageTextButton createMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageTextButton menuChoiceButton = new ImageTextButton (choiceText, createMenuChoiceStyle ());

    final Stack singlePlayerButtonStack = new Stack ();
    singlePlayerButtonStack.add (new Container <> (menuChoiceButton.getLabel ()).left ().padLeft (60));
    singlePlayerButtonStack.add (menuChoiceButton.getImage ());
    menuChoiceButton.clearChildren ();
    menuChoiceButton.add (singlePlayerButtonStack).fill ().expand ();
    menuChoiceButton.addListener (listener);

    return menuChoiceButton;
  }

  public ImageTextButton.ImageTextButtonStyle createMenuChoiceStyle ()
  {
    return new ImageTextButton.ImageTextButtonStyle (getSkinResource ("menu-choice", TextButton.TextButtonStyle.class));
  }

  public Label createMenuSettingSectionTitleLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createMenuSettingSectionTitleLabelStyle ());
  }

  public Label.LabelStyle createMenuSettingSectionTitleLabelStyle ()
  {
    return createLabelStyle ("menu-settings-section-title");
  }

  public Label createMenuSettingLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createMenuSettingLabelStyle ());
  }

  public Label.LabelStyle createMenuSettingLabelStyle ()
  {
    return createLabelStyle ("menu-settings");
  }

  public TextField createPlayerNameTextField ()
  {
    return createTextField (InputSettings.INITIAL_PLAYER_NAME, GameSettings.MAX_PLAYER_NAME_LENGTH,
                            InputSettings.VALID_PLAYER_NAME_TEXTFIELD_INPUT_PATTERN, createPlayerNameTextFieldStyle ());
  }

  public TextField.TextFieldStyle createPlayerNameTextFieldStyle ()
  {
    return createTextFieldStyle ("default");
  }

  public TextField createClanNameTextField ()
  {
    return createTextField (InputSettings.INITIAL_CLAN_NAME, GameSettings.MAX_CLAN_NAME_LENGTH,
                            InputSettings.VALID_CLAN_NAME_TEXTFIELD_PATTERN, createClanNameTextFieldStyle ());
  }

  public TextField.TextFieldStyle createClanNameTextFieldStyle ()
  {
    return createTextFieldStyle ("default");
  }

  public TextField createServerNameTextField ()
  {
    return createTextField (InputSettings.INITIAL_SERVER_NAME, NetworkSettings.MAX_SERVER_NAME_LENGTH,
                            InputSettings.VALID_SERVER_NAME_TEXTFIELD_INPUT_PATTERN, createServerNameTextFieldStyle ());
  }

  public TextField.TextFieldStyle createServerNameTextFieldStyle ()
  {
    return createTextFieldStyle ("default");
  }

  public CheckBox createClanNameCheckBox (final ChangeListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createCheckBox (createClanNameCheckBoxStyle (), listener);
  }

  public CheckBox.CheckBoxStyle createClanNameCheckBoxStyle ()
  {
    return getSkinResource ("default", CheckBox.CheckBoxStyle.class);
  }

  public Label createPlayerLimitLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createPlayerLimitLabelStyle ());
  }

  public Label.LabelStyle createPlayerLimitLabelStyle ()
  {
    return createLabelStyle ("option-box");
  }

  public Label createMapNameLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createMapNameLabelStyle ());
  }

  public Label.LabelStyle createMapNameLabelStyle ()
  {
    return createLabelStyle ("option-box");
  }

  public ImageButton createCustomizePlayersButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createCustomizePlayersButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createCustomizePlayersButtonStyle ()
  {
    return createImageButtonStyle ("options");
  }

  public ImageButton createCustomizeMapButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createCustomizeMapButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createCustomizeMapButtonStyle ()
  {
    return createImageButtonStyle ("options");
  }

  public SelectBox <Integer> createWinPercentSelectBox ()
  {
    return createSelectBox (createWinPercentSelectBoxStyle ());
  }

  public SelectBox.SelectBoxStyle createWinPercentSelectBoxStyle ()
  {
    return createSelectBoxStyle ("default");
  }

  public SelectBox <String> createInitialCountryAssignmentSelectBox ()
  {
    return createSelectBox (createInitialCountryAssignmentSelectBoxStyle ());
  }

  public SelectBox.SelectBoxStyle createInitialCountryAssignmentSelectBoxStyle ()
  {
    return createSelectBoxStyle ("default");
  }

  public SelectBox <Integer> createSpectatorsSelectBox ()
  {
    return createSelectBox (createSpectatorLimitSelectBoxStyle ());
  }

  public SelectBox.SelectBoxStyle createSpectatorLimitSelectBoxStyle ()
  {
    return createSelectBoxStyle ("default");
  }

  public TextField createServerAddressTextField ()
  {
    return createTextField (InputSettings.INITIAL_SERVER_ADDRESS, NetworkConstants.MAX_SERVER_ADDRESS_STRING_LENGTH,
                            NetworkConstants.SERVER_ADDRESS_PATTERN, createServerAddressTextFieldStyle ());
  }

  public TextField.TextFieldStyle createServerAddressTextFieldStyle ()
  {
    return createTextFieldStyle ("default");
  }

  public Label createPlayerSettingsSectionTitleLabel ()
  {
    return createMenuSettingSectionTitleLabel ("Your Player");
  }

  public Label.LabelStyle createPlayerSettingsSectionTitleLabelStyle ()
  {
    return createMenuSettingSectionTitleLabelStyle ();
  }

  public Label createPlayerNameSettingLabel ()
  {
    return createMenuSettingLabel ("Name");
  }

  public Label.LabelStyle createPlayerNameSettingLabelStyle ()
  {
    return createMenuSettingLabelStyle ();
  }

  public Label createClanTagSettingLabel ()
  {
    return createMenuSettingLabel ("Clan Tag");
  }

  public Label.LabelStyle createClanTagSettingLabelStyle ()
  {
    return createMenuSettingLabelStyle ();
  }

  public Label createGameSettingsSectionTitleLabel ()
  {
    return createMenuSettingSectionTitleLabel ("Game Settings");
  }

  public Label.LabelStyle createGameSettingsSectionTitleLabelStyle ()
  {
    return createMenuSettingSectionTitleLabelStyle ();
  }

  private Sprite createTopBackgroundShadowSprite ()
  {
    return createSpriteFromTextureRegion ("top-and-bottom-shadow");
  }

  private Sprite createBottomBackgroundShadowSprite ()
  {
    final Sprite sprite = new Sprite (createTopBackgroundShadowSprite ());
    sprite.flip (false, true);

    return sprite;
  }

  private TextureRegion createLeftMenuBarShadowTextureRegion ()
  {
    return createTextureRegion ("left-and-right-menu-bar-shadow");
  }

  private TextureRegion createRightMenuBarShadowTextureRegion ()
  {
    final TextureRegion region = new TextureRegion (createLeftMenuBarShadowTextureRegion ());
    region.flip (true, false);

    return region;
  }
}
