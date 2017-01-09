/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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
  public String parseCardsFileNamePath (final PlayMapMetadata metadata)
  {
    return parseCardDataPath (metadata) + AssetSettings.CARD_DATA_FILENAME;
  }

  @Override
  public final String parseCountriesFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseCountryDataPath (metadata) + AssetSettings.COUNTRY_DATA_FILENAME;
  }

  @Override
  public final String parseCountryGraphFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseCountryDataPath (metadata) + AssetSettings.COUNTRY_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentGraphFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseContinentDataPath (metadata) + AssetSettings.CONTINENT_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentsFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseContinentDataPath (metadata) + AssetSettings.CONTINENT_DATA_FILENAME;
  }

  @Override
  public final String parseCountryDataPath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseCountriesPath (metadata) + AssetSettings.RELATIVE_COUNTRY_DATA_DIRECTORY;
  }

  @Override
  public final String parseContinentDataPath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseContinentsPath (metadata) + AssetSettings.RELATIVE_CONTINENT_DATA_DIRECTORY;
  }

  @Override
  public String parseCardDataPath (final PlayMapMetadata metadata)
  {
    return parseCardsPath (metadata) + AssetSettings.RELATIVE_CARD_DATA_DIRECTORY;
  }

  @Override
  public final String parsePlayMapPath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parsePlayMapTypePath (metadata.getType ()) + parsePlayMapDirName (metadata) + "/";
  }

  @Override
  public final String parseCountriesPath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parsePlayMapPath (metadata) + AssetSettings.RELATIVE_COUNTRIES_DIRECTORY;
  }

  @Override
  public final String parseContinentsPath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parsePlayMapPath (metadata) + AssetSettings.RELATIVE_CONTINENTS_DIRECTORY;
  }

  @Override
  public String parseCardsPath (final PlayMapMetadata metadata)
  {
    return parsePlayMapPath (metadata) + AssetSettings.RELATIVE_CARDS_DIRECTORY;
  }

  @Override
  public final String parsePlayMapTypePath (final PlayMapType type)
  {
    Arguments.checkIsNotNull (type, "type");

    return parsePlayMapsModePath (type, gameMode) + AssetSettings.asPathSegment (type);
  }

  @Override
  public final GameMode getGameMode ()
  {
    return gameMode;
  }

  protected abstract String parsePlayMapDirName (final PlayMapMetadata metadata);

  protected abstract String parsePlayMapsModePath (final PlayMapType type, final GameMode mode);

  protected final String parseExternalPlayMapDirName (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return AssetSettings.asExternalPlayMapDirName (metadata);
  }

  protected final String parseInternalPlayMapDirName (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return AssetSettings.asInternalPlayMapDirName (metadata);
  }

  protected final String parseAbsoluteExternalPlayMapsModePath (final GameMode mode)
  {
    Arguments.checkIsNotNull (mode, "mode");

    return AssetSettings.ABSOLUTE_EXTERNAL_PLAY_MAPS_DIRECTORY + AssetSettings.asExternalPathSegment (mode);
  }

  protected final String parseAbsoluteInternalPlayMapsModePath (final GameMode mode)
  {
    Arguments.checkIsNotNull (mode, "mode");

    return AssetSettings.ABSOLUTE_INTERNAL_PLAY_MAPS_MODE_DIRECTORY + AssetSettings.asInternalPathSegment (mode);
  }

  protected final String parseRelativeExternalPlayMapsModePath (final GameMode mode)
  {
    Arguments.checkIsNotNull (mode, "mode");

    return AssetSettings.RELATIVE_PLAY_MAPS_DIRECTORY + AssetSettings.asExternalPathSegment (mode);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}: {}", getClass ().getSimpleName (), gameMode.getClass ().getSimpleName (), gameMode);
  }
}
