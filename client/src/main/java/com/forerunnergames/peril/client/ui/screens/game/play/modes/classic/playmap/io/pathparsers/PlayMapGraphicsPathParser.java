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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers;

import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.io.PlayMapDataPathParser;

public interface PlayMapGraphicsPathParser extends PlayMapDataPathParser
{
  String parseCountryAtlasesPath (final PlayMapMetadata metadata);

  String parseCountryImageDataFileNamePath (final PlayMapMetadata metadata);

  String parseCountryInputDetectionDataFileNamePath (final PlayMapMetadata metadata);

  String parseContinentInputDetectionDataFileNamePath (final PlayMapMetadata metadata);

  String parseInputDetectionImageFileNamePath (final PlayMapMetadata metadata);

  String parseBackgroundImageFileNamePath (final PlayMapMetadata metadata);
}
