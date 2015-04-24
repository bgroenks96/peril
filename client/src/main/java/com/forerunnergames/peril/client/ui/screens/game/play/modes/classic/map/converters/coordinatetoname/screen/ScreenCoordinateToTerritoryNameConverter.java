package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.core.model.map.territory.TerritoryName;

public interface ScreenCoordinateToTerritoryNameConverter <T extends TerritoryName>
{
  T convert (final Vector2 screenCoordinate);
}
