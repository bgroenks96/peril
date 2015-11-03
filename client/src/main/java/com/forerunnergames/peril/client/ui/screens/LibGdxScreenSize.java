package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class LibGdxScreenSize implements ScreenSize
{
  private final Graphics graphics;
  private final Vector2 referenceScreenSize = new Vector2 ();
  private final Vector2 actualToReferenceScaling = new Vector2 ();
  private final Vector2 referenceToActualScaling = new Vector2 ();
  private final Vector2 temp = new Vector2 ();
  private final int referenceScreenWidth;
  private final int referenceScreenHeight;
  private int currentWidth;
  private int currentHeight;
  private int previousWidth;
  private int previousHeight;

  public LibGdxScreenSize (final Graphics graphics, final int referenceScreenWidth, final int referenceScreenHeight)
  {
    Arguments.checkIsNotNull (graphics, "graphics");
    Arguments.checkLowerExclusiveBound (referenceScreenWidth, 0, "referenceScreenWidth");
    Arguments.checkLowerExclusiveBound (referenceScreenHeight, 0, "referenceScreenHeight");

    this.graphics = graphics;
    this.referenceScreenWidth = referenceScreenWidth;
    this.referenceScreenHeight = referenceScreenHeight;
    referenceScreenSize.set (referenceScreenWidth, referenceScreenHeight);
  }

  @Override
  public int referenceWidth ()
  {
    return referenceScreenWidth;
  }

  @Override
  public int referenceHeight ()
  {
    return referenceScreenHeight;
  }

  @Override
  public Vector2 reference ()
  {
    return temp.set (referenceScreenSize);
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

  private void update ()
  {
    currentWidth = graphics.getWidth ();
    currentHeight = graphics.getHeight ();

    if (currentWidth != previousWidth)
    {
      actualToReferenceScaling.x = referenceScreenWidth / (float) currentWidth;
      referenceToActualScaling.x = currentWidth / (float) referenceScreenWidth;
      previousWidth = currentWidth;
    }

    if (currentHeight != previousHeight)
    {
      actualToReferenceScaling.y = referenceScreenHeight / (float) currentHeight;
      referenceToActualScaling.y = currentHeight / (float) referenceScreenHeight;
      previousHeight = currentHeight;
    }
  }

  @Override
  public String toString ()
  {
    update ();

    return Strings.format (
                           "{}: Current Width: {} | Current Height: {} | Previous Width: {}"
                                   + " | Previous Height: {} | Reference Width: {} | Reference Height: {}"
                                   + " | Reference to Actual Scaling: {} | Actual to Reference Scaling: {}",
                           getClass ().getSimpleName (), currentWidth, currentHeight, previousWidth, previousHeight,
                           referenceScreenWidth, referenceScreenHeight, referenceToActualScaling,
                           actualToReferenceScaling);
  }
}
