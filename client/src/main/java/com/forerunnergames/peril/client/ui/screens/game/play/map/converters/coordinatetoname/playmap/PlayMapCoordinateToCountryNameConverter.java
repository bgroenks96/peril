package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.playmap;

import com.forerunnergames.peril.client.ui.screens.game.play.map.colors.CountryColor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.colortoname.TerritoryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocolor.PlayMapCoordinateToTerritoryColorConverter;
import com.forerunnergames.peril.core.model.map.country.CountryName;

// @formatter:off
public final class PlayMapCoordinateToCountryNameConverter
                extends AbstractPlayMapCoordinateToTerritoryNameConverter <CountryColor, CountryName>
{
  public PlayMapCoordinateToCountryNameConverter (
                  final PlayMapCoordinateToTerritoryColorConverter <CountryColor> playMapCoordinateToCountryColorConverter,
                  final TerritoryColorToNameConverter <CountryColor, CountryName> countryColorToCountryNameConverter)
  {
    super (playMapCoordinateToCountryColorConverter, countryColorToCountryNameConverter);
  }
}
