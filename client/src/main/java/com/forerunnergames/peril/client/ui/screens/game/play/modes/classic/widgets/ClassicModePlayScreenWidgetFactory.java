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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.playerbox.PlayerBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle.attack.AttackPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle.attack.AttackPopupListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle.BattlePopupListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle.BattlePopupWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle.defend.DefendPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.armymovement.occupation.OccupationPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.quit.PlayScreenQuitPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.armymovement.reinforcement.ReinforcementPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.sidebar.SideBar;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.sidebar.SideBarButton;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.popup.OkPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupStyle;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ClassicModePlayScreenWidgetFactory extends AbstractWidgetFactory
{
  private static final int ARMY_MOVEMENT_POPUP_SLIDER_STEP_SIZE = 1;
  private final PlayMapFactory playMapFactory;
  private final BattlePopupWidgetFactory battlePopupWidgetFactory;

  public ClassicModePlayScreenWidgetFactory (final AssetManager assetManager,
                                             final PlayMapFactory playMapFactory,
                                             final BattlePopupWidgetFactory battlePopupWidgetFactory)
  {
    super (assetManager);

    Arguments.checkIsNotNull (playMapFactory, "playMapFactory");
    Arguments.checkIsNotNull (battlePopupWidgetFactory, "battlePopupWidgetFactory");

    this.playMapFactory = playMapFactory;
    this.battlePopupWidgetFactory = battlePopupWidgetFactory;
  }

  @Override
  protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
  {
    return AssetSettings.CLASSIC_MODE_PLAY_SCREEN_SKIN_ASSET_DESCRIPTOR;
  }

  public Image createBackgroundImage ()
  {
    return new Image (createBackgroundImageDrawable ());
  }

  public Drawable createBackgroundImageDrawable ()
  {
    return new TextureRegionDrawable (
            new TextureRegion (getAsset (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BACKGROUND_ASSET_DESCRIPTOR)));
  }

  public MessageBox <StatusMessage> createStatusBox ()
  {
    return createStatusBox ("default");
  }

  public MessageBox <ChatMessage> createChatBox (final MBassador <Event> eventBus)
  {
    return createChatBox ("default", "default", eventBus);
  }

  public PlayerBox createPlayerBox ()
  {
    return createPlayerBox ("default");
  }

  public SideBar createSideBar (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new SideBar (this, eventBus);
  }

  public SideBarButton createSideBarButton (final SideBarButton.ButtonType buttonType, final EventListener listener)
  {
    Arguments.checkIsNotNull (buttonType, "buttonType");
    Arguments.checkIsNotNull (listener, "listener");

    return new SideBarButton (createImageButton (createSideBarButtonStyle (buttonType), listener), buttonType);
  }

  public ImageButton.ImageButtonStyle createSideBarButtonStyle (final SideBarButton.ButtonType buttonType)
  {
    Arguments.checkIsNotNull (buttonType, "buttonType");

    return getSkinResource (buttonType.getStyleName (), ImageButton.ImageButtonStyle.class);
  }

  public ReinforcementPopup createReinforcementPopup (final Stage stage,
                                                      final MBassador <Event> eventBus,
                                                      final PopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    return new ReinforcementPopup (this, stage, listener, eventBus);
  }

  public OccupationPopup createOccupationPopup (final Stage stage,
                                                final MBassador <Event> eventBus,
                                                final PopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    return new OccupationPopup (this, stage, listener, eventBus);
  }

  public Slider createArmyMovementPopupSlider (final ChangeListener changeListener)
  {
    Arguments.checkIsNotNull (changeListener, "changeListener");

    return createHorizontalSlider (0, 0, ARMY_MOVEMENT_POPUP_SLIDER_STEP_SIZE, "default-horizontal", changeListener);
  }

  public Slider.SliderStyle createArmyMovementPopupSliderStyle ()
  {
    return createSliderStyle ("default-horizontal");
  }

  public ImageButton createArmyMovementPopupMinButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementPopupMinButtonStyle (), listener);
  }

  public Label createArmyMovementPopupCountryNameLabel ()
  {
    return createLabel ("", Align.center, createArmyMovementPopupCountryNameLabelStyle ());
  }

  public Label.LabelStyle createArmyMovementPopupCountryNameLabelStyle ()
  {
    return createLabelStyle ("army-movement-popup-country-name");
  }

  public ImageButton.ImageButtonStyle createArmyMovementPopupMinButtonStyle ()
  {
    return getSkinResource ("min", ImageButton.ImageButtonStyle.class);
  }

  public ImageButton createArmyMovementPopupMinusButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementPopupMinusButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createArmyMovementPopupMinusButtonStyle ()
  {
    return getSkinResource ("minus", ImageButton.ImageButtonStyle.class);
  }

  public ImageButton createArmyMovementPopupPlusButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementPopupPlusButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createArmyMovementPopupPlusButtonStyle ()
  {
    return getSkinResource ("plus", ImageButton.ImageButtonStyle.class);
  }

  public ImageButton createArmyMovementPopupMaxButton (final ClickListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (createArmyMovementPopupMaxButtonStyle (), listener);
  }

  public ImageButton.ImageButtonStyle createArmyMovementPopupMaxButtonStyle ()
  {
    return getSkinResource ("max", ImageButton.ImageButtonStyle.class);
  }

  public AttackPopup createAttackPopup (final Stage stage,
                                        final MBassador <Event> eventBus,
                                        final AttackPopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    return new AttackPopup (battlePopupWidgetFactory, stage, listener, eventBus);
  }

  public DefendPopup createDefendPopup (final Stage stage,
                                        final MBassador <Event> eventBus,
                                        final BattlePopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    return new DefendPopup (battlePopupWidgetFactory, stage, listener, eventBus);
  }

  public void destroyPlayMap (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    playMapFactory.destroy (mapMetadata);
  }

  public OkPopup createBattleResultPopup (final Stage stage, final PopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");

    return new OkPopup (this,
            PopupStyle.builder ().windowStyle ("popup-non-modal").modal (false).movable (true)
                    .position (587, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 284).border (28).buttonSpacing (16)
                    .buttonWidth (90).textPadding (16).textBoxPaddingBottom (20).build (),
            stage, listener);
  }

  public Popup createQuitPopup (final Stage stage, final PopupListener listener)
  {
    return new PlayScreenQuitPopup (this,
            "Are you sure you want to quit?\nIf you are the host, quitting will end the game for everyone.", stage,
            listener);
  }

  public Sound createBattleSingleExplosionSound ()
  {
    return getAsset (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BATTLE_SINGLE_EXPLOSION_SOUND_ASSET_DESCRIPTOR);
  }
}
