/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.tools.common.Arguments;

public final class TankBody extends Actor
{
  private final TextureRegion tankBody;

  public TankBody (final TextureRegion tankBody)
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
