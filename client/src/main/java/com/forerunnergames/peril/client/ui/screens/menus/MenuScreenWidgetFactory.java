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
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.NetworkConstants;

public final class MenuScreenWidgetFactory extends AbstractWidgetFactory
{
  private static final String MENU_BAR_DRAWABLE_NAME = "menu-bar";
  private static final String RIGHT_BACKGROUND_SHADOW_DRAWABLE_NAME = "right-background-shadow";
  private static final String TITLE_BACKGROUND_DRAWABLE_NAME = "menu-title-background";
  private static final String SCREEN_BACKGROUND_LEFT_DRAWABLE_NAME = "background-left";
  private static final String SCREEN_BACKGROUND_RIGHT_DRAWABLE_NAME = "background-right";
  private static final String PLAYER_SETTINGS_SECTION_TITLE_LABEL_NAME = "Your Player";
  private static final String PLAYER_NAME_SETTING_LABEL_NAME = "Name";
  private static final String CLAN_TAG_SETTING_LABEL_NAME = "Clan Tag";
  private static final String GAME_SETTINGS_SECTION_TITLE_LABEL_NAME = "Game Settings";
  private static final String TOP_BACKGROUND_SHADOW_SPRITE_NAME = "top-and-bottom-shadow";
  private static final String LEFT_MENU_BAR_SHADOW_TEXTURE_REGION_NAME = "left-and-right-menu-bar-shadow";

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
    return new TiledDrawable (createTextureRegion (MENU_BAR_DRAWABLE_NAME));
  }

  public Image createRightBackgroundShadow ()
  {
    return new Image (createRightBackgroundShadowDrawable ());
  }

  public Drawable createRightBackgroundShadowDrawable ()
  {
    return new TextureRegionDrawable (createTextureRegion (RIGHT_BACKGROUND_SHADOW_DRAWABLE_NAME));
  }

  public Image createTopBackgroundShadow ()
  {
    return new Image (createTopBackgroundShadowDrawable ());
  }

  public Drawable createTopBackgroundShadowDrawable ()
  {
    return new SpriteDrawable (createTopBackgroundShadowSprite ());
  }

  public Image createBottomBackgroundShadow ()
  {
    return new Image (createBottomBackgroundShadowDrawable ());
  }

  public Drawable createBottomBackgroundShadowDrawable ()
  {
    return new SpriteDrawable (createBottomBackgroundShadowSprite ());
  }

  public Image createTitleBackground ()
  {
    return new Image (createTitleBackgroundDrawable ());
  }

  public Drawable createTitleBackgroundDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion (TITLE_BACKGROUND_DRAWABLE_NAME));
  }

  public Image createScreenBackgroundLeft ()
  {
    return new Image (createScreenBackgroundLeftDrawable ());
  }

  public Drawable createScreenBackgroundLeftDrawable ()
  {
    return new TextureRegionDrawable (createTextureRegion (SCREEN_BACKGROUND_LEFT_DRAWABLE_NAME));
  }

  public Image createScreenBackgroundRight ()
  {
    return new Image (createScreenBackgroundRightDrawable ());
  }

  public Drawable createScreenBackgroundRightDrawable ()
  {
    return new TextureRegionDrawable (createTextureRegion (SCREEN_BACKGROUND_RIGHT_DRAWABLE_NAME));
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
    return getSkinResource (StyleSettings.MENU_TITLE_LABEL_STYLE, Label.LabelStyle.class);
  }

  public Label createSubTitle (final String titleText, final int alignment)
  {
    Arguments.checkIsNotNull (titleText, "titleText");

    return createLabel (titleText, alignment, createSubTitleStyle ());
  }

  public Label.LabelStyle createSubTitleStyle ()
  {
    return getSkinResource (StyleSettings.MENU_SUB_TITLE_LABEL_STYLE, Label.LabelStyle.class);
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
    return new ImageTextButton.ImageTextButtonStyle (
            getSkinResource (StyleSettings.MENU_CHOICE_IMAGE_TEXT_BUTTON_STYLE, TextButton.TextButtonStyle.class));
  }

  public Label createMenuSettingSectionTitleLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createMenuSettingSectionTitleLabelStyle ());
  }

  public Label.LabelStyle createMenuSettingSectionTitleLabelStyle ()
  {
    return createLabelStyle (StyleSettings.MENU_SETTING_SECTION_TITLE_LABEL_STYLE);
  }

  public Label createMenuSettingLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createMenuSettingLabelStyle ());
  }

  public Label.LabelStyle createMenuSettingLabelStyle ()
  {
    return createLabelStyle (StyleSettings.MENU_SETTING_LABEL_STYLE);
  }

  public TextField createPlayerNameTextField ()
  {
    return createTextField (InputSettings.INITIAL_PLAYER_NAME, GameSettings.MAX_PLAYER_NAME_LENGTH,
                            InputSettings.VALID_PLAYER_NAME_TEXTFIELD_INPUT_PATTERN, createPlayerNameTextFieldStyle ());
  }

  public TextField.TextFieldStyle createPlayerNameTextFieldStyle ()
  {
    return createTextFieldStyle (StyleSettings.MENU_PLAYER_NAME_TEXTFIELD_STYLE);
  }

  public TextField createClanNameTextField ()
  {
    return createTextField (InputSettings.INITIAL_CLAN_NAME, GameSettings.MAX_CLAN_NAME_LENGTH,
                            InputSettings.VALID_CLAN_NAME_TEXTFIELD_PATTERN, createClanNameTextFieldStyle ());
  }

  public TextField.TextFieldStyle createClanNameTextFieldStyle ()
  {
    return createTextFieldStyle (StyleSettings.MENU_CLAN_NAME_TEXTFIELD_STYLE);
  }

  public TextField createServerNameTextField ()
  {
    return createTextField (InputSettings.INITIAL_SERVER_NAME, NetworkSettings.MAX_SERVER_NAME_LENGTH,
                            InputSettings.VALID_SERVER_NAME_TEXTFIELD_INPUT_PATTERN, createServerNameTextFieldStyle ());
  }

  public TextField.TextFieldStyle createServerNameTextFieldStyle ()
  {
    return createTextFieldStyle (StyleSettings.MENU_SERVER_NAME_TEXTFIELD_STYLE);
  }

  public CheckBox createClanNameCheckBox (final ChangeListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createCheckBox (createClanNameCheckBoxStyle (), listener);
  }

  public CheckBox.CheckBoxStyle createClanNameCheckBoxStyle ()
  {
    return getSkinResource (StyleSettings.MENU_CLAN_NAME_CHECK_BOX_STYLE, CheckBox.CheckBoxStyle.class);
  }

  public Label createPlayerLimitLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createPlayerLimitLabelStyle ());
  }

  public Label.LabelStyle createPlayerLimitLabelStyle ()
  {
    return createLabelStyle (StyleSettings.MENU_PLAYER_LIMIT_LABEL_STYLE);
  }

  public Label createMapNameLabel (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    return createLabel (text, Align.left, createMapNameLabelStyle ());
  }

  public Label.LabelStyle createMapNameLabelStyle ()
  {
    return createLabelStyle (StyleSettings.MENU_MAP_NAME_LABEL_STYLE);
  }

  public ImageButton createCustomizePlayersButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createCustomizePlayersButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createCustomizePlayersButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.MENU_CUSTOMIZE_PLAYERS_IMAGE_BUTTON_STYLE);
  }

  public ImageButton createCustomizeMapButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createCustomizeMapButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createCustomizeMapButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.MENU_CUSTOMIZE_MAP_IMAGE_BUTTON_STYLE);
  }

  public SelectBox <Integer> createWinPercentSelectBox ()
  {
    return createSelectBox (createWinPercentSelectBoxStyle ());
  }

  public SelectBox.SelectBoxStyle createWinPercentSelectBoxStyle ()
  {
    return createSelectBoxStyle (StyleSettings.MENU_WIN_PERCENT_SELECT_BOX_STYLE);
  }

  public SelectBox <String> createInitialCountryAssignmentSelectBox ()
  {
    return createSelectBox (createInitialCountryAssignmentSelectBoxStyle ());
  }

  public SelectBox.SelectBoxStyle createInitialCountryAssignmentSelectBoxStyle ()
  {
    return createSelectBoxStyle (StyleSettings.MENU_INITIAL_COUNTRY_ASSIGNMENT_SELECT_BOX_STYLE);
  }

  public SelectBox <Integer> createSpectatorsSelectBox ()
  {
    return createSelectBox (createSpectatorLimitSelectBoxStyle ());
  }

  public SelectBox.SelectBoxStyle createSpectatorLimitSelectBoxStyle ()
  {
    return createSelectBoxStyle (StyleSettings.MENU_SPECTATOR_LIMIT_SELECT_BOX_STYLE);
  }

  public TextField createServerAddressTextField ()
  {
    return createTextField (InputSettings.INITIAL_SERVER_ADDRESS, NetworkConstants.MAX_SERVER_ADDRESS_STRING_LENGTH,
                            NetworkConstants.SERVER_ADDRESS_PATTERN, createServerAddressTextFieldStyle ());
  }

  public TextField.TextFieldStyle createServerAddressTextFieldStyle ()
  {
    return createTextFieldStyle (StyleSettings.MENU_SERVER_ADDRESS_TEXTFIELD_STYLE);
  }

  public Label createPlayerSettingsSectionTitleLabel ()
  {
    return createMenuSettingSectionTitleLabel (PLAYER_SETTINGS_SECTION_TITLE_LABEL_NAME);
  }

  public Label.LabelStyle createPlayerSettingsSectionTitleLabelStyle ()
  {
    return createMenuSettingSectionTitleLabelStyle ();
  }

  public Label createPlayerNameSettingLabel ()
  {
    return createMenuSettingLabel (PLAYER_NAME_SETTING_LABEL_NAME);
  }

  public Label.LabelStyle createPlayerNameSettingLabelStyle ()
  {
    return createMenuSettingLabelStyle ();
  }

  public Label createClanTagSettingLabel ()
  {
    return createMenuSettingLabel (CLAN_TAG_SETTING_LABEL_NAME);
  }

  public Label.LabelStyle createClanTagSettingLabelStyle ()
  {
    return createMenuSettingLabelStyle ();
  }

  public Label createGameSettingsSectionTitleLabel ()
  {
    return createMenuSettingSectionTitleLabel (GAME_SETTINGS_SECTION_TITLE_LABEL_NAME);
  }

  public Label.LabelStyle createGameSettingsSectionTitleLabelStyle ()
  {
    return createMenuSettingSectionTitleLabelStyle ();
  }

  private Sprite createTopBackgroundShadowSprite ()
  {
    return createSpriteFromTextureRegion (TOP_BACKGROUND_SHADOW_SPRITE_NAME);
  }

  private Sprite createBottomBackgroundShadowSprite ()
  {
    final Sprite sprite = new Sprite (createTopBackgroundShadowSprite ());
    sprite.flip (false, true);

    return sprite;
  }

  private TextureRegion createLeftMenuBarShadowTextureRegion ()
  {
    return createTextureRegion (LEFT_MENU_BAR_SHADOW_TEXTURE_REGION_NAME);
  }

  private TextureRegion createRightMenuBarShadowTextureRegion ()
  {
    final TextureRegion region = new TextureRegion (createLeftMenuBarShadowTextureRegion ());
    region.flip (true, false);

    return region;
  }
}
