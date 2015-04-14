package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;

public interface PlayMapCoordinateToTerritoryColorConverter <T extends TerritoryColor <?>>
{
  T convert (final Vector2 playMapCoordinate);
}
