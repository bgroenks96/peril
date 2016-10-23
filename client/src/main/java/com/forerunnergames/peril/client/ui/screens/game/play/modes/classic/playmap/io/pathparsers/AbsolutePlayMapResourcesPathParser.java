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
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

/**
 * Use to resolve play map resource paths ONLY for resources that WILL NOT be loaded with an
 * {@link com.badlogic.gdx.assets.AssetManager}
 */
public final class AbsolutePlayMapResourcesPathParser extends AbstractPlayMapResourcesPathParser
{
  public AbsolutePlayMapResourcesPathParser (final GameMode gameMode)
  {
    super (gameMode);
  }

  @Override
  protected String parsePlayMapsModePath (final PlayMapType playMapType, final GameMode gameMode)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");
    Arguments.checkIsNotNull (gameMode, "gameMode");

    switch (playMapType)
    {
      case STOCK:
      case CUSTOM:
      {
        return AssetSettings.ABSOLUTE_EXTERNAL_PLAY_MAPS_DIRECTORY + gameMode.name ().toLowerCase () + " mode/";
      }
      default:
      {
        throw new PlayMapLoadingException (Strings.format ("Unsupported {}: [{}].", PlayMapType.class.getSimpleName (),
                                                           playMapType));
      }
    }
  }

  @Override
  protected String parsePlayMapNameAsPath (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    return playMapMetadata.getName ().toLowerCase () + "/";
  }
}
