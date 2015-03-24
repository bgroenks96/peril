package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.ScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

// @formatter:off
public abstract class AbstractScreenCoordinateToTerritoryNameConverter <T extends TerritoryName>
                implements ScreenCoordinateToTerritoryNameConverter <T>
{
  private final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter;
  private final PlayMapCoordinateToTerritoryNameConverter<T> playMapCoordinateToTerritoryNameConverter;
  private Point2D playMapCoordinate;

  protected AbstractScreenCoordinateToTerritoryNameConverter (
                  final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter,
                  final PlayMapCoordinateToTerritoryNameConverter <T> playMapCoordinateToTerritoryNameConverter)
  {
    Arguments.checkIsNotNull (screenToPlayMapCoordinateConverter, "screenToPlayMapCoordinateConverter");
    Arguments.checkIsNotNull (playMapCoordinateToTerritoryNameConverter, "playMapCoordinateToTerritoryNameConverter");

    this.screenToPlayMapCoordinateConverter = screenToPlayMapCoordinateConverter;
    this.playMapCoordinateToTerritoryNameConverter = playMapCoordinateToTerritoryNameConverter;
  }

  @Override
  public T convert (final Point2D screenCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (screenCoordinate, "screenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    playMapCoordinate = screenToPlayMapCoordinateConverter.convert (screenCoordinate, screenSize);

    return playMapCoordinateToTerritoryNameConverter.convert (playMapCoordinate);
  }
}
