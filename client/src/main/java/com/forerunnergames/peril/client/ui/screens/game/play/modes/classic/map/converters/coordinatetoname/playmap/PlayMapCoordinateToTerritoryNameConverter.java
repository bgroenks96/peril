package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.core.model.map.territory.TerritoryName;

public interface PlayMapCoordinateToTerritoryNameConverter <T extends TerritoryName>
{
  T convert (final Vector2 playMapCoordinate);
}
