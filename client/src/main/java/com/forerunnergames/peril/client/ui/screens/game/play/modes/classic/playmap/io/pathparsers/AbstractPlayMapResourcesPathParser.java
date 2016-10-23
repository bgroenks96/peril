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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.io.AbstractPlayMapDataPathParser;
import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractPlayMapResourcesPathParser extends AbstractPlayMapDataPathParser implements
        PlayMapResourcesPathParser
{
  protected AbstractPlayMapResourcesPathParser (final GameMode gameMode)
  {
    super (gameMode);
  }

  @Override
  public final String parseCountryAtlasesPath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseCountriesPath (playMapMetadata) + AssetSettings.RELATIVE_COUNTRY_ATLASES_DIRECTORY;
  }

  @Override
  public final String parseCountryImageDataFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseCountryDataPath (playMapMetadata) + AssetSettings.COUNTRY_IMAGE_DATA_FILENAME;
  }

  @Override
  public final String parseCountryInputDetectionDataFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseCountryDataPath (playMapMetadata) + AssetSettings.COUNTRY_INPUT_DETECTION_DATA_FILENAME;
  }

  @Override
  public final String parseContinentInputDetectionDataFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parseContinentDataPath (playMapMetadata) + AssetSettings.CONTINENT_INPUT_DETECTION_DATA_FILENAME;
  }

  @Override
  public final String parseInputDetectionImageFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parsePlayMapNamePath (playMapMetadata) + AssetSettings.PLAY_MAP_INPUT_DETECTION_IMAGE_FILENAME;
  }

  @Override
  public final String parseBackgroundImageFileNamePath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return parsePlayMapNamePath (playMapMetadata) + AssetSettings.PLAY_MAP_BACKGROUND_IMAGE_FILENAME;
  }
}
