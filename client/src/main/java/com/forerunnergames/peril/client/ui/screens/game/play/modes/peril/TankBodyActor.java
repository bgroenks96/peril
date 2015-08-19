package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.tools.common.Arguments;

public final class TankBodyActor extends Actor
{
  private final TextureRegion tankBody;

  public TankBodyActor (final TextureRegion tankBody)
  {
    Arguments.checkIsNotNull (tankBody, "tankBody");

    this.tankBody = tankBody;

    setOrigin (12, 26);
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    batch.draw (tankBody, getX (), Gdx.graphics.getHeight () - getY () - tankBody.getRegionHeight (), getOriginX (),
                getOriginY (), tankBody.getRegionWidth (), tankBody.getRegionHeight (), getScaleX (), getScaleY (),
                getRotation ());
  }
}
