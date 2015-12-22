package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.math.IntMath;

public class DefaultCountryArmyTextActor implements CountryArmyTextActor
{
  private static final int MIN_ARMIES = 0;
  private static final int MAX_ARMIES = 99;
  private static final Vector2 FONT_METRICS_ADJUSTMENT = new Vector2 (0, 1);
  private final Vector2 circleTopLeft = new Vector2 ();
  private final Vector2 circleSize = new Vector2 ();
  private final Vector2 initialPosition = new Vector2 ();
  private final Vector2 finalPosition = new Vector2 ();
  private final BitmapFontActor textActor;
  private int armies = 0;

  public DefaultCountryArmyTextActor (final BitmapFont font)
  {
    Arguments.checkIsNotNull (font, "font");

    textActor = new BitmapFontActor (font);

    setArmies (MIN_ARMIES);
    setText (String.valueOf (MIN_ARMIES));
  }

  @Override
  public final void setCircleTopLeft (final Vector2 topLeft)
  {
    Arguments.checkIsNotNull (topLeft, "topLeft");

    circleTopLeft.set (topLeft);
  }

  @Override
  public final void setCircleSize (final Vector2 size)
  {
    Arguments.checkIsNotNull (size, "size");
    Arguments.checkIsNotNegative (size.x, "size.x");
    Arguments.checkIsNotNegative (size.y, "size.y");

    circleSize.set (size);
  }

  @Override
  public final void changeArmiesTo (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    changeArmiesBy (calculateDeltaArmies (armies));
  }

  @Override
  public final void incrementArmies ()
  {
    changeArmiesBy (1);
  }

  @Override
  public final void decrementArmies ()
  {
    changeArmiesBy (-1);
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
    final int newArmies = IntMath.checkedAdd (armies, deltaArmies);

    setArmies (newArmies);

    textActor.setText (String.valueOf (armies));
  }

  @Override
  public final void onPrimaryStateChange (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    switch (state)
    {
      case DISABLED:
      {
        textActor.setVisible (false);
        break;
      }
      default:
      {
        textActor.setVisible (true);
        break;
      }
    }
  }

  @Override
  public final int getArmies ()
  {
    return armies;
  }

  @Override
  public void setFont (final BitmapFont font)
  {
    Arguments.checkIsNotNull (font, "font");

    textActor.setFont (font);
  }

  @Override
  public final Actor asActor ()
  {
    return textActor;
  }

  protected final void setArmies (final int armies)
  {
    if (armies < MIN_ARMIES || armies > MAX_ARMIES) return;

    this.armies = armies;
  }

  protected final void setText (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    textActor.setText (text);
  }

  protected final void addAction (final Action action)
  {
    Arguments.checkIsNotNull (action, "action");

    textActor.addAction (action);
  }

  private int calculateDeltaArmies (final int desiredArmies)
  {
    return IntMath.checkedSubtract (desiredArmies, armies);
  }

  private final class BitmapFontActor extends Actor
  {
    private BitmapFont font;
    private final GlyphLayout layout;

    BitmapFontActor (final BitmapFont font)
    {
      this.font = font;
      layout = new GlyphLayout ();
    }

    @Override
    public void draw (final Batch batch, final float parentAlpha)
    {
      localToParentCoordinates (initialPosition.set (circleTopLeft));

      finalPosition.x = initialPosition.x + (circleSize.x - layout.width) / 2.0f + FONT_METRICS_ADJUSTMENT.x;
      finalPosition.y = initialPosition.y - (circleSize.y - layout.height) / 2.0f + FONT_METRICS_ADJUSTMENT.y;

      font.draw (batch, layout, finalPosition.x, finalPosition.y);
    }

    private void setText (final String text)
    {
      layout.setText (font, text);
      setSize (layout.width, layout.height);
    }

    private void setFont (final BitmapFont font)
    {
      this.font = font;
    }
  }
}
