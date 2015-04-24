package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.forerunnergames.peril.client.ui.Assets;

public abstract class QuitPopup extends OkCancelPopup
{
  protected QuitPopup (final Skin skin, final PopupStyle popupStyle, final Stage stage)
  {
    super (skin, new Window.WindowStyle (Assets.droidSans20, Color.WHITE, new TextureRegionDrawable (
            new TextureRegion (Assets.quitPopupBackground))), popupStyle, stage);

    changeButtonText ("OK", "QUIT");
  }
}
