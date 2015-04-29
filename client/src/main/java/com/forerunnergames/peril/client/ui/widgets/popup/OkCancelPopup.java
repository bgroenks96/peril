package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class OkCancelPopup extends OkPopup
{
  public OkCancelPopup (final Skin skin,
                        final PopupStyle popupStyle,
                        final Stage stage,
                        final PopupListener listener)
  {
    super (skin, popupStyle, stage, listener);
  }

  public OkCancelPopup (final Skin skin,
                        final Window.WindowStyle windowStyle,
                        final PopupStyle popupStyle,
                        final Stage stage,
                        final PopupListener listener)
  {
    super (skin, windowStyle, popupStyle, stage, listener);
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
