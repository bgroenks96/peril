package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.peril.core.model.map.country.CountryName;

public interface PlayMapInputDetection
{
  CountryName getCountryNameAt (final Vector2 inputCoordinate);

  ContinentName getContinentNameAt (final Vector2 inputCoordinate);
}
