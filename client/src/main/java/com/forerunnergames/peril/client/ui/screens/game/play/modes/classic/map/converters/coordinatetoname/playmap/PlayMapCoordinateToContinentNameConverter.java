package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.ContinentColor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname.TerritoryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.PlayMapCoordinateToTerritoryColorConverter;

public final class PlayMapCoordinateToContinentNameConverter
        extends AbstractPlayMapCoordinateToTerritoryNameConverter <ContinentColor>
{
  public PlayMapCoordinateToContinentNameConverter (final PlayMapCoordinateToTerritoryColorConverter <ContinentColor> playMapCoordinateToContinentColorConverter,
                                                    final TerritoryColorToNameConverter <ContinentColor> continentColorToContinentNameConverter)
  {
    super (playMapCoordinateToContinentColorConverter, continentColorToContinentNameConverter);
  }
}
