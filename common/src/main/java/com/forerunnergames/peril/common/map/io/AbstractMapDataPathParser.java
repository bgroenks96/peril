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

package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public abstract class AbstractMapDataPathParser implements MapDataPathParser
{
  private final GameMode gameMode;

  protected AbstractMapDataPathParser (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    this.gameMode = gameMode;
  }

  @Override
  public String parseCardsFileNamePath (final MapMetadata mapMetadata)
  {
    return parseCardDataPath (mapMetadata) + AssetSettings.CARD_DATA_FILENAME;
  }

  @Override
  public final String parseCountriesFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountryDataPath (mapMetadata) + AssetSettings.COUNTRY_DATA_FILENAME;
  }

  @Override
  public final String parseCountryGraphFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountryDataPath (mapMetadata) + AssetSettings.COUNTRY_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentGraphFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseContinentDataPath (mapMetadata) + AssetSettings.CONTINENT_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentsFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseContinentDataPath (mapMetadata) + AssetSettings.CONTINENT_DATA_FILENAME;
  }

  @Override
  public final String parseCountryDataPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountriesPath (mapMetadata) + AssetSettings.RELATIVE_COUNTRY_DATA_DIRECTORY;
  }

  @Override
  public final String parseContinentDataPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseContinentsPath (mapMetadata) + AssetSettings.RELATIVE_CONTINENT_DATA_DIRECTORY;
  }

  @Override
  public String parseCardDataPath (final MapMetadata mapMetadata)
  {
    return parseCardsPath (mapMetadata) + AssetSettings.RELATIVE_CARD_DATA_DIRECTORY;
  }

  @Override
  public final String parseMapNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapTypePath (mapMetadata.getType ()) + parseMapNameAsPath (mapMetadata);
  }

  @Override
  public final String parseCountriesPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapNamePath (mapMetadata) + AssetSettings.RELATIVE_COUNTRIES_DIRECTORY;
  }

  @Override
  public final String parseContinentsPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapNamePath (mapMetadata) + AssetSettings.RELATIVE_CONTINENTS_DIRECTORY;
  }

  @Override
  public String parseCardsPath (final MapMetadata mapMetadata)
  {
    return parseMapNamePath (mapMetadata) + AssetSettings.RELATIVE_CARDS_DIRECTORY;
  }

  @Override
  public final String parseMapTypePath (final MapType mapType)
  {
    Arguments.checkIsNotNull (mapType, "mapType");

    return parseMapsModePath (mapType, gameMode) + parseMapTypeAsPath (mapType);
  }

  @Override
  public final GameMode getGameMode ()
  {
    return gameMode;
  }

  protected abstract String parseMapsModePath (final MapType mapType, final GameMode gameMode);

  protected abstract String parseMapNameAsPath (final MapMetadata mapMetadata);

  private String parseMapTypeAsPath (final MapType mapType)
  {
    return mapType.name ().toLowerCase () + "/";
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}: {}", getClass ().getSimpleName (), gameMode.getClass ().getSimpleName (), gameMode);
  }
}
