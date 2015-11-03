package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.PlayerBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.ScrollbarStyle;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import java.util.regex.Pattern;

import net.engio.mbassy.bus.MBassador;

public interface WidgetFactory
{
  TextButton createTextButton (final String text, final String style, final EventListener listener);

  TextButton createTextButton (final String text, final String style);

  TextButton.TextButtonStyle createTextButtonStyle (final String styleName);

  ImageButton createImageButton (final String styleName, final EventListener listener);

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

  MessageBox <Message> createPopupMessageBox (final String scrollPaneStyleName,
                                              final String rowLabelStyleName,
                                              final int rowLabelAlignment,
                                              final ScrollbarStyle scrollbarStyle);

  MessageBox <StatusMessage> createStatusBox (final String scrollPaneStyle);

  MessageBox <ChatMessage> createChatBox (final String scrollPaneStyle,
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
}
