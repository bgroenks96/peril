package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.tools.common.Arguments;

final class NullCountryArmyTextActor implements CountryArmyTextActor
{
  private final Actor actor = new Actor ();

  @Override
  public void setCircleTopLeft (final Vector2 circleTopLeft)
  {
    Arguments.checkIsNotNull (circleTopLeft, "circleTopLeft");
  }

  @Override
  public void setCircleSize (final Vector2 circleSize)
  {
    Arguments.checkIsNotNull (circleSize, "circleSize");
    Arguments.checkIsNotNegative (circleSize.x, "circleSize.x");
    Arguments.checkIsNotNegative (circleSize.y, "circleSize.y");
  }

  @Override
  public void setArmies (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");
  }

  @Override
  public void incrementArmies ()
  {
  }

  @Override
  public void decrementArmies ()
  {
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
  }

  @Override
  public void onPrimaryStateChange (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public Actor asActor ()
  {
    return actor;
  }
}
