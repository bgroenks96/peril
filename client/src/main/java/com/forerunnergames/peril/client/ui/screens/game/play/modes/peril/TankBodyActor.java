package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.Assets;

public final class TankBodyActor extends Actor
{
  private final TextureRegion tankBody;

  public TankBodyActor ()
  {
    tankBody = Assets.perilModeAtlas.findRegion ("tankBody");

    setOrigin (tankBody.getRegionWidth () / 2.0f, tankBody.getRegionHeight () / 3.0f);
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    batch.draw (tankBody, getX (), Gdx.graphics.getHeight () - getY () - tankBody.getRegionHeight (), getOriginX (),
                getOriginY (), tankBody.getRegionWidth (), tankBody.getRegionHeight (), getScaleX (), getScaleY (),
                getRotation ());
  }
}
