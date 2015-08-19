package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.math.IntMath;

public final class DefaultCountryArmyTextActor implements CountryArmyTextActor
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
  private final Actor actor = new ActorDelegate ();
  private String armiesText;
  private int armies = 0;

  public DefaultCountryArmyTextActor (final BitmapFont font)
  {
    Arguments.checkIsNotNull (font, "font");

    this.font = font;

    setArmies (0);
  }

  @Override
  public void setCircleTopLeft (final Vector2 circleTopLeft)
  {
    Arguments.checkIsNotNull (circleTopLeft, "circleTopLeft");

    this.circleTopLeft.set (circleTopLeft);
  }

  @Override
  public void setCircleSize (final Vector2 circleSize)
  {
    Arguments.checkIsNotNull (circleSize, "circleSize");
    Arguments.checkIsNotNegative (circleSize.x, "circleSize.x");
    Arguments.checkIsNotNegative (circleSize.y, "circleSize.y");

    this.circleSize.set (circleSize);
  }

  @Override
  public void setArmies (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    changeArmiesBy (calculateDeltaArmies (armies));
  }

  @Override
  public void incrementArmies ()
  {
    changeArmiesBy (1);
  }

  @Override
  public void decrementArmies ()
  {
    changeArmiesBy (-1);
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
    final int newArmies = IntMath.checkedAdd (armies, deltaArmies);

    if (newArmies < MIN_ARMIES || newArmies > MAX_ARMIES) return;

    armies = newArmies;

    changeText (String.valueOf (newArmies));
  }

  @Override
  public void onPrimaryStateChange (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    switch (state)
    {
      case DISABLED:
      {
        actor.setVisible (false);
        break;
      }
      default:
      {
        actor.setVisible (true);
        break;
      }
    }
  }

  @Override
  public Actor asActor ()
  {
    return actor;
  }

  private int calculateDeltaArmies (final int desiredArmies)
  {
    return IntMath.checkedSubtract (desiredArmies, armies);
  }

  private void changeText (final String text)
  {
    armiesText = text;
    glyphLayout.setText (font, text);
    actor.setSize (glyphLayout.width, glyphLayout.height);
  }

  private final class ActorDelegate extends Actor
  {
    @Override
    public void draw (final Batch batch, final float parentAlpha)
    {
      localToParentCoordinates (initialPosition.set (circleTopLeft));

      finalPosition.x = initialPosition.x + (circleSize.x - glyphLayout.width) / 2.0f + FONT_METRICS_ADJUSTMENT.x;
      finalPosition.y = initialPosition.y - (circleSize.y - glyphLayout.height) / 2.0f + FONT_METRICS_ADJUSTMENT.y;

      font.draw (batch, armiesText, finalPosition.x, finalPosition.y);
    }
  }
}
