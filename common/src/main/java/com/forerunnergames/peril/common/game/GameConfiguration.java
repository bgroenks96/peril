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

package com.forerunnergames.peril.common.game;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;

public interface GameConfiguration
{
  GameMode getGameMode ();

  int getPlayerLimit ();

  int getSpectatorLimit ();

  int getWinPercentage ();

  InitialCountryAssignment getInitialCountryAssignment ();

  String getMapName ();

  MapMetadata getMapMetadata ();

  MapType getMapType ();
}
