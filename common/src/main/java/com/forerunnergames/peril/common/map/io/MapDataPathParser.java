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

package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;

public interface MapDataPathParser
{
  String parseCardsFileNamePath (final MapMetadata mapMetadata);

  String parseCountriesFileNamePath (final MapMetadata mapMetadata);

  String parseCountryGraphFileNamePath (final MapMetadata mapMetadata);

  String parseContinentGraphFileNamePath (final MapMetadata mapMetadata);

  String parseContinentsFileNamePath (final MapMetadata mapMetadata);

  String parseCountryDataPath (final MapMetadata mapMetadata);

  String parseContinentDataPath (final MapMetadata mapMetadata);

  String parseCardDataPath (final MapMetadata mapMetadata);

  String parseMapNamePath (final MapMetadata mapMetadata);

  String parseCountriesPath (final MapMetadata mapMetadata);

  String parseContinentsPath (final MapMetadata mapMetadata);

  String parseCardsPath (final MapMetadata mapMetadata);

  String parseMapTypePath (MapType mapType);

  GameMode getGameMode ();
}
