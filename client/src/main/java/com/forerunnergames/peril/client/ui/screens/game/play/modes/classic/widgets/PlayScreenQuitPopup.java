package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.OkCancelPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupStyle;

public final class PlayScreenQuitPopup extends OkCancelPopup
{
  public PlayScreenQuitPopup (final WidgetFactory widgetFactory,
                              final String message,
                              final Stage stage,
                              final PopupListener listener)
  {
    super (widgetFactory,
           PopupStyle.builder ().title ("QUIT?").border (28).buttonSpacing (16).buttonWidth (90).textPadding (16)
                   .textBoxPaddingBottom (20).size (650, 244)
                   .position (587, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 284).message (message).build (),
           stage, listener);

    changeButtonText ("OK", "QUIT");
  }
}
