package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.forerunnergames.peril.client.io.LibGdxStreamParserFactory;
import com.forerunnergames.peril.core.model.io.StreamParserFactory;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.peril.client.settings.ClassicPlayMapSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.ContinentColor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.CountryColor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname.ContinentColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname.CountryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname.TerritoryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.DefaultPlayMapCoordinateToRgbaColorConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.PlayMapCoordinateToContinentColorConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.PlayMapCoordinateToCountryColorConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.PlayMapCoordinateToRgbaColorConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor.PlayMapCoordinateToTerritoryColorConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.DefaultInputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.DefaultScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.InputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.ScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.InputCoordinateToContinentNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.InputCoordinateToCountryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.InputCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToContinentNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToCountryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.ScreenCoordinateToContinentNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.ScreenCoordinateToCountryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders.ContinentColorToNameLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders.CountryColorToNameLoader;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

// @formatter:off
public final class PlayMapInputDetectionFactory
{
  public static PlayMapInputDetection create (final ScreenSize screenSize)
  {
    Arguments.checkIsNotNull (screenSize, "screenSize");

    final PlayMapCoordinateToRgbaColorConverter playMapCoordinateToRgbaColorConverter;

    final PlayMapCoordinateToTerritoryColorConverter <CountryColor> playMapCoordinateToCountryColorConverter;
    final PlayMapCoordinateToTerritoryColorConverter <ContinentColor> playMapCoordinateToContinentColorConverter;

    final TerritoryColorToNameConverter <CountryColor, CountryName> countryColorToNameConverter;
    final TerritoryColorToNameConverter <ContinentColor, ContinentName> continentColorToNameConverter;

    final PlayMapCoordinateToTerritoryNameConverter <CountryName> playMapCoordinateToCountryNameConverter;
    final PlayMapCoordinateToTerritoryNameConverter <ContinentName> playMapCoordinateToContinentNameConverter;

    final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter;

    final ScreenCoordinateToTerritoryNameConverter <CountryName> screenCoordinateToCountryNameConverter;
    final ScreenCoordinateToTerritoryNameConverter <ContinentName> screenCoordinateToContinentNameConverter;

    final InputToScreenCoordinateConverter inputToScreenCoordinateConverter;

    final InputCoordinateToTerritoryNameConverter <CountryName> inputCoordinateToCountryNameConverter;
    final InputCoordinateToTerritoryNameConverter <ContinentName> inputCoordinateToContinentNameConverter;

    playMapCoordinateToRgbaColorConverter = new DefaultPlayMapCoordinateToRgbaColorConverter (Assets.playMapInputDetection);

    playMapCoordinateToCountryColorConverter = new PlayMapCoordinateToCountryColorConverter (playMapCoordinateToRgbaColorConverter);
    playMapCoordinateToContinentColorConverter = new PlayMapCoordinateToContinentColorConverter (playMapCoordinateToRgbaColorConverter);

    final StreamParserFactory streamParserFactory = new LibGdxStreamParserFactory ();
    final DataLoader <CountryColor, CountryName> countryColorToNameLoader = new CountryColorToNameLoader (streamParserFactory);
    final DataLoader <ContinentColor, ContinentName> continentColorToNameLoader = new ContinentColorToNameLoader (streamParserFactory);

    countryColorToNameConverter = new CountryColorToNameConverter (countryColorToNameLoader.load (ClassicPlayMapSettings.COUNTRY_NAME_TO_COLOR_FILENAME));
    continentColorToNameConverter = new ContinentColorToNameConverter (continentColorToNameLoader.load (ClassicPlayMapSettings.CONTINENT_NAME_TO_COLOR_FILENAME));

    playMapCoordinateToCountryNameConverter =
                    new PlayMapCoordinateToCountryNameConverter (
                                    playMapCoordinateToCountryColorConverter,
                                    countryColorToNameConverter);

    playMapCoordinateToContinentNameConverter =
                    new PlayMapCoordinateToContinentNameConverter (
                                    playMapCoordinateToContinentColorConverter,
                                    continentColorToNameConverter);

    screenToPlayMapCoordinateConverter = new DefaultScreenToPlayMapCoordinateConverter (screenSize);

    screenCoordinateToCountryNameConverter =
                    new ScreenCoordinateToCountryNameConverter (
                                    screenToPlayMapCoordinateConverter,
                                    playMapCoordinateToCountryNameConverter);

    screenCoordinateToContinentNameConverter =
                    new ScreenCoordinateToContinentNameConverter (
                                    screenToPlayMapCoordinateConverter,
                                    playMapCoordinateToContinentNameConverter);

    inputToScreenCoordinateConverter = new DefaultInputToScreenCoordinateConverter ();

    inputCoordinateToCountryNameConverter =
                    new InputCoordinateToCountryNameConverter (
                                    inputToScreenCoordinateConverter,
                                    screenCoordinateToCountryNameConverter);

    inputCoordinateToContinentNameConverter =
                    new InputCoordinateToContinentNameConverter (
                                    inputToScreenCoordinateConverter,
                                    screenCoordinateToContinentNameConverter);

    return new PlayMapInputDetection (inputCoordinateToCountryNameConverter, inputCoordinateToContinentNameConverter);
  }
  // @formatter:on

  private PlayMapInputDetectionFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
