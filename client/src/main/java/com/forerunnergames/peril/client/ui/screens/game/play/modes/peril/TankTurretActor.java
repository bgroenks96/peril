package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.Assets;

public final class TankTurretActor extends Actor
{
  private final TextureRegion tankTurret;

  public TankTurretActor ()
  {
    tankTurret = Assets.perilModeAtlas.findRegion ("tankTurret");

    setOrigin (tankTurret.getRegionWidth () / 2.0f, tankTurret.getRegionHeight () / 3.0f);
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    batch.draw (tankTurret, getX (), Gdx.graphics.getHeight () - getY () - tankTurret.getRegionHeight (), getOriginX (),
            getOriginY (), tankTurret.getRegionWidth (), tankTurret.getRegionHeight (), getScaleX (), getScaleY (),
            getRotation ());
  }
}
