package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
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
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.PlayerBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.messagebox.ScrollbarStyle;
import com.forerunnergames.peril.client.ui.widgets.popup.ErrorPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.QuitPopup;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public abstract class AbstractWidgetFactory implements WidgetFactory
{
  private static final int DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT = Align.left;
  private static final int DEFAULT_MESSAGE_BOX_ROW_HEIGHT = 24;
  private static final int DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT = 12;
  private static final int DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT = 12;
  private static final int DEFAULT_MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH = 14;
  private static final int DEFAULT_MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT = 14;
  private static final int DEFAULT_SELECT_BOX_HORIZONTAL_SCROLLBAR_HEIGHT = 14;
  private static final int DEFAULT_SELECT_BOX_VERTICAL_SCROLLBAR_WIDTH = 14;
  private static final MessageBoxRowStyle STATUS_BOX_ROW_STYLE = new MessageBoxRowStyle ("status-box-message",
          DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT, DEFAULT_MESSAGE_BOX_ROW_HEIGHT, DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT,
          DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT);
  private static final MessageBoxRowStyle CHATBOX_ROW_STYLE = new MessageBoxRowStyle ("chatbox-message",
          DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT, DEFAULT_MESSAGE_BOX_ROW_HEIGHT, DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT,
          DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT);
  private static final MessageBoxRowStyle PLAYER_BOX_ROW_STYLE = new MessageBoxRowStyle ("player-box-message",
          DEFAULT_MESSAGE_BOX_ROW_ALIGNMENT, DEFAULT_MESSAGE_BOX_ROW_HEIGHT, DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT,
          DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT);
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
  public final ImageButton createImageButton (final ImageButton.ImageButtonStyle style, final EventListener listener)
  {
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageButton imageButton = new ImageButton (style);
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
  public final Popup createQuitPopup (final String message, final Stage stage, final PopupListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new QuitPopup (this, message, stage, listener);
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
    Arguments.checkIsNotNull (styleName, "labelStyle");

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
  public Popup createErrorPopup (final Stage stage, final PopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new ErrorPopup (this, stage, listener);
  }

  @Override
  public Popup createErrorPopup (final Stage stage, final String submitButtonText, final PopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (submitButtonText, "submitButtonText");
    Arguments.checkIsNotNull (listener, "listener");

    return new ErrorPopup (this, stage, submitButtonText, listener);
  }

  @Override
  public MessageBox <Message> createPopupMessageBox (final String scrollPaneStyleName,
                                                     final String rowLabelStyleName,
                                                     final int rowLabelAlignment,
                                                     final ScrollbarStyle scrollbarStyle)
  {
    Arguments.checkIsNotNull (scrollPaneStyleName, "scrollPaneStyleName");
    Arguments.checkIsNotNull (rowLabelStyleName, "rowLabelStyleName");
    Arguments.checkIsNotNull (rowLabelAlignment, "rowLabelAlignment");
    Arguments.checkIsNotNull (scrollbarStyle, "scrollbarStyle");

    return new DefaultMessageBox <> (this, scrollPaneStyleName, scrollbarStyle,
            new MessageBoxRowStyle (rowLabelStyleName, rowLabelAlignment, DEFAULT_MESSAGE_BOX_ROW_HEIGHT,
                    DEFAULT_MESSAGE_BOX_ROW_PADDING_LEFT, DEFAULT_MESSAGE_BOX_ROW_PADDING_RIGHT));
  }

  @Override
  public MessageBox <StatusMessage> createStatusBox (final String scrollPaneStyle)
  {
    Arguments.checkIsNotNull (scrollPaneStyle, "scrollPaneStyle");

    return new DefaultMessageBox <> (this, scrollPaneStyle, DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE, STATUS_BOX_ROW_STYLE);
  }

  @Override
  public MessageBox <ChatMessage> createChatBox (final String scrollPaneStyle,
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

    return new PlayerBox (new DefaultMessageBox <> (this, scrollPaneStyle, DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE,
            PLAYER_BOX_ROW_STYLE));
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
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final Slider slider = new Slider (min, max, sliderStepSize, false, createSliderStyle (style));
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
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final Slider slider = new Slider (min, max, sliderStepSize, true, createSliderStyle (style));
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

  protected final <T> T getAsset (final AssetDescriptor <T> assetDescriptor)
  {
    Arguments.checkIsNotNull (assetDescriptor, "assetDescriptor");

    return assetManager.get (assetDescriptor);
  }

  protected final <T> T getSkinResource (final String styleName, final Class <T> type)
  {
    Arguments.checkIsNotNull (styleName, "styleName");
    Arguments.checkIsNotNull (type, "type");

    return getSkin ().get (styleName, type);
  }

  protected abstract AssetDescriptor <Skin> getSkinAssetDescriptor ();

  protected final Skin getSkin ()
  {
    return getAsset (getSkinAssetDescriptor ());
  }
}
