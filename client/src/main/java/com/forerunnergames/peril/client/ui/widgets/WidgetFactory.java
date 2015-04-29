package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.QuitPopup;
import com.forerunnergames.tools.common.Arguments;

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

  public TextButton createTextButton (final String text,
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

  public Popup createQuitPopup (final String message, final Stage stage, final PopupListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (listener, "listener");

    return new QuitPopup (skin, message, stage, listener);
  }
}
