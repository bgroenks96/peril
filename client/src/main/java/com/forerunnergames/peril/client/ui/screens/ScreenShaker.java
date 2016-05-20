/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

public final class ScreenShaker
{
  private static final boolean RECENTER_CAMERA = true;
  private final Viewport viewport;
  private final ScreenSize screenSize;
  private final Vector3 cameraTranslation = new Vector3 ();
  private boolean isShaking = false;
  private double radius;
  private double randomAngle;

  public ScreenShaker (final Viewport viewport, final ScreenSize screenSize)
  {
    Arguments.checkIsNotNull (viewport, "viewport");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    this.viewport = viewport;
    this.screenSize = screenSize;
  }

  public void shake ()
  {
    isShaking = true;
    radius = 2.0;
    randomAngle = StrictMath.toRadians (Randomness.getRandomIntegerFrom (0, 359));
    cameraTranslation.set ((float) (StrictMath.sin (randomAngle) * radius),
                           (float) (StrictMath.cos (randomAngle) * radius), 0.0f);
    viewport.getCamera ().translate (cameraTranslation);
  }

  public void stop ()
  {
    viewport.update (screenSize.actualWidth (), screenSize.actualHeight (), RECENTER_CAMERA);
    viewport.setScreenPosition (InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_X,
                                InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_Y);
    isShaking = false;
  }

  public void update (final float delta)
  {
    if (!isShaking) return;

    if (radius < 1.0)
    {
      stop ();
      return;
    }

    radius *= 0.9f;
    randomAngle += StrictMath.toRadians (Randomness.getRandomIntegerFrom (121, 239));
    cameraTranslation.set ((float) (StrictMath.sin (randomAngle) * radius),
                           (float) (StrictMath.cos (randomAngle) * radius), 0.0f);

    viewport.getCamera ().translate (cameraTranslation);
  }
}
