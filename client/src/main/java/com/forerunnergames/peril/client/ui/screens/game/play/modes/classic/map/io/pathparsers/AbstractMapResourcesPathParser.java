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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.io.AbstractMapDataPathParser;
import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractMapResourcesPathParser extends AbstractMapDataPathParser implements MapResourcesPathParser
{
  protected AbstractMapResourcesPathParser (final GameMode gameMode)
  {
    super (gameMode);
  }

  @Override
  public final String parseCountryAtlasesPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountriesPath (mapMetadata) + AssetSettings.RELATIVE_COUNTRY_ATLASES_DIRECTORY;
  }

  @Override
  public final String parseCountryImageDataFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountryDataPath (mapMetadata) + AssetSettings.COUNTRY_IMAGE_DATA_FILENAME;
  }

  @Override
  public final String parseCountryInputDetectionDataFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountryDataPath (mapMetadata) + AssetSettings.COUNTRY_INPUT_DETECTION_DATA_FILENAME;
  }

  @Override
  public final String parseContinentInputDetectionDataFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseContinentDataPath (mapMetadata) + AssetSettings.CONTINENT_INPUT_DETECTION_DATA_FILENAME;
  }

  @Override
  public final String parseInputDetectionImageFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapNamePath (mapMetadata) + AssetSettings.MAP_INPUT_DETECTION_IMAGE_FILENAME;
  }

  @Override
  public final String parseBackgroundImageFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapNamePath (mapMetadata) + AssetSettings.MAP_BACKGROUND_IMAGE_FILENAME;
  }
}
