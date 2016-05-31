package com.forerunnergames.peril.client.ui.widgets.playercoloricons;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.tools.common.Arguments;

abstract class NullPlayerColorIconWidgetFactory extends AbstractWidgetFactory implements PlayerColorIconWidgetFactory
{
  NullPlayerColorIconWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  public PlayerColorIcon createPlayerColorIcon (final PlayerColor activeColor)
  {
    return new NullPlayerColorIcon ();
  }

  @Override
  public Button createPlayerColorIconButton (final PlayerColor color)
  {
    return new Button (createPlayerColorIconStyle (color));
  }

  @Override
  public Button.ButtonStyle createPlayerColorIconStyle (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return new Button.ButtonStyle ();
  }
}
