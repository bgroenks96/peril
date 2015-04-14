package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.color.RgbaColor;

public final class DefaultPlayMapCoordinateToRgbaColorConverter implements PlayMapCoordinateToRgbaColorConverter
{
  private final Pixmap rawPlayMapTerritoryColorsImage;
  private final IntMap <RgbaColor> rawToRgbaColors = new IntMap <> ();

  public DefaultPlayMapCoordinateToRgbaColorConverter (final Pixmap rawPlayMapTerritoryColorsImage)
  {
    Arguments.checkIsNotNull (rawPlayMapTerritoryColorsImage, "rawPlayMapTerritoryColorsImage");

    this.rawPlayMapTerritoryColorsImage = rawPlayMapTerritoryColorsImage;
  }

  @Override
  public RgbaColor convert (final Vector2 playMapCoordinate)
  {
    Arguments.checkIsNotNull (playMapCoordinate, "playMapCoordinate");

    final int rawColor = rawPlayMapTerritoryColorsImage.getPixel (Math.round (playMapCoordinate.x),
                                                                  Math.round (playMapCoordinate.y));

    RgbaColor rgbaColor = rawToRgbaColors.get (rawColor);

    if (rgbaColor != null) return rgbaColor;

    rgbaColor = new RgbaColor (rawColor);
    rawToRgbaColors.put (rawColor, rgbaColor);

    return rgbaColor;
  }
}
