/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.assets.AssetManager;
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
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.DefaultInputCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.InputCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToContinentNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToCountryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.DefaultScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.ContinentColorToNameLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.CountryColorToNameLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.DefaultPlayMapInputDetectionImageLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.PlayMapInputDetectionImageLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.AbsoluteMapResourcesPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.MapResourcesPathParser;
import com.forerunnergames.peril.common.io.BiMapDataLoader;
import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

public final class DefaultPlayMapInputDetectionFactory implements PlayMapInputDetectionFactory
{
  private final ScreenSize screenSize;
  private final PlayMapInputDetectionImageLoader imageLoader;

  public DefaultPlayMapInputDetectionFactory (final AssetManager assetManager, final ScreenSize screenSize)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    this.screenSize = screenSize;
    imageLoader = new DefaultPlayMapInputDetectionImageLoader (assetManager);
  }

  @Override
  public void loadAssets (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    imageLoader.load (mapMetadata);
  }

  @Override
  public boolean isFinishedLoadingAssets (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return imageLoader.isFinishedLoading (mapMetadata);
  }

  @Override
  public PlayMapInputDetection create (final MapMetadata mapMetadata, final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");
    Preconditions.checkIsTrue (isFinishedLoadingAssets (mapMetadata),
                               Strings.format ("Assets must finish loading before creating {} for map [{}].",
                                               PlayMapInputDetection.class.getSimpleName (), mapMetadata));

    // @formatter:off

    final PlayMapCoordinateToRgbaColorConverter playMapCoordinateToRgbaColorConverter =
            new DefaultPlayMapCoordinateToRgbaColorConverter (imageLoader.get (mapMetadata));

    final PlayMapCoordinateToTerritoryColorConverter <CountryColor> playMapCoordinateToCountryColorConverter =
            new PlayMapCoordinateToCountryColorConverter (playMapCoordinateToRgbaColorConverter);

    final PlayMapCoordinateToTerritoryColorConverter <ContinentColor> playMapCoordinateToContinentColorConverter =
            new PlayMapCoordinateToContinentColorConverter (playMapCoordinateToRgbaColorConverter);

    final StreamParserFactory streamParserFactory = new ExternalStreamParserFactory ();

    final BiMapDataLoader <CountryColor, String> countryColorToNameLoader =
            new CountryColorToNameLoader (streamParserFactory);

    final BiMapDataLoader <ContinentColor, String> continentColorToNameLoader =
            new ContinentColorToNameLoader (streamParserFactory);

    final MapResourcesPathParser absoluteMapResourcesPathParser =
            new AbsoluteMapResourcesPathParser (mapMetadata.getMode ());

    final TerritoryColorToNameConverter <CountryColor> countryColorToNameConverter =
            new CountryColorToNameConverter (
                    countryColorToNameLoader.load (
                            absoluteMapResourcesPathParser.parseCountryInputDetectionDataFileNamePath (
                                    mapMetadata)));

    final TerritoryColorToNameConverter <ContinentColor> continentColorToNameConverter =
            new ContinentColorToNameConverter (
                    continentColorToNameLoader.load (
                            absoluteMapResourcesPathParser.parseContinentInputDetectionDataFileNamePath (
                                    mapMetadata)));

    final PlayMapCoordinateToTerritoryNameConverter playMapCoordinateToCountryNameConverter =
            new PlayMapCoordinateToCountryNameConverter (
                    playMapCoordinateToCountryColorConverter,
                    countryColorToNameConverter);

    final PlayMapCoordinateToTerritoryNameConverter playMapCoordinateToContinentNameConverter =
            new PlayMapCoordinateToContinentNameConverter (
                    playMapCoordinateToContinentColorConverter,
                    continentColorToNameConverter);

    final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter =
            new DefaultScreenToPlayMapCoordinateConverter (screenSize, playMapReferenceSize);

    final ScreenCoordinateToTerritoryNameConverter screenCoordinateToCountryNameConverter =
            new DefaultScreenCoordinateToTerritoryNameConverter (
                    screenToPlayMapCoordinateConverter,
                    playMapCoordinateToCountryNameConverter);

    final ScreenCoordinateToTerritoryNameConverter screenCoordinateToContinentNameConverter =
            new DefaultScreenCoordinateToTerritoryNameConverter (
                    screenToPlayMapCoordinateConverter,
                    playMapCoordinateToContinentNameConverter);

    final InputToScreenCoordinateConverter inputToScreenCoordinateConverter =
            new DefaultInputToScreenCoordinateConverter ();

    final InputCoordinateToTerritoryNameConverter inputCoordinateToCountryNameConverter =
            new DefaultInputCoordinateToTerritoryNameConverter (
                    inputToScreenCoordinateConverter,
                    screenCoordinateToCountryNameConverter);

    final InputCoordinateToTerritoryNameConverter inputCoordinateToContinentNameConverter =
            new DefaultInputCoordinateToTerritoryNameConverter (
                    inputToScreenCoordinateConverter,
                    screenCoordinateToContinentNameConverter);

    return new DefaultPlayMapInputDetection (
            inputCoordinateToCountryNameConverter,
            inputCoordinateToContinentNameConverter);
  }

  @Override
  public void destroy (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    imageLoader.unload (mapMetadata);
  }
}
