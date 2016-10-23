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
  String parseCardsFileNamePath (final PlayMapMetadata metadata);

  String parseCountriesFileNamePath (final PlayMapMetadata metadata);

  String parseCountryGraphFileNamePath (final PlayMapMetadata metadata);

  String parseContinentGraphFileNamePath (final PlayMapMetadata metadata);

  String parseContinentsFileNamePath (final PlayMapMetadata metadata);

  String parseCountryDataPath (final PlayMapMetadata metadata);

  String parseContinentDataPath (final PlayMapMetadata metadata);

  String parseCardDataPath (final PlayMapMetadata metadata);

  String parsePlayMapPath (final PlayMapMetadata metadata);

  String parseCountriesPath (final PlayMapMetadata metadata);

  String parseContinentsPath (final PlayMapMetadata metadata);

  String parseCardsPath (final PlayMapMetadata metadata);

  String parsePlayMapTypePath (final PlayMapType type);

  GameMode getGameMode ();
}
