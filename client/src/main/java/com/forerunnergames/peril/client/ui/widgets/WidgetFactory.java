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

package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox.PersonBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox.PlayerPersonBoxRow;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox.SpectatorPersonBoxRow;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowHighlighting;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.messagebox.ScrollbarStyle;
import com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.statusbox.StatusBoxRow;
import com.forerunnergames.peril.client.ui.widgets.personicons.players.PlayerIcon;
import com.forerunnergames.peril.client.ui.widgets.personicons.spectators.SpectatorIcon;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import java.util.regex.Pattern;

import net.engio.mbassy.bus.MBassador;

public interface WidgetFactory
{
  Button createButton (final Button.ButtonStyle style);

  Button createButton (final Button.ButtonStyle style, final EventListener listener);

  Button createButton (final String styleName);

  Button createButton (final String styleName, final EventListener listener);

  Button.ButtonStyle createButtonStyle (final String styleName);

  Button.ButtonStyle createButtonStyle (final String styleName, final Class <? extends Button.ButtonStyle> styleType);

  TextButton createTextButton (final String text, final String style, final EventListener listener);

  TextButton createTextButton (final String text, final String style);

  TextButton.TextButtonStyle createTextButtonStyle (final String styleName);

  ImageButton createImageButton (final String styleName, final EventListener listener);

  ImageButton createImageButton (final ImageButton.ImageButtonStyle style);

  ImageButton createImageButton (final ImageButton.ImageButtonStyle style, final EventListener listener);

  ImageButton.ImageButtonStyle createImageButtonStyle (final String styleName);

  Dialog createMessageDialog (final WidgetFactory widgetFactory, final Stage stage, final DialogListener listener);

  Dialog createQuitDialog (final String message, final Stage stage, final CancellableDialogListener listener);

  Label createLabel (final String text, final int alignment, final String labelStyle);

  Label createLabel (final String text, final int alignment, final Label.LabelStyle labelStyle);

  Label createWrappingLabel (final String text, final int alignment, final String labelStyle);

  Label createWrappingLabel (final String text, final int alignment, final Label.LabelStyle labelStyle);

  Label.LabelStyle createLabelStyle (final String styleName);

  TextField createTextField (final String initialText,
                             final int maxLength,
                             final Pattern filter,
                             final TextField.TextFieldStyle style);

  TextField createTextField (final String initialText, final int maxLength, final Pattern filter, final String style);

  TextField createTextField (final int maxLength, final Pattern filter, final String style);

  TextField.TextFieldStyle createTextFieldStyle (final String styleName);

  ScrollPane.ScrollPaneStyle createScrollPaneStyle (final String scrollPaneStyleName,
                                                    final ScrollbarStyle scrollbarStyle);

  CheckBox createCheckBox (final CheckBox.CheckBoxStyle style, final EventListener listener);

  <T> SelectBox <T> createSelectBox (final SelectBox.SelectBoxStyle style);

  SelectBox.SelectBoxStyle createSelectBoxStyle (final String styleName);

  List.ListStyle createListStyle (String styleName);

  Cursor createNormalCursor ();

  Dialog createErrorDialog (final Stage stage, final DialogListener listener);

  Dialog createErrorDialog (final Stage stage, final String submitButtonText, final DialogListener listener);

  MessageBox <MessageBoxRow <Message>> createDialogMessageBox (final String scrollPaneStyle,
                                                               final String rowLabelStyle,
                                                               final int rowLabelAlignment,
                                                               final ScrollbarStyle scrollbarStyle);

  MessageBox <StatusBoxRow> createStatusBox ();

  MessageBox <ChatBoxRow> createChatBox (final MBassador <Event> eventBus);

  PersonBox createPersonBox ();

  ProgressBar createHorizontalProgressBar (final float min, final float max, final float stepSize, final String style);

  ProgressBar createVerticalProgressBar (final float min, final float max, final float stepSize, final String style);

  Slider createHorizontalSlider (final int min,
                                 final int max,
                                 final int sliderStepSize,
                                 final String style,
                                 final EventListener listener);

  Slider createHorizontalSlider (final int min,
                                 final int max,
                                 final int sliderStepSize,
                                 final Slider.SliderStyle style,
                                 final EventListener listener);

  Slider createVerticalSlider (final int min,
                               final int max,
                               final int sliderStepSize,
                               final String style,
                               final EventListener listener);

  Slider createVerticalSlider (final int min,
                               final int max,
                               final int sliderStepSize,
                               final Slider.SliderStyle style,
                               final EventListener listener);

  Slider.SliderStyle createSliderStyle (final String styleName);

  Window.WindowStyle createWindowStyle (final String styleName);

  BitmapFont createBitmapFont (final String fontName);

  TextureRegion createTextureRegion (final String name);

  Sprite createSpriteFromTextureRegion (final String regionName);

  NinePatch createNinePatchFromTextureRegion (final String regionName);

  TiledDrawable createTiledDrawableFromTextureRegion (final String regionName);

  ChatBoxRow createChatMessageBoxRow (final ChatMessage message);

  StatusBoxRow createStatusMessageBoxRow (final StatusMessage message);

  PlayerPersonBoxRow createPlayerPersonBoxRow (final PlayerPacket player);

  SpectatorPersonBoxRow createSpectatorPersonBoxRow (final SpectatorPacket spectator);

  PlayerIcon createPlayerIcon (final PlayerPacket player);

  SpectatorIcon createSpectatorIcon (final SpectatorPacket spectator);

  MessageBoxRow <Message> createMessageBoxRow (final Message message, final MessageBoxRowStyle rowStyle);

  MessageBoxRowHighlighting createMessageBoxRowHighlighting ();

  Drawable createMessageBoxRowHighlightingDrawable ();

  Drawable createChatBoxBackgroundDrawable ();
}
