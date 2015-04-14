package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.tools.common.Arguments;

public class DefaultInputToScreenCoordinateConverter implements InputToScreenCoordinateConverter
{
  private final Vector2 screenCoordinate = new Vector2 ();

  @Override
  public Vector2 convert (final Vector2 inputCoordinate)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");

    return screenCoordinate.set (inputCoordinate)
            .add (InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION);
  }
}
