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
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.playmap.io.DefaultPlayMapMetadataFinder;
import com.forerunnergames.peril.common.playmap.io.PlayMapMetadataFinder;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CorePlayMapMetadataFinderFactory
{
  public static PlayMapMetadataFinder create (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return new DefaultPlayMapMetadataFinder (gameMode,
            new CorePlayMapMetadataLoaderFactory (gameMode).create (PlayMapType.STOCK, PlayMapType.CUSTOM).load ());
  }

  private CorePlayMapMetadataFinderFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
