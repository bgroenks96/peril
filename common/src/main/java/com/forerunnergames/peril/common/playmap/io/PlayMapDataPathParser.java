/*
 * Copyright © 2016 Forerunner Games, LLC.
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

public interface PlayMapDataPathParser
{
  String parseCardsFileNamePath (final PlayMapMetadata playMapMetadata);

  String parseCountriesFileNamePath (final PlayMapMetadata playMapMetadata);

  String parseCountryGraphFileNamePath (final PlayMapMetadata playMapMetadata);

  String parseContinentGraphFileNamePath (final PlayMapMetadata playMapMetadata);

  String parseContinentsFileNamePath (final PlayMapMetadata playMapMetadata);

  String parseCountryDataPath (final PlayMapMetadata playMapMetadata);

  String parseContinentDataPath (final PlayMapMetadata playMapMetadata);

  String parseCardDataPath (final PlayMapMetadata playMapMetadata);

  String parsePlayMapNamePath (final PlayMapMetadata playMapMetadata);

  String parseCountriesPath (final PlayMapMetadata playMapMetadata);

  String parseContinentsPath (final PlayMapMetadata playMapMetadata);

  String parseCardsPath (final PlayMapMetadata playMapMetadata);

  String parsePlayMapTypePath (final PlayMapType playMapType);

  GameMode getGameMode ();
}
