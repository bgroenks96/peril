/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.common.playmap.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public abstract class AbstractPlayMapDataPathParser implements PlayMapDataPathParser
{
  private final GameMode gameMode;

  protected AbstractPlayMapDataPathParser (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    this.gameMode = gameMode;
  }

  @Override
  public String parseCardsFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    return parseCardDataPath (playMapMetadata) + AssetSettings.CARD_DATA_FILENAME;
  }

  @Override
  public final String parseCountriesFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseCountryDataPath (playMapMetadata) + AssetSettings.COUNTRY_DATA_FILENAME;
  }

  @Override
  public final String parseCountryGraphFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseCountryDataPath (playMapMetadata) + AssetSettings.COUNTRY_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentGraphFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseContinentDataPath (playMapMetadata) + AssetSettings.CONTINENT_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentsFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseContinentDataPath (playMapMetadata) + AssetSettings.CONTINENT_DATA_FILENAME;
  }

  @Override
  public final String parseCountryDataPath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseCountriesPath (playMapMetadata) + AssetSettings.RELATIVE_COUNTRY_DATA_DIRECTORY;
  }

  @Override
  public final String parseContinentDataPath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseContinentsPath (playMapMetadata) + AssetSettings.RELATIVE_CONTINENT_DATA_DIRECTORY;
  }

  @Override
  public String parseCardDataPath (final PlayMapMetadata playMapMetadata)
  {
    return parseCardsPath (playMapMetadata) + AssetSettings.RELATIVE_CARD_DATA_DIRECTORY;
  }

  @Override
  public final String parsePlayMapNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parsePlayMapTypePath (playMapMetadata.getType ()) + parsePlayMapNameAsPath (playMapMetadata);
  }

  @Override
  public final String parseCountriesPath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parsePlayMapNamePath (playMapMetadata) + AssetSettings.RELATIVE_COUNTRIES_DIRECTORY;
  }

  @Override
  public final String parseContinentsPath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parsePlayMapNamePath (playMapMetadata) + AssetSettings.RELATIVE_CONTINENTS_DIRECTORY;
  }

  @Override
  public String parseCardsPath (final PlayMapMetadata playMapMetadata)
  {
    return parsePlayMapNamePath (playMapMetadata) + AssetSettings.RELATIVE_CARDS_DIRECTORY;
  }

  @Override
  public final String parsePlayMapTypePath (final PlayMapType playMapType)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");

    return parsePlayMapsModePath (playMapType, gameMode) + parsePlayMapTypeAsPath (playMapType);
  }

  @Override
  public final GameMode getGameMode ()
  {
    return gameMode;
  }

  protected abstract String parsePlayMapsModePath (final PlayMapType playMapType, final GameMode gameMode);

  protected abstract String parsePlayMapNameAsPath (final PlayMapMetadata playMapMetadata);

  private String parsePlayMapTypeAsPath (final PlayMapType playMapType)
  {
    return playMapType.name ().toLowerCase () + "/";
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}: {}", getClass ().getSimpleName (), gameMode.getClass ().getSimpleName (), gameMode);
  }
}
