package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultCountryArmyTextActor;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.math.IntMath;

public final class CountryArmyTextEffectsActor extends DefaultCountryArmyTextActor
{
  private static final float MOVE_BY_X = -80.0f;
  private static final float MOVE_BY_Y = 80.0f;
  private static final float MOVE_TIME_SECONDS = 1.5f;
  private final HorizontalMoveDirection direction;

  public CountryArmyTextEffectsActor (final BitmapFont font, final HorizontalMoveDirection direction)
  {
    super (font);

    Arguments.checkIsNotNull (direction, "direction");

    this.direction = direction;
  }

  enum HorizontalMoveDirection
  {
    LEFT (1.0f),
    RIGHT (-1.0f);

    private final float sign;

    public float getSign ()
    {
      return sign;
    }

    HorizontalMoveDirection (final float sign)
    {
      this.sign = sign;
    }
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
    final int oldArmies = getArmies ();
    final int newArmies = IntMath.checkedAdd (oldArmies, deltaArmies);

    setArmies (newArmies);

    if (deltaArmies == 0) return;

    setText ((deltaArmies > 0 ? "+" : "") + deltaArmies);

    // @formatter:off
    addAction (
            Actions.sequence (
                    Actions.show (),
                    Actions.parallel (
                            Actions.moveBy (direction.getSign () * MOVE_BY_X, MOVE_BY_Y, MOVE_TIME_SECONDS),
                            Actions.fadeOut (MOVE_TIME_SECONDS, Interpolation.fade)),
                    Actions.hide (),
                    Actions.parallel (
                            Actions.alpha (1.0f),
                            Actions.moveBy (-direction.getSign () * MOVE_BY_X, -MOVE_BY_Y))));
    // @formatter:on
  }
}
