package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname.TerritoryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.PlayMapCoordinateToTerritoryColorConverter;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractPlayMapCoordinateToTerritoryNameConverter <T extends TerritoryColor <?>, U extends TerritoryName>
        implements PlayMapCoordinateToTerritoryNameConverter <U>
{
  private final TerritoryColorToNameConverter <T, U> territoryColorToNameConverter;
  private final PlayMapCoordinateToTerritoryColorConverter <T> playMapCoordinateToTerritoryColorConverter;

  protected AbstractPlayMapCoordinateToTerritoryNameConverter (final PlayMapCoordinateToTerritoryColorConverter <T> playMapCoordinateToTerritoryColorConverter,
                                                               final TerritoryColorToNameConverter <T, U> territoryColorToNameConverter)
  {
    Arguments.checkIsNotNull (playMapCoordinateToTerritoryColorConverter, "playMapCoordinateToTerritoryColorConverter");
    Arguments.checkIsNotNull (territoryColorToNameConverter, "territoryColorToNameConverter");

    this.playMapCoordinateToTerritoryColorConverter = playMapCoordinateToTerritoryColorConverter;
    this.territoryColorToNameConverter = territoryColorToNameConverter;
  }

  @Override
  public final U convert (final Vector2 playMapCoordinate)
  {
    final T color = playMapCoordinateToTerritoryColorConverter.convert (playMapCoordinate);

    return territoryColorToNameConverter.convert (color);
  }
}
