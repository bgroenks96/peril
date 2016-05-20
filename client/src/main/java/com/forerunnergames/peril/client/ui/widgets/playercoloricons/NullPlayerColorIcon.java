package com.forerunnergames.peril.client.ui.widgets.playercoloricons;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.tools.common.Arguments;

final class NullPlayerColorIcon implements PlayerColorIcon
{
  private final Actor actor = new Actor ();

  @Override
  public void setColor (final String color)
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
