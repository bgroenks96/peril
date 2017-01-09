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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.playmap.io.AbstractPlayMapDataPathParser;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

abstract class AbstractPlayMapGraphicsPathParser extends AbstractPlayMapDataPathParser
        implements PlayMapGraphicsPathParser
{
  AbstractPlayMapGraphicsPathParser (final GameMode gameMode)
  {
    super (gameMode);
  }

  @Override
  public final String parseCountryAtlasesPath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseCountriesPath (metadata) + AssetSettings.RELATIVE_COUNTRY_ATLASES_DIRECTORY;
  }

  @Override
  public final String parseCountryImageDataFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseCountryDataPath (metadata) + AssetSettings.COUNTRY_IMAGE_DATA_FILENAME;
  }

  @Override
  public final String parseCountryInputDetectionDataFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseCountryDataPath (metadata) + AssetSettings.COUNTRY_INPUT_DETECTION_DATA_FILENAME;
  }

  @Override
  public final String parseContinentInputDetectionDataFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parseContinentDataPath (metadata) + AssetSettings.CONTINENT_INPUT_DETECTION_DATA_FILENAME;
  }

  @Override
  public final String parseInputDetectionImageFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parsePlayMapPath (metadata) + AssetSettings.PLAY_MAP_INPUT_DETECTION_IMAGE_FILENAME;
  }

  @Override
  public final String parseBackgroundImageFileNamePath (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    return parsePlayMapPath (metadata) + AssetSettings.PLAY_MAP_BACKGROUND_IMAGE_FILENAME;
  }

  @Override
  protected final String parsePlayMapDirName (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    switch (metadata.getType ())
    {
      case CUSTOM:
      case STOCK:
      {
        return parseExternalPlayMapDirName (metadata);
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Unsupported {}: [{}].", PlayMapType.class.getSimpleName (), metadata.getType ()));
      }
    }
  }
}
