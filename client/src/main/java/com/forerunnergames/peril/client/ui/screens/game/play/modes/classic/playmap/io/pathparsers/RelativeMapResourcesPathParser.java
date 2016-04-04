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

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

/**
 * Use to resolve map resource paths ONLY for resources that WILL be loaded with an
 * {@link com.badlogic.gdx.assets.AssetManager}
 */
public final class RelativeMapResourcesPathParser extends AbstractMapResourcesPathParser
{
  public RelativeMapResourcesPathParser (final GameMode gameMode)
  {
    super (gameMode);
  }

  @Override
  protected String parseMapsModePath (final MapType mapType, final GameMode gameMode)
  {
    Arguments.checkIsNotNull (mapType, "mapType");
    Arguments.checkIsNotNull (gameMode, "gameMode");

    switch (mapType)
    {
      case STOCK:
      case CUSTOM:
      {
        return AssetSettings.RELATIVE_MAPS_DIRECTORY + gameMode.name ().toLowerCase () + " mode/";
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Unsupported {}: [{}].", MapType.class.getSimpleName (), mapType));
      }
    }
  }

  @Override
  protected String parseMapNameAsPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return mapMetadata.getName ().toLowerCase () + "/";
  }
}
