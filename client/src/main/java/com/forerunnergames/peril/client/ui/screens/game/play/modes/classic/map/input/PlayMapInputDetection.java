package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.badlogic.gdx.math.Vector2;

public interface PlayMapInputDetection
{
  String getCountryNameAt (final Vector2 inputCoordinate);

  String getContinentNameAt (final Vector2 inputCoordinate);
}
