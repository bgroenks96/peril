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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.common.playmap.PlayMapMetadata;

import com.google.common.collect.ImmutableList;

public interface CountryAtlasesLoader
{
  void load (final PlayMapMetadata playMapMetadata);

  boolean isFinishedLoading (final PlayMapMetadata playMapMetadata);

  ImmutableList <TextureAtlas> get (final PlayMapMetadata playMapMetadata);

  void unload (final PlayMapMetadata playMapMetadata);
}
