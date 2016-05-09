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

import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.ui.widgets.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowHighlighting;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.messagebox.ScrollbarStyle;
import com.forerunnergames.peril.client.ui.widgets.playerbox.PlayerBox;
import com.forerunnergames.peril.client.ui.widgets.playerbox.PlayerBoxRow;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.statusbox.StatusBoxRow;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
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

  Popup createQuitPopup (final String message, final Stage stage, final PopupListener listener);

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

  Popup createErrorPopup (final Stage stage, final PopupListener listener);

  Popup createErrorPopup (final Stage stage, final String submitButtonText, final PopupListener listener);

  MessageBox <MessageBoxRow <Message>> createPopupMessageBox (final String scrollPaneStyleName,
                                                              final String rowLabelStyleName,
                                                              final int rowLabelAlignment,
                                                              final ScrollbarStyle scrollbarStyle);

  MessageBox <StatusBoxRow> createStatusBox (final String scrollPaneStyle);

  MessageBox <ChatBoxRow> createChatBox (final String scrollPaneStyle,
                                         final String textFieldStyle,
                                         final MBassador <Event> eventBus);

  PlayerBox createPlayerBox (final String scrollPaneStyle);

  ProgressBar createHorizontalProgressBar (final float min, final float max, final float stepSize, final String style);

  ProgressBar createVerticalProgressBar (final float min, final float max, final float stepSize, final String style);

  Slider createHorizontalSlider (final int min,
                                 final int max,
                                 final int sliderStepSize,
                                 final String style,
                                 final EventListener listener);

  Slider createVerticalSlider (final int min,
                               final int max,
                               final int sliderStepSize,
                               final String style,
                               final EventListener listener);

  Slider.SliderStyle createSliderStyle (final String styleName);

  Window.WindowStyle createWindowStyle (final String styleName);

  BitmapFont createBitmapFont (final String fontName);

  TextureRegion createTextureRegion (final String name);

  Sprite createSpriteFromTextureRegion (final String regionName);

  NinePatch createNinePatchFromTextureRegion (final String regionName);

  ChatBoxRow createChatMessageBoxRow (final ChatMessage message);

  StatusBoxRow createStatusMessageBoxRow (final StatusMessage message);

  PlayerBoxRow createPlayerBoxRow (final PlayerPacket player);

  MessageBoxRow <Message> createMessageBoxRow (final Message message, final MessageBoxRowStyle rowStyle);

  MessageBoxRowHighlighting createMessageBoxRowHighlighting ();

  Drawable createMessageBoxRowHighlightingDrawable ();
}
