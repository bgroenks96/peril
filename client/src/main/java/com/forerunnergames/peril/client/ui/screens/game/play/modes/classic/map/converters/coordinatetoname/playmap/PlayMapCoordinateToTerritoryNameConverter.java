package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap;

import com.badlogic.gdx.math.Vector2;

public interface PlayMapCoordinateToTerritoryNameConverter
{
  String convert (final Vector2 playMapCoordinate);
}
