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

package com.forerunnergames.peril.core.model.playmap.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.playmap.io.AbstractPlayMapDataPathParser;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class CorePlayMapDataPathParser extends AbstractPlayMapDataPathParser
{
  public CorePlayMapDataPathParser (final GameMode gameMode)
  {
    super (gameMode);
  }

  @Override
  protected String parsePlayMapDirName (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    switch (metadata.getType ())
    {
      case CUSTOM:
      {
        return parseExternalPlayMapDirName (metadata);
      }
      case STOCK:
      {
        return parseInternalPlayMapDirName (metadata);
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Unsupported {}: [{}].", PlayMapType.class.getSimpleName (), metadata.getType ()));
      }
    }
  }

  @Override
  protected String parsePlayMapsModePath (final PlayMapType type, final GameMode mode)
  {
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNull (mode, "mode");

    switch (type)
    {
      case CUSTOM:
      {
        return parseAbsoluteExternalPlayMapsModePath (mode);
      }
      case STOCK:
      {
        return parseAbsoluteInternalPlayMapsModePath (mode);
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Unsupported {}: [{}].", PlayMapType.class.getSimpleName (), type));
      }
    }
  }
}
