package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.playmap;

import com.forerunnergames.peril.client.ui.screens.game.play.map.colors.ContinentColor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.colortoname.TerritoryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocolor.PlayMapCoordinateToTerritoryColorConverter;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;

// @formatter:off
public final class PlayMapCoordinateToContinentNameConverter
                extends AbstractPlayMapCoordinateToTerritoryNameConverter <ContinentColor, ContinentName>
{
  public PlayMapCoordinateToContinentNameConverter (
                  final PlayMapCoordinateToTerritoryColorConverter <ContinentColor> playMapCoordinateToContinentColorConverter,
                  final TerritoryColorToNameConverter <ContinentColor, ContinentName> continentColorToContinentNameConverter)
  {
    super (playMapCoordinateToContinentColorConverter, continentColorToContinentNameConverter);
  }
}
