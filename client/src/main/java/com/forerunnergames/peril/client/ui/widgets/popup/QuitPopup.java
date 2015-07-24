package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class QuitPopup extends OkCancelPopup
{
  public QuitPopup (final Skin skin, final String message, final Stage stage, final PopupListener listener)
  {
    super (skin, PopupStyle.builder ().windowStyle ("popup").title ("QUIT?").textButtonStyle ("popup")
            .border (28).buttonSpacing (16).buttonWidth (90).textPadding (16).textBoxPaddingBottom (20)
            .message (message).build (), stage, listener);

    changeButtonText ("OK", "QUIT");
  }
}
