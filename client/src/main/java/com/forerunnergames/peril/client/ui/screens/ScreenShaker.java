package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

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
