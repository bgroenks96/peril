package com.forerunnergames.peril.client.ui.widgets.playercoloricons;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.tools.common.Arguments;

abstract class NullPlayerColorIconWidgetFactory extends AbstractWidgetFactory implements PlayerColorIconWidgetFactory
{
  NullPlayerColorIconWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  public PlayerColorIcon createPlayerColorIcon (final String activeColor)
  {
    return new NullPlayerColorIcon ();
  }

  @Override
  public Button createPlayerColorIconButton (final String color)
  {
    return new Button (createPlayerColorIconStyle (color));
  }

  @Override
  public Button.ButtonStyle createPlayerColorIconStyle (final String color)
  {
    Arguments.checkIsNotNull (color, "color");

    return new Button.ButtonStyle ();
  }
}
