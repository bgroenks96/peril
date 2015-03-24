package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.color.RgbaColor;
import com.forerunnergames.tools.common.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;

// @formatter:off
public abstract class AbstractPlayMapCoordinateToTerritoryColorConverter <T extends TerritoryColor <?>>
                implements PlayMapCoordinateToTerritoryColorConverter <T>
{
  private final PlayMapCoordinateToRgbaColorConverter converter;
  private final Map <RgbaColor, T> rgbaToTerritoryColors = new HashMap <> ();
  private RgbaColor rgbaColor;
  private T territoryColor;

  protected AbstractPlayMapCoordinateToTerritoryColorConverter (final PlayMapCoordinateToRgbaColorConverter converter)
  {
    Arguments.checkIsNotNull (converter, "converter");

    this.converter = converter;
  }

  @Override
  public T convert (final Point2D playMapCoordinate)
  {
    Arguments.checkIsNotNull (playMapCoordinate, "playMapCoordinate");

    rgbaColor = converter.convert (playMapCoordinate);

    territoryColor = rgbaToTerritoryColors.get (rgbaColor);

    if (territoryColor != null) return territoryColor;

    territoryColor = createTerritoryColor (rgbaColor);
    rgbaToTerritoryColors.put (rgbaColor, territoryColor);

    return territoryColor;
  }

  protected abstract T createTerritoryColor (final RgbaColor color);
}
