package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public final class QuitPopup extends OkCancelPopup
{
  public QuitPopup (final WidgetFactory widgetFactory,
                    final String message,
                    final Stage stage,
                    final PopupListener listener)
  {
    super (widgetFactory,
           PopupStyle.builder ().windowStyle ("popup").title ("QUIT?").textButtonStyle ("popup")
                   .messageBoxRowLabelStyle ("popup-message").border (28).buttonSpacing (16).buttonWidth (90)
                   .textPadding (16).textBoxPaddingBottom (20).message (message).build (),
           stage, listener);

    changeButtonText ("OK", "QUIT");
  }
}
