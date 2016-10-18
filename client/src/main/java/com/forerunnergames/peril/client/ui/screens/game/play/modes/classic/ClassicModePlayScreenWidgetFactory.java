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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenShaker;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox.ControlRoomBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox.DefaultControlRoomBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.fortification.FortificationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialogWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialogWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.AttackerBattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.BattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.DefenderBattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox.DefaultIntelBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox.IntelBox;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.QuitDialog;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.tools.common.Arguments;

public final class ClassicModePlayScreenWidgetFactory extends AbstractWidgetFactory
{
  private static final int ARMY_MOVEMENT_DIALOG_SLIDER_STEP_SIZE = 1;
  private static final String PLAY_MAP_TABLE_FOREGROUND_DRAWABLE_NAME = "play-map-borders";
  private static final String INTEL_BOX_BACKGROUND_DRAWABLE_NAME = "side-bar-borders";
  private static final String INTEL_BOX_TITLE_BACKGROUND_DRAWABLE_NAME = "side-bar-title-background";
  private static final String CONTROL_ROOM_BOX_BACKGROUND_DRAWABLE_NAME = "side-bar-borders";
  private static final String CONTROL_ROOM_BOX_TITLE_BACKGROUND_DRAWABLE_NAME = "side-bar-title-background";

  public ClassicModePlayScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  public Dialog createQuitDialog (final String message, final Stage stage, final CancellableDialogListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new QuitDialog (this, message, 587, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 284, stage, listener);
  }

  @Override
  protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
  {
    return AssetSettings.CLASSIC_MODE_PLAY_SCREEN_SKIN_ASSET_DESCRIPTOR;
  }

  public Image createPlayMapTableForegroundImage ()
  {
    return new Image (createPlayMapTableForegroundImageDrawable ());
  }

  public Drawable createPlayMapTableForegroundImageDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion (PLAY_MAP_TABLE_FOREGROUND_DRAWABLE_NAME));
  }

  public Drawable createIntelBoxBackgroundDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion (INTEL_BOX_BACKGROUND_DRAWABLE_NAME));
  }

  public Drawable createIntelBoxTitleBackgroundDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion (INTEL_BOX_TITLE_BACKGROUND_DRAWABLE_NAME));
  }

  public Drawable createControlRoomBoxBackgroundDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion (CONTROL_ROOM_BOX_BACKGROUND_DRAWABLE_NAME));
  }

  public Drawable createControlRoomBoxTitleBackgroundDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion (CONTROL_ROOM_BOX_TITLE_BACKGROUND_DRAWABLE_NAME));
  }

  public IntelBox createIntelBox (final EventListener detailedReportButtonListener)
  {
    Arguments.checkIsNotNull (detailedReportButtonListener, "detailedReportButtonListener");

    return new DefaultIntelBox (this, detailedReportButtonListener);
  }

  public ControlRoomBox createControlRoomBox (final EventListener tradeInButtonListener,
                                              final EventListener fortifyButtonListener,
                                              final EventListener endTurnButtonListener,
                                              final EventListener mySettingsButtonListener,
                                              final EventListener surrenderButtonListener)
  {
    Arguments.checkIsNotNull (tradeInButtonListener, "tradeInButtonListener");
    Arguments.checkIsNotNull (fortifyButtonListener, "fortifyButtonListener");
    Arguments.checkIsNotNull (endTurnButtonListener, "endTurnButtonListener");
    Arguments.checkIsNotNull (mySettingsButtonListener, "mySettingsButtonListener");
    Arguments.checkIsNotNull (surrenderButtonListener, "surrenderButtonListener");

    return new DefaultControlRoomBox (this, tradeInButtonListener, fortifyButtonListener, endTurnButtonListener,
            mySettingsButtonListener, surrenderButtonListener);
  }

  public FortificationDialog createFortificationDialog (final Stage stage, final CancellableDialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new FortificationDialog (this, stage, listener);
  }

  public OccupationDialog createOccupationDialog (final Stage stage, final DialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new OccupationDialog (this, stage, listener);
  }

  public Slider createArmyMovementDialogSlider (final ChangeListener changeListener)
  {
    Arguments.checkIsNotNull (changeListener, "changeListener");

    return createHorizontalSlider (0, 0, ARMY_MOVEMENT_DIALOG_SLIDER_STEP_SIZE, createArmyMovementDialogSliderStyle (),
                                   changeListener);
  }

  public Slider.SliderStyle createArmyMovementDialogSliderStyle ()
  {
    return createSliderStyle (StyleSettings.ARMY_MOVEMENT_DIALOG_SLIDER_STYLE);
  }

  public ImageButton createArmyMovementDialogMinButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementDialogMinButtonStyle (), listener);
  }

  public Label createArmyMovementDialogCountryNameLabel ()
  {
    return createLabel ("", Align.center, createArmyMovementDialogCountryNameLabelStyle ());
  }

  public Label.LabelStyle createArmyMovementDialogCountryNameLabelStyle ()
  {
    return createLabelStyle (StyleSettings.ARMY_MOVEMENT_DIALOG_COUNTRY_NAME_LABEL_STYLE);
  }

  public ImageButton.ImageButtonStyle createArmyMovementDialogMinButtonStyle ()
  {
    return getSkinResource (StyleSettings.ARMY_MOVEMENT_DIALOG_MIN_IMAGE_BUTTON_STYLE,
                            ImageButton.ImageButtonStyle.class);
  }

  public ImageButton createArmyMovementDialogMinusButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementDialogMinusButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createArmyMovementDialogMinusButtonStyle ()
  {
    return getSkinResource (StyleSettings.ARMY_MOVEMENT_DIALOG_MINUS_IMAGE_BUTTON_STYLE,
                            ImageButton.ImageButtonStyle.class);
  }

  public ImageButton createArmyMovementDialogPlusButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementDialogPlusButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createArmyMovementDialogPlusButtonStyle ()
  {
    return getSkinResource (StyleSettings.ARMY_MOVEMENT_DIALOG_PLUS_IMAGE_BUTTON_STYLE,
                            ImageButton.ImageButtonStyle.class);
  }

  public ImageButton createArmyMovementDialogMaxButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementDialogMaxButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createArmyMovementDialogMaxButtonStyle ()
  {
    return getSkinResource (StyleSettings.ARMY_MOVEMENT_DIALOG_MAX_IMAGE_BUTTON_STYLE,
                            ImageButton.ImageButtonStyle.class);
  }

  public BattleDialog createAttackDialog (final Stage stage,
                                          final GameRules rules,
                                          final ScreenShaker screenShaker,
                                          final AttackDialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (screenShaker, "screenShaker");
    Arguments.checkIsNotNull (listener, "listener");

    return new AttackDialog (new AttackDialogWidgetFactory (getAssetManager (), rules.getAbsoluteAttackerDieRange (),
            rules.getAbsoluteDefenderDieRange ()), stage, screenShaker, listener);
  }

  public BattleDialog createDefendDialog (final Stage stage,
                                          final GameRules rules,
                                          final ScreenShaker screenShaker,
                                          final DefendDialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (screenShaker, "screenShaker");
    Arguments.checkIsNotNull (listener, "listener");

    return new DefendDialog (new DefendDialogWidgetFactory (getAssetManager (), rules.getAbsoluteAttackerDieRange (),
            rules.getAbsoluteDefenderDieRange ()), stage, screenShaker, listener);
  }

  public BattleResultDialog createAttackerBattleResultDialog (final Stage stage, final DialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new AttackerBattleResultDialog (this, DialogStyle.builder ()
            .windowStyle (StyleSettings.BATTLE_RESULT_DIALOG_WINDOW_STYLE).modal (false).movable (true)
            .position (587, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 284).size (650, 244).titleHeight (51).border (28)
            .buttonSpacing (16).buttonWidth (90).textBoxPaddingHorizontal (2).textBoxPaddingBottom (21)
            .textPaddingHorizontal (4).textPaddingBottom (4).build (), stage, listener);
  }

  public BattleResultDialog createDefenderBattleResultDialog (final Stage stage, final DialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new DefenderBattleResultDialog (this, DialogStyle.builder ()
            .windowStyle (StyleSettings.BATTLE_RESULT_DIALOG_WINDOW_STYLE).modal (false).movable (true)
            .position (587, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 284).size (650, 244).titleHeight (51).border (28)
            .buttonSpacing (16).buttonWidth (90).textBoxPaddingHorizontal (2).textBoxPaddingBottom (21)
            .textPaddingHorizontal (4).textPaddingBottom (4).build (), stage, listener);
  }

  public Sound createBattleSingleExplosionSound ()
  {
    return getAsset (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BATTLE_SINGLE_EXPLOSION_SOUND_ASSET_DESCRIPTOR);
  }

  public Label createIntelBoxTitleLabel (final String titleText)
  {
    Arguments.checkIsNotNull (titleText, "titleText");

    return createLabel (titleText, Align.left, createIntelBoxTitleLabelStyle ());
  }

  public Label.LabelStyle createIntelBoxTitleLabelStyle ()
  {
    return createLabelStyle (StyleSettings.INTEL_BOX_TITLE_LABEL_STYLE);
  }

  public Label createIntelBoxSettingNameLabel (final String settingNameText)
  {
    Arguments.checkIsNotNull (settingNameText, "settingNameText");

    return createLabel (settingNameText, Align.left, createIntelBoxSettingNameLabelStyle ());
  }

  public Label createIntelBoxSettingNameLabel (final String settingNameText, final int alignment)
  {
    Arguments.checkIsNotNull (settingNameText, "settingNameText");
    Arguments.checkIsNotNegative (alignment, "alignment");

    return createLabel (settingNameText, alignment, createIntelBoxSettingNameLabelStyle ());
  }

  public Label.LabelStyle createIntelBoxSettingNameLabelStyle ()
  {
    return createLabelStyle (StyleSettings.INTEL_BOX_SETTING_NAME_LABEL_STYLE);
  }

  public Label createIntelBoxSettingTextLabel (final String settingText)
  {
    Arguments.checkIsNotNull (settingText, "settingText");

    return createLabel (settingText, Align.left, createIntelBoxSettingTextLabelStyle ());
  }

  public Label createIntelBoxSettingTextLabel (final String settingText, final int alignment)
  {
    Arguments.checkIsNotNull (settingText, "settingText");
    Arguments.checkIsNotNegative (alignment, "alignment");

    return createLabel (settingText, alignment, createIntelBoxSettingTextLabelStyle ());
  }

  public Label.LabelStyle createIntelBoxSettingTextLabelStyle ()
  {
    return createLabelStyle (StyleSettings.INTEL_BOX_SETTING_TEXT_LABEL_STYLE);
  }

  public Label createIntelBoxSettingTextWrappingLabel (final String settingText)
  {
    Arguments.checkIsNotNull (settingText, "settingText");

    return createWrappingLabel (settingText, Align.left, createIntelBoxSettingTextWrappingLabelStyle ());
  }

  public Label.LabelStyle createIntelBoxSettingTextWrappingLabelStyle ()
  {
    return createLabelStyle (StyleSettings.INTEL_BOX_SETTING_TEXT_LABEL_STYLE);
  }

  public Label createIntelBoxButtonTextLabel (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");

    return createLabel (buttonText, Align.left, createIntelBoxButtonTextLabelStyle ());
  }

  public Label.LabelStyle createIntelBoxButtonTextLabelStyle ()
  {
    return createLabelStyle (StyleSettings.INTEL_BOX_BUTTON_TEXT_LABEL_STYLE);
  }

  public ImageButton createIntelBoxDetailedReportButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createIntelBoxDetailedReportButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createIntelBoxDetailedReportButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.INTEL_BOX_DETAILED_REPORT_IMAGE_BUTTON_STYLE);
  }

  public Label createControlRoomBoxTitleLabel (final String titleText)
  {
    Arguments.checkIsNotNull (titleText, "titleText");

    return createLabel (titleText, Align.left, createControlRoomBoxTitleLabelStyle ());
  }

  public Label.LabelStyle createControlRoomBoxTitleLabelStyle ()
  {
    return createLabelStyle (StyleSettings.CONTROL_ROOM_BOX_TITLE_LABEL_STYLE);
  }

  public Label createControlRoomBoxButtonTextLabel (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");

    return createLabel (buttonText, Align.left, createControlRoomBoxButtonTextLabelStyle ());
  }

  public Label.LabelStyle createControlRoomBoxButtonTextLabelStyle ()
  {
    return createLabelStyle (StyleSettings.CONTROL_ROOM_BOX_BUTTON_TEXT_LABEL_STYLE);
  }

  public ImageButton createControlRoomBoxTradeInButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createControlRoomBoxTradeInButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createControlRoomBoxTradeInButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.CONTROL_ROOM_BOX_TRADE_IN_IMAGE_BUTTON_STYLE);
  }

  public ImageButton createControlRoomBoxFortifyButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createControlRoomBoxFortifyButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createControlRoomBoxFortifyButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.CONTROL_ROOM_BOX_FORTIFY_IMAGE_BUTTON_STYLE);
  }

  public ImageButton createControlRoomBoxEndTurnButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createControlRoomBoxEndTurnButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createControlRoomBoxEndTurnButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.CONTROL_ROOM_BOX_END_TURN_IMAGE_BUTTON_STYLE);
  }

  public ImageButton createControlRoomBoxMySettingsButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createControlRoomBoxMySettingsButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createControlRoomBoxMySettingsButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.CONTROL_ROOM_BOX_MY_SETTINGS_IMAGE_BUTTON_STYLE);
  }

  public ImageButton createControlRoomBoxSurrenderButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createControlRoomBoxSurrenderButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createControlRoomBoxSurrenderButtonStyle ()
  {
    return createImageButtonStyle (StyleSettings.CONTROL_ROOM_BOX_SURRENDER_IMAGE_BUTTON_STYLE);
  }
}
