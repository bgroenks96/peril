package com.forerunnergames.peril.client.ui.widgets.playercoloricons;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.tools.common.Arguments;

final class NullPlayerColorIcon implements PlayerColorIcon
{
  private final Actor actor = new Actor ();

  @Override
  public void setColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
  }

  @Override
  public void refreshAssets ()
  {
  }

  @Override
  public Actor asActor ()
  {
    return actor;
  }
}
