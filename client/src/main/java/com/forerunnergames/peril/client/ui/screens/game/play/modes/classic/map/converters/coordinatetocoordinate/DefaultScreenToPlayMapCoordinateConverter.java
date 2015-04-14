package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultScreenToPlayMapCoordinateConverter implements ScreenToPlayMapCoordinateConverter
{
  private final Vector2 coordinate = new Vector2 ();

  @Override
  public Vector2 convert (final Vector2 actualScreenCoordinate, final Vector2 screenSize)
  {
    Arguments.checkIsNotNull (actualScreenCoordinate, "actualScreenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return coordinate
            .set (actualScreenCoordinate)
            .scl (GraphicsSettings.REFERENCE_SCREEN_WIDTH / screenSize.x,
                  GraphicsSettings.REFERENCE_SCREEN_HEIGHT / screenSize.y)
            .add (PlayMapSettings.REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION)
            .scl (PlayMapSettings.ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_PLAY_MAP_SPACE_SCALING);
  }
}
