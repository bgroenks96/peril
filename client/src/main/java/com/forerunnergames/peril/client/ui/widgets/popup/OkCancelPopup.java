package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public class OkCancelPopup extends OkPopup
{
  public OkCancelPopup (final WidgetFactory widgetFactory,
                        final PopupStyle popupStyle,
                        final Stage stage,
                        final PopupListener listener)
  {
    super (widgetFactory, popupStyle, stage, listener);
  }

  @Override
  protected void addButtons ()
  {
    addButton ("CANCEL", PopupAction.HIDE);

    super.addButtons ();
  }

  @Override
  protected void addKeys ()
  {
    super.addKeys ();

    addKey (Input.Keys.ESCAPE, PopupAction.HIDE);
  }
}
