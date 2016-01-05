package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultScreenToPlayMapCoordinateConverter implements ScreenToPlayMapCoordinateConverter
{
  private final ScreenSize screenSize;
  private final Vector2 coordinate = new Vector2 ();
  private final Vector2 playMapReferenceSize;

  public DefaultScreenToPlayMapCoordinateConverter (final ScreenSize screenSize, final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    this.screenSize = screenSize;
    this.playMapReferenceSize = playMapReferenceSize;
  }

  @Override
  public Vector2 convert (final Vector2 actualScreenCoordinate)
  {
    Arguments.checkIsNotNull (actualScreenCoordinate, "actualScreenCoordinate");

    return coordinate.set (actualScreenCoordinate).scl (screenSize.actualToReferenceScaling ())
            .add (PlayMapSettings.REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION)
            .scl (PlayMapSettings.actualToReferencePlayMapScaling (playMapReferenceSize));
  }
}
