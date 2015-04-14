package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.color.RgbaColor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlayMapCoordinateToTerritoryColorConverter <T extends TerritoryColor <?>> implements
        PlayMapCoordinateToTerritoryColorConverter <T>
{
  private final PlayMapCoordinateToRgbaColorConverter converter;
  private final Map <RgbaColor, T> rgbaToTerritoryColors = new HashMap <> ();

  protected AbstractPlayMapCoordinateToTerritoryColorConverter (final PlayMapCoordinateToRgbaColorConverter converter)
  {
    Arguments.checkIsNotNull (converter, "converter");

    this.converter = converter;
  }

  @Override
  public T convert (final Vector2 playMapCoordinate)
  {
    Arguments.checkIsNotNull (playMapCoordinate, "playMapCoordinate");

    final RgbaColor rgbaColor = converter.convert (playMapCoordinate);

    T territoryColor = rgbaToTerritoryColors.get (rgbaColor);

    if (territoryColor != null) return territoryColor;

    territoryColor = createTerritoryColor (rgbaColor);
    rgbaToTerritoryColors.put (rgbaColor, territoryColor);

    return territoryColor;
  }

  protected abstract T createTerritoryColor (final RgbaColor color);
}
