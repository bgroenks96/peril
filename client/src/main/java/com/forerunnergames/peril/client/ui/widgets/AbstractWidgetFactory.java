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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.ErrorDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.QuitDialog;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.LabelMessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.MessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.MessageBoxRowHighlighting;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.ScrollbarStyle;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.chatbox.ChatBox;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.playerbox.PlayerBox;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.playerbox.PlayerBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.statusbox.StatusBoxRow;
import com.forerunnergames.peril.client.ui.widgets.playercoloricons.PlayerColorIcon;
import com.forerunnergames.peril.client.ui.widgets.playercoloricons.PlayerColorIconWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.playercoloricons.PlayerColorIconWidgetFactoryCreator;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public abstract class AbstractWidgetFactory implements WidgetFactory
{
  private static final String MESSAGE_BOX_ROW_HIGHLIGHTING_DRAWABLE_NAME = "message-box-row-highlighting";
  private static final String CHATBOX_BACKGROUND_DRAWABLE_NAME = "chat-box-borders";
  private static final int DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT = Align.left;
  private static final int DEFAULT_MESSAGE_BOX_ROW_HEIGHT = 24;
  private static final int DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT = 14;
  private static final int DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT = 14;
  private static final int DEFAULT_MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH = 14;
  private static final int DEFAULT_MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT = 14;
  private static final int DEFAULT_SELECT_BOX_HORIZONTAL_SCROLLBAR_HEIGHT = 14;
  private static final int DEFAULT_SELECT_BOX_VERTICAL_SCROLLBAR_WIDTH = 14;
  private static final MessageBoxRowStyle STATUS_BOX_ROW_STYLE = new MessageBoxRowStyle (
          StyleSettings.STATUS_BOX_ROW_LABEL_STYLE, DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT, DEFAULT_MESSAGE_BOX_ROW_HEIGHT,
          DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT, DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT);
  private static final MessageBoxRowStyle CHATBOX_ROW_STYLE = new MessageBoxRowStyle (
          StyleSettings.CHAT_BOX_ROW_LABEL_STYLE, DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT, DEFAULT_MESSAGE_BOX_ROW_HEIGHT,
          DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT, DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT);
  private static final MessageBoxRowStyle PLAYER_BOX_ROW_STYLE = new MessageBoxRowStyle (
          StyleSettings.PLAYER_BOX_ROW_LABEL_STYLE, DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT, DEFAULT_MESSAGE_BOX_ROW_HEIGHT,
          0, DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT);
  private static final ScrollbarStyle DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE = new ScrollbarStyle (
          ScrollbarStyle.Scrollbars.REQUIRED, DEFAULT_MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT,
          DEFAULT_MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH);
  private static final ScrollbarStyle DEFAULT_SELECT_BOX_SCROLLBAR_STYLE = new ScrollbarStyle (
          ScrollbarStyle.Scrollbars.OPTIONAL, DEFAULT_SELECT_BOX_HORIZONTAL_SCROLLBAR_HEIGHT,
          DEFAULT_SELECT_BOX_VERTICAL_SCROLLBAR_WIDTH);
  private final AssetManager assetManager;
  @Nullable
  private Cursor normalCursor = null;

  protected AbstractWidgetFactory (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public Button createButton (final Button.ButtonStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    return new Button (style);
  }

  @Override
  public Button createButton (final Button.ButtonStyle style, final EventListener listener)
  {
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final Button button = createButton (style);
    button.addListener (listener);

    return button;
  }

  @Override
  public Button createButton (final String styleName)
  {
    return createButton (createButtonStyle (styleName));
  }

  @Override
  public Button createButton (final String styleName, final EventListener listener)
  {
    return createButton (createButtonStyle (styleName), listener);
  }

  @Override
  public Button.ButtonStyle createButtonStyle (final String styleName)
  {
    return getSkinResource (styleName, Button.ButtonStyle.class);
  }

  @Override
  public Button.ButtonStyle createButtonStyle (final String styleName,
                                               final Class <? extends Button.ButtonStyle> styleType)

  {
    return getSkinResource (styleName, styleType);
  }

  @Override
  public final TextButton createTextButton (final String text, final String style, final EventListener listener)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final TextButton textButton = createTextButton (text, style);
    textButton.addListener (listener);

    return textButton;
  }

  @Override
  public TextButton createTextButton (final String text, final String style)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (style, "style");

    return new TextButton (text, createTextButtonStyle (style));
  }

  @Override
  public TextButton.TextButtonStyle createTextButtonStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, TextButton.TextButtonStyle.class);
  }

  @Override
  public final ImageButton createImageButton (final String styleName, final EventListener listener)
  {
    Arguments.checkIsNotNull (styleName, "styleName");
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (getSkin ().get (styleName, ImageButton.ImageButtonStyle.class), listener);
  }

  @Override
  public ImageButton createImageButton (final ImageButton.ImageButtonStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    return new ImageButton (style);
  }

  @Override
  public final ImageButton createImageButton (final ImageButton.ImageButtonStyle style, final EventListener listener)
  {
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageButton imageButton = createImageButton (style);
    imageButton.addListener (listener);

    return imageButton;
  }

  @Override
  public ImageButton.ImageButtonStyle createImageButtonStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, ImageButton.ImageButtonStyle.class);
  }

  @Override
  public Dialog createQuitDialog (final String message, final Stage stage, final DialogListener listener)
  {
    return new QuitDialog (this, message, stage, listener);
  }

  @Override
  public final Label createLabel (final String text, final int alignment, final String labelStyle)
  {
    return createLabel (text, alignment, getSkinResource (labelStyle, Label.LabelStyle.class));
  }

  @Override
  public Label createLabel (final String text, final int alignment, final Label.LabelStyle labelStyle)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    final Label label = new Label (text, labelStyle);
    label.setAlignment (alignment);

    return label;
  }

  @Override
  public final Label createWrappingLabel (final String text, final int alignment, final String labelStyle)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    return createWrappingLabel (text, alignment, getSkinResource (labelStyle, Label.LabelStyle.class));
  }

  @Override
  public final Label createWrappingLabel (final String text, final int alignment, final Label.LabelStyle labelStyle)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    final Label label = createLabel (text, alignment, labelStyle);
    label.setWrap (true);

    return label;
  }

  @Override
  public Label.LabelStyle createLabelStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, Label.LabelStyle.class);
  }

  @Override
  public final TextField createTextField (final String initialText,
                                          final int maxLength,
                                          final Pattern filter,
                                          final TextField.TextFieldStyle style)
  {
    Arguments.checkIsNotNull (initialText, "initialText");
    Arguments.checkIsNotNegative (maxLength, "maxLength");
    Arguments.checkIsNotNull (filter, "filter");
    Arguments.checkIsNotNull (style, "style");

    final TextField textField = new TextField (initialText, style)
    {
      @Override
      protected InputListener createInputListener ()
      {
        return new TextFieldClickListener ()
        {
          @Override
          public boolean keyDown (final InputEvent event, final int keycode)
          {
            return doNotHandleEscapeKeyInTextField (event, keycode);
          }

          private boolean doNotHandleEscapeKeyInTextField (final InputEvent event, final int keycode)
          {
            return keycode != Input.Keys.ESCAPE && super.keyDown (event, keycode);
          }
        };
      }
    };

    final Matcher matcher = filter.matcher ("").reset ();

    textField.setTextFieldFilter (new TextField.TextFieldFilter ()
    {
      @Override
      public boolean acceptChar (final TextField textField, final char c)
      {
        return matcher.reset (String.valueOf (c)).matches ();
      }
    });

    textField.setMaxLength (maxLength);

    return textField;
  }

  @Override
  public final TextField createTextField (final String initialText,
                                          final int maxLength,
                                          final Pattern filter,
                                          final String style)
  {
    return createTextField (initialText, maxLength, filter, createTextFieldStyle (style));
  }

  @Override
  public final TextField createTextField (final int maxLength, final Pattern filter, final String style)
  {
    return createTextField ("", maxLength, filter, createTextFieldStyle (style));
  }

  @Override
  public TextField.TextFieldStyle createTextFieldStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, TextField.TextFieldStyle.class);
  }

  @Override
  public ScrollPane.ScrollPaneStyle createScrollPaneStyle (final String scrollPaneStyleName,
                                                           final ScrollbarStyle scrollbarStyle)
  {
    Arguments.checkIsNotNull (scrollPaneStyleName, "scrollPaneStyleName");
    Arguments.checkIsNotNull (scrollbarStyle, "scrollbarStyle");

    final ScrollPane.ScrollPaneStyle style = getSkinResource (scrollPaneStyleName, ScrollPane.ScrollPaneStyle.class);

    if (style.hScrollKnob != null) style.hScrollKnob.setMinHeight (scrollbarStyle.getHorizontalHeight ());
    if (style.vScrollKnob != null) style.vScrollKnob.setMinWidth (scrollbarStyle.getVerticalWidth ());

    return style;
  }

  @Override
  public final CheckBox createCheckBox (final CheckBox.CheckBoxStyle style, final EventListener listener)
  {
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final CheckBox checkBox = new CheckBox ("", style);
    checkBox.addListener (listener);

    return checkBox;
  }

  @Override
  public final <T> SelectBox <T> createSelectBox (final SelectBox.SelectBoxStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    return new SelectBox <> (style);
  }

  @Override
  public SelectBox.SelectBoxStyle createSelectBoxStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, SelectBox.SelectBoxStyle.class);
  }

  @Override
  public List.ListStyle createListStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, List.ListStyle.class);
  }

  @Override
  public Cursor createNormalCursor ()
  {
    if (normalCursor != null) return normalCursor;

    normalCursor = Gdx.graphics.newCursor (getAsset (AssetSettings.NORMAL_CURSOR_ASSET_DESCRIPTOR),
                                           Math.round (InputSettings.NORMAL_MOUSE_CURSOR_HOTSPOT.x),
                                           Math.round (InputSettings.NORMAL_MOUSE_CURSOR_HOTSPOT.y));

    return normalCursor;
  }

  @Override
  public Dialog createErrorDialog (final Stage stage, final DialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new ErrorDialog (this, stage, listener);
  }

  @Override
  public Dialog createErrorDialog (final Stage stage, final String submitButtonText, final DialogListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (submitButtonText, "submitButtonText");
    Arguments.checkIsNotNull (listener, "listener");

    return new ErrorDialog (this, stage, submitButtonText, listener);
  }

  @Override
  public MessageBox <MessageBoxRow <Message>> createDialogMessageBox (final String scrollPaneStyleName,
                                                                      final String rowLabelStyleName,
                                                                      final int rowLabelAlignment,
                                                                      final ScrollbarStyle scrollbarStyle)
  {
    Arguments.checkIsNotNull (scrollPaneStyleName, "scrollPaneStyleName");
    Arguments.checkIsNotNull (rowLabelStyleName, "rowLabelStyleName");
    Arguments.checkIsNotNull (rowLabelAlignment, "rowLabelAlignment");
    Arguments.checkIsNotNull (scrollbarStyle, "scrollbarStyle");

    return new DefaultMessageBox <> (this, scrollPaneStyleName, scrollbarStyle, new MessageBoxRowStyle (
            rowLabelStyleName, rowLabelAlignment, DEFAULT_MESSAGE_BOX_ROW_HEIGHT, DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT,
            DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT), new Padding (0, 0, 2, 2), new Padding (0, 0, 6, 6));
  }

  @Override
  public MessageBox <StatusBoxRow> createStatusBox (final String scrollPaneStyle)
  {
    Arguments.checkIsNotNull (scrollPaneStyle, "scrollPaneStyle");

    return new DefaultMessageBox <> (this, scrollPaneStyle, DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE, STATUS_BOX_ROW_STYLE,
            new Padding (0, 0, 2, 2), new Padding (0, 0, 6, 6));
  }

  @Override
  public MessageBox <ChatBoxRow> createChatBox (final String scrollPaneStyle,
                                                final String textFieldStyle,
                                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (scrollPaneStyle, "scrollPaneStyle");
    Arguments.checkIsNotNull (textFieldStyle, "textFieldStyle");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new ChatBox (this, scrollPaneStyle, DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE, CHATBOX_ROW_STYLE, textFieldStyle,
            eventBus);
  }

  @Override
  public PlayerBox createPlayerBox (final String scrollPaneStyle)
  {
    Arguments.checkIsNotNull (scrollPaneStyle, "scrollPaneStyle");

    return new PlayerBox (this, scrollPaneStyle, DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE, PLAYER_BOX_ROW_STYLE);
  }

  @Override
  public ProgressBar createHorizontalProgressBar (final float min,
                                                  final float max,
                                                  final float stepSize,
                                                  final String style)
  {
    Arguments.checkIsNotNull (style, "style");

    return new ProgressBar (min, max, stepSize, false, getSkin (), style);
  }

  @Override
  public ProgressBar createVerticalProgressBar (final float min,
                                                final float max,
                                                final float stepSize,
                                                final String style)
  {
    Arguments.checkIsNotNull (style, "style");

    return new ProgressBar (min, max, stepSize, true, getSkin (), style);
  }

  @Override
  public Slider createHorizontalSlider (final int min,
                                        final int max,
                                        final int sliderStepSize,
                                        final String style,
                                        final EventListener listener)
  {
    return createHorizontalSlider (min, max, sliderStepSize, createSliderStyle (style), listener);
  }

  @Override
  public Slider createHorizontalSlider (final int min,
                                        final int max,
                                        final int sliderStepSize,
                                        final Slider.SliderStyle style,
                                        final EventListener listener)
  {
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final Slider slider = new Slider (min, max, sliderStepSize, false, style);
    slider.addListener (listener);

    return slider;
  }

  @Override
  public Slider createVerticalSlider (final int min,
                                      final int max,
                                      final int sliderStepSize,
                                      final String style,
                                      final EventListener listener)
  {
    return createVerticalSlider (min, max, sliderStepSize, createSliderStyle (style), listener);
  }

  @Override
  public Slider createVerticalSlider (final int min,
                                      final int max,
                                      final int sliderStepSize,
                                      final Slider.SliderStyle style,
                                      final EventListener listener)
  {
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final Slider slider = new Slider (min, max, sliderStepSize, true, style);
    slider.addListener (listener);

    return slider;
  }

  @Override
  public Slider.SliderStyle createSliderStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, Slider.SliderStyle.class);
  }

  @Override
  public Window.WindowStyle createWindowStyle (final String styleName)
  {
    Arguments.checkIsNotNull (styleName, "styleName");

    return getSkinResource (styleName, Window.WindowStyle.class);
  }

  @Override
  public BitmapFont createBitmapFont (final String fontName)
  {
    Arguments.checkIsNotNull (fontName, "fontName");

    return getSkinResource (fontName, BitmapFont.class);
  }

  @Override
  public TextureRegion createTextureRegion (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return getSkin ().getRegion (name);
  }

  @Override
  public Sprite createSpriteFromTextureRegion (final String regionName)
  {
    return getSkin ().getSprite (regionName);
  }

  @Override
  public NinePatch createNinePatchFromTextureRegion (final String regionName)
  {
    return getSkin ().getPatch (regionName);
  }

  @Override
  public TiledDrawable createTiledDrawableFromTextureRegion (final String regionName)
  {
    return getSkin ().getTiledDrawable (regionName);
  }

  @Override
  public ChatBoxRow createChatMessageBoxRow (final ChatMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    return new ChatBoxRow (message, CHATBOX_ROW_STYLE, this);
  }

  @Override
  public StatusBoxRow createStatusMessageBoxRow (final StatusMessage message)
  {
    Arguments.checkIsNotNull (message, "message");

    return new StatusBoxRow (message, STATUS_BOX_ROW_STYLE, this);
  }

  @Override
  public PlayerBoxRow createPlayerBoxRow (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return new PlayerBoxRow (player, PLAYER_BOX_ROW_STYLE, createPlayerColorIconWidgetFactory (player));
  }

  @Override
  public PlayerColorIcon createPlayerColorIcon (final PlayerPacket player)
  {
    return createPlayerColorIconWidgetFactory (player).createPlayerColorIcon (player.getColor ());
  }

  @Override
  public MessageBoxRow <Message> createMessageBoxRow (final Message message, final MessageBoxRowStyle rowStyle)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (rowStyle, "rowStyle");

    return new LabelMessageBoxRow <> (message, rowStyle, this);
  }

  @Override
  public MessageBoxRowHighlighting createMessageBoxRowHighlighting ()
  {
    return new MessageBoxRowHighlighting (new Image (createMessageBoxRowHighlightingDrawable ()), this);
  }

  @Override
  public Drawable createMessageBoxRowHighlightingDrawable ()
  {
    return new TextureRegionDrawable (createTextureRegion (MESSAGE_BOX_ROW_HIGHLIGHTING_DRAWABLE_NAME));
  }

  @Override
  public Drawable createChatBoxBackgroundDrawable ()
  {
    return new NinePatchDrawable (createNinePatchFromTextureRegion (CHATBOX_BACKGROUND_DRAWABLE_NAME));
  }

  protected final <T> T getAsset (final AssetDescriptor <T> assetDescriptor)
  {
    Arguments.checkIsNotNull (assetDescriptor, "assetDescriptor");

    return assetManager.get (assetDescriptor);
  }

  protected final <T> boolean isAssetLoaded (final AssetDescriptor <T> assetDescriptor)
  {
    Arguments.checkIsNotNull (assetDescriptor, "assetDescriptor");

    return assetManager.isLoaded (assetDescriptor);
  }

  protected final <T> T getSkinResource (final String name, final Class <T> type)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (type, "type");

    return getSkin ().get (name, type);
  }

  protected abstract AssetDescriptor <Skin> getSkinAssetDescriptor ();

  protected final Skin getSkin ()
  {
    return getAsset (getSkinAssetDescriptor ());
  }

  private PlayerColorIconWidgetFactory createPlayerColorIconWidgetFactory (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return PlayerColorIconWidgetFactoryCreator.create (player, getSkinAssetDescriptor (), assetManager);
  }
}
