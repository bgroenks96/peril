package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.ScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultScreenCoordinateToTerritoryNameConverter implements ScreenCoordinateToTerritoryNameConverter
{
  private final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter;
  private final PlayMapCoordinateToTerritoryNameConverter playMapCoordinateToTerritoryNameConverter;

  public DefaultScreenCoordinateToTerritoryNameConverter (final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter,
                                                          final PlayMapCoordinateToTerritoryNameConverter playMapCoordinateToTerritoryNameConverter)
  {
    Arguments.checkIsNotNull (screenToPlayMapCoordinateConverter, "screenToPlayMapCoordinateConverter");
    Arguments.checkIsNotNull (playMapCoordinateToTerritoryNameConverter, "playMapCoordinateToTerritoryNameConverter");

    this.screenToPlayMapCoordinateConverter = screenToPlayMapCoordinateConverter;
    this.playMapCoordinateToTerritoryNameConverter = playMapCoordinateToTerritoryNameConverter;
  }

  @Override
  public String convert (final Vector2 screenCoordinate)
  {
    Arguments.checkIsNotNull (screenCoordinate, "screenCoordinate");

    return playMapCoordinateToTerritoryNameConverter
            .convert (screenToPlayMapCoordinateConverter.convert (screenCoordinate));
  }
}
