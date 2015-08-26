package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public final class ErrorPopup extends OkPopup
{
  public ErrorPopup (final Skin skin, final Stage stage, final PopupListener listener)
  {
    super (skin,
           PopupStyle.builder ().windowStyle ("popup").title ("ERROR").textButtonStyle ("popup").border (28)
                   .buttonSpacing (16).buttonWidth (90).textPadding (16).textBoxPaddingBottom (20).size (800, 400)
                   .build (),
           stage, listener);
  }
}
