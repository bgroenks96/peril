package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.QuitPopup;
import com.forerunnergames.tools.common.Arguments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WidgetFactory
{
  private final Skin skin;

  public WidgetFactory (final Skin skin)
  {
    Arguments.checkIsNotNull (skin, "skin");

    this.skin = skin;
  }

  public TextButton createTextButton (final String text, final EventListener listener)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (listener, "listener");

    final TextButton textButton = new TextButton (text, skin);
    textButton.addListener (listener);

    return textButton;
  }

  public static TextButton createTextButton (final String text, final TextButton.TextButtonStyle style, final EventListener listener)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final TextButton textButton = new TextButton (text, style);
    textButton.addListener (listener);

    return textButton;
  }

  public ImageButton createImageButton (final String styleName, final EventListener listener)
  {
    Arguments.checkIsNotNull (styleName, "styleName");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageButton imageButton = new ImageButton (skin.get (styleName, ImageButton.ImageButtonStyle.class));
    imageButton.addListener (listener);

    return imageButton;
  }

  public Popup createQuitPopup (final String message, final Stage stage, final PopupListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new QuitPopup (skin, message, stage, listener);
  }

  public Label createLabel (final String text, final int alignment, final String labelStyle)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    final Label label = new Label (text, skin, labelStyle);
    label.setAlignment (alignment);

    return label;
  }

  public Label createWrappingLabel (final String text, final int alignment, final String labelStyle)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    final Label label = new Label (text, skin, labelStyle);
    label.setAlignment (alignment);
    label.setWrap (true);

    return label;
  }

  public Label createBackgroundLabel (final String text, final int alignment)
  {
    Arguments.checkIsNotNull (text, "text");

    final Label label = new Label (text, skin, "label-text-with-background");
    label.setAlignment (alignment);

    return label;
  }

  public TextField createTextField (final int maxLength, final Pattern filter)
  {
    Arguments.checkIsNotNegative (maxLength, "maxLength");
    Arguments.checkIsNotNull (filter, "filter");

    final TextField textField = new TextField ("", skin)
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

  public CheckBox createCheckBox ()
  {
    return new CheckBox ("", skin);
  }

  public <T> SelectBox <T> createSelectBox ()
  {
    return new SelectBox <> (skin);
  }
}
