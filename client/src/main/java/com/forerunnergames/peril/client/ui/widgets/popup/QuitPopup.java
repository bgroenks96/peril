package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.forerunnergames.peril.client.ui.Assets;

public class QuitPopup extends OkCancelPopup
{
  public QuitPopup (final Skin skin, final String message, final Stage stage, final PopupListener listener)
  {
    super (skin, new Window.WindowStyle (skin.getFont ("default-font"), Color.WHITE, new TextureRegionDrawable (
            new TextureRegion (Assets.quitPopupBackground))), PopupStyle.builder ().titleHeight (34)
            .textButtonStyle ("popup").message (message).build (), stage, listener);

    changeButtonText ("OK", "QUIT");
  }
}
