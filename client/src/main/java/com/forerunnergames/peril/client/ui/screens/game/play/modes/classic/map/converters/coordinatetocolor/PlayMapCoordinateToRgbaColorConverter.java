package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.color.RgbaColor;

public interface PlayMapCoordinateToRgbaColorConverter
{
  RgbaColor convert (final Vector2 playMapCoordinate);
}
