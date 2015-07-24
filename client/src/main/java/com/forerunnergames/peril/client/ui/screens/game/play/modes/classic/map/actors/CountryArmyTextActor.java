package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.math.IntMath;

public final class CountryArmyTextActor extends Actor
{
  private static final int MIN_ARMIES = 0;
  private static final int MAX_ARMIES = 99;
  private static final Vector2 FONT_METRICS_ADJUSTMENT = new Vector2 (0, 1);
  private final BitmapFont font;
  private final GlyphLayout glyphLayout = new GlyphLayout ();
  private final Vector2 circleTopLeft = new Vector2 ();
  private final Vector2 circleSize = new Vector2 ();
  private final Vector2 initialPosition = new Vector2 ();
  private final Vector2 finalPosition = new Vector2 ();
  private String armiesText;
  private int armies = 0;

  public CountryArmyTextActor ()
  {
    font = Assets.defaultFont;

    setArmies (0);
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    localToParentCoordinates (initialPosition.set (circleTopLeft));

    finalPosition.x = initialPosition.x + (circleSize.x - glyphLayout.width) / 2.0f + FONT_METRICS_ADJUSTMENT.x;
    finalPosition.y = initialPosition.y - (circleSize.y - glyphLayout.height) / 2.0f + FONT_METRICS_ADJUSTMENT.y;

    font.draw (batch, armiesText, finalPosition.x, finalPosition.y);
  }

  public void setCircleTopLeft (final Vector2 circleTopLeft)
  {
    Arguments.checkIsNotNull (circleTopLeft, "circleTopLeft");

    this.circleTopLeft.set (circleTopLeft);
  }

  public void setCircleSize (final Vector2 circleSize)
  {
    Arguments.checkIsNotNull (circleSize, "circleSize");
    Arguments.checkIsNotNegative (circleSize.x, "circleSize.x");
    Arguments.checkIsNotNegative (circleSize.y, "circleSize.y");

    this.circleSize.set (circleSize);
  }

  public void setArmies (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    changeArmiesBy (calculateDeltaArmies (armies));
  }

  public void incrementArmies ()
  {
    changeArmiesBy (1);
  }

  public void decrementArmies ()
  {
    changeArmiesBy (-1);
  }

  public void changeArmiesBy (final int deltaArmies)
  {
    final int newArmies = IntMath.checkedAdd (armies, deltaArmies);

    if (newArmies < MIN_ARMIES || newArmies > MAX_ARMIES) return;

    armies = newArmies;

    changeText (String.valueOf (newArmies));
  }

  public void onPrimaryStateChange (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    switch (state)
    {
      case DISABLED:
      {
        setVisible (false);
        break;
      }
      default:
      {
        setVisible (true);
        break;
      }
    }
  }

  private int calculateDeltaArmies (final int desiredArmies)
  {
    return IntMath.checkedSubtract (desiredArmies, armies);
  }

  private void changeText (final String text)
  {
    armiesText = text;
    glyphLayout.setText (font, text);
    setSize (glyphLayout.width, glyphLayout.height);
  }
}
