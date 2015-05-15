package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.ClassicPlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultScreenToPlayMapCoordinateConverter implements ScreenToPlayMapCoordinateConverter
{
  private final ScreenSize screenSize;
  private final Vector2 coordinate = new Vector2 ();

  public DefaultScreenToPlayMapCoordinateConverter (final ScreenSize screenSize)
  {
    Arguments.checkIsNotNull (screenSize, "screenSize");

    this.screenSize = screenSize;
  }

  @Override
  public Vector2 convert (final Vector2 actualScreenCoordinate)
  {
    Arguments.checkIsNotNull (actualScreenCoordinate, "actualScreenCoordinate");

    return coordinate.set (actualScreenCoordinate).scl (screenSize.actualToReferenceScaling ())
            .add (ClassicPlayMapSettings.REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION)
            .scl (ClassicPlayMapSettings.ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_PLAY_MAP_SPACE_SCALING);
  }
}
