package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
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

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.QuitPopup;
import com.forerunnergames.tools.common.Arguments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WidgetFactory
{
  private final AssetManager assetManager;

  public WidgetFactory (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  public final TextButton createTextButton (final String text,
                                            final TextButton.TextButtonStyle style,
                                            final EventListener listener)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (style, "style");
    Arguments.checkIsNotNull (listener, "listener");

    final TextButton textButton = new TextButton (text, style);
    textButton.addListener (listener);

    return textButton;
  }

  public final TextButton createTextButton (final String text, final EventListener listener)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (listener, "listener");

    final TextButton textButton = new TextButton (text, getSkin ());
    textButton.addListener (listener);

    return textButton;
  }

  public final ImageButton createImageButton (final String styleName, final EventListener listener)
  {
    Arguments.checkIsNotNull (styleName, "styleName");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageButton imageButton = new ImageButton (getSkin ().get (styleName, ImageButton.ImageButtonStyle.class));
    imageButton.addListener (listener);

    return imageButton;
  }

  public final Popup createQuitPopup (final String message, final Stage stage, final PopupListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new QuitPopup (getSkin (), message, stage, listener);
  }

  public final Label createLabel (final String text, final int alignment, final String labelStyle)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    final Label label = new Label (text, getSkin (), labelStyle);
    label.setAlignment (alignment);

    return label;
  }

  public final Label createWrappingLabel (final String text, final int alignment, final String labelStyle)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNull (labelStyle, "labelStyle");

    final Label label = new Label (text, getSkin (), labelStyle);
    label.setAlignment (alignment);
    label.setWrap (true);

    return label;
  }

  public final Label createBackgroundLabel (final String text, final int alignment)
  {
    Arguments.checkIsNotNull (text, "text");

    final Label label = new Label (text, getSkin (), "label-text-with-background");
    label.setAlignment (alignment);

    return label;
  }

  public final TextField createTextField (final int maxLength, final Pattern filter)
  {
    Arguments.checkIsNotNegative (maxLength, "maxLength");
    Arguments.checkIsNotNull (filter, "filter");

    final TextField textField = new TextField ("", getSkin ())
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

  public final CheckBox createCheckBox ()
  {
    return new CheckBox ("", getSkin ());
  }

  public final <T> SelectBox <T> createSelectBox ()
  {
    return new SelectBox <> (getSkin ());
  }

  public Pixmap createNormalCursor ()
  {
    return getAsset (AssetSettings.NORMAL_CURSOR_ASSET_DESCRIPTOR);
  }

  protected final <T> T getAsset (final AssetDescriptor <T> assetDescriptor)
  {
    Arguments.checkIsNotNull (assetDescriptor, "assetDescriptor");

    return assetManager.get (assetDescriptor);
  }

  protected final <T> T getSkinStyle (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return getSkin ().get (type);
  }

  protected final <T> T getSkinStyle (final String styleName, final Class <T> type)
  {
    Arguments.checkIsNotNull (styleName, "styleName");
    Arguments.checkIsNotNull (type, "type");

    return getSkin ().get (styleName, type);
  }

  protected final Skin getSkin ()
  {
    return getAsset (AssetSettings.SKIN_JSON_ASSET_DESCRIPTOR);
  }

  public final AssetManager getAssetManager ()
  {
    return assetManager;
  }
}
