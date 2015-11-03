package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public final class ErrorPopup extends OkPopup
{
  public ErrorPopup (final WidgetFactory widgetFactory, final Stage stage, final PopupListener listener)
  {
    super (widgetFactory,
           PopupStyle.builder ().windowStyle ("popup").title ("ERROR").textButtonStyle ("popup")
                   .messageBoxRowLabelStyle ("popup-message").border (28).buttonSpacing (16).buttonWidth (90)
                   .textPadding (16).textBoxPaddingBottom (20).size (650, 388).build (),
           stage, listener);
  }

  public ErrorPopup (final WidgetFactory widgetFactory,
                     final Stage stage,
                     final String submitButtonText,
                     final PopupListener listener)
  {
    this (widgetFactory, stage, listener);

    changeButtonText ("OK", submitButtonText);
  }
}
