package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.tools.common.Arguments;

public final class LibGdxScreenSize implements ScreenSize
{
  private final Graphics graphics;
  private final Vector2 actualToReferenceScaling = new Vector2 ();
  private final Vector2 referenceToActualScaling = new Vector2 ();
  private final Vector2 temp = new Vector2 ();
  private int currentWidth;
  private int currentHeight;
  private int previousWidth;
  private int previousHeight;

  public LibGdxScreenSize (final Graphics graphics)
  {
    Arguments.checkIsNotNull (graphics, "graphics");

    this.graphics = graphics;
  }

  @Override
  public int referenceWidth ()
  {
    return ScreenSettings.REFERENCE_SCREEN_WIDTH;
  }

  @Override
  public int referenceHeight ()
  {
    return ScreenSettings.REFERENCE_SCREEN_HEIGHT;
  }

  @Override
  public Vector2 reference ()
  {
    return temp.set (ScreenSettings.REFERENCE_SCREEN_SIZE);
  }

  @Override
  public int actualWidth ()
  {
    update ();

    return currentWidth;
  }

  @Override
  public int actualHeight ()
  {
    update ();

    return currentHeight;
  }

  @Override
  public Vector2 actual ()
  {
    update ();

    return temp.set (currentWidth, currentHeight);
  }

  @Override
  public float actualToReferenceScalingX ()
  {
    update ();

    return actualToReferenceScaling.x;
  }

  @Override
  public float actualToReferenceScalingY ()
  {
    update ();

    return actualToReferenceScaling.y;
  }

  @Override
  public Vector2 actualToReferenceScaling ()
  {
    update ();

    return temp.set (actualToReferenceScaling);
  }

  @Override
  public float referenceToActualScalingX ()
  {
    update ();

    return referenceToActualScaling.x;
  }

  @Override
  public float referenceToActualScalingY ()
  {
    update ();

    return referenceToActualScaling.y;
  }

  @Override
  public Vector2 referenceToActualScaling ()
  {
    update ();

    return temp.set (referenceToActualScaling);
  }

  @Override
  public String toString ()
  {
    update ();

    return String.format ("%1$s: Current Width: %2$s | Current Height: %3$s | Previous Width: %4$s | "
                                  + "Previous Height: %5$s | Reference to Actual Scaling: %6$s | "
                                  + "Actual to Reference Scaling: %7$s",
                          getClass ().getSimpleName (), currentWidth, currentHeight, previousWidth, previousHeight,
                          referenceToActualScaling, actualToReferenceScaling);
  }

  private void update ()
  {
    currentWidth = graphics.getWidth ();
    currentHeight = graphics.getHeight ();

    if (currentWidth != previousWidth)
    {
      actualToReferenceScaling.x = ScreenSettings.REFERENCE_SCREEN_WIDTH / (float) currentWidth;
      referenceToActualScaling.x = currentWidth / (float) ScreenSettings.REFERENCE_SCREEN_WIDTH;
      previousWidth = currentWidth;
    }

    if (currentHeight != previousHeight)
    {
      actualToReferenceScaling.y = ScreenSettings.REFERENCE_SCREEN_HEIGHT / (float) currentHeight;
      referenceToActualScaling.y = currentHeight / (float) ScreenSettings.REFERENCE_SCREEN_HEIGHT;
      previousHeight = currentHeight;
    }
  }
}
