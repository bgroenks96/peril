package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate;

import com.badlogic.gdx.math.Vector2;

public interface ScreenToPlayMapCoordinateConverter
{
  Vector2 convert (final Vector2 actualScreenCoordinate, final Vector2 screenSize);
}
