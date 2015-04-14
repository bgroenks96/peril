package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.CountryColor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname.TerritoryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.PlayMapCoordinateToTerritoryColorConverter;
import com.forerunnergames.peril.core.model.map.country.CountryName;

public final class PlayMapCoordinateToCountryNameConverter extends
        AbstractPlayMapCoordinateToTerritoryNameConverter <CountryColor, CountryName>
{
  public PlayMapCoordinateToCountryNameConverter (final PlayMapCoordinateToTerritoryColorConverter <CountryColor> playMapCoordinateToCountryColorConverter,
                                                  final TerritoryColorToNameConverter <CountryColor, CountryName> countryColorToCountryNameConverter)
  {
    super (playMapCoordinateToCountryColorConverter, countryColorToCountryNameConverter);
  }
}
