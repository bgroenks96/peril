package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocolor;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.IntMap;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.color.RgbaColor;
import com.forerunnergames.tools.common.geometry.Point2D;

public final class DefaultPlayMapCoordinateToRgbaColorConverter implements PlayMapCoordinateToRgbaColorConverter
{
  private final Pixmap rawPlayMapTerritoryColorsImage;
  private final IntMap <RgbaColor> rawToRgbaColors = new IntMap <> ();
  private int rawColor;
  private RgbaColor rgbaColor;

  public DefaultPlayMapCoordinateToRgbaColorConverter (final Pixmap rawPlayMapTerritoryColorsImage)
  {
    Arguments.checkIsNotNull (rawPlayMapTerritoryColorsImage, "rawPlayMapTerritoryColorsImage");

    this.rawPlayMapTerritoryColorsImage = rawPlayMapTerritoryColorsImage;
  }

  @Override
  public RgbaColor convert (final Point2D playMapCoordinate)
  {
    Arguments.checkIsNotNull (playMapCoordinate, "playMapCoordinate");

    rawColor = rawPlayMapTerritoryColorsImage.getPixel (Math.round (playMapCoordinate.getX ()),
                                                        Math.round (playMapCoordinate.getY ()));
    rgbaColor = rawToRgbaColors.get (rawColor);

    if (rgbaColor != null) return rgbaColor;

    rgbaColor = new RgbaColor (rawColor);
    rawToRgbaColors.put (rawColor, rgbaColor);

    return rgbaColor;
  }
}
