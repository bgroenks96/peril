package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public abstract class OkCancelPopup extends OkPopup
{
  public OkCancelPopup (final Skin skin, final PopupStyle popupStyle, final Stage stage)
  {
    super (skin, popupStyle, stage);
  }

  public OkCancelPopup (final Skin skin,
                        final Window.WindowStyle windowStyle,
                        final PopupStyle popupStyle,
                        final Stage stage)
  {
    super (skin, windowStyle, popupStyle, stage);
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
