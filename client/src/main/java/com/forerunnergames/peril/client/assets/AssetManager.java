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

package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;

public interface AssetManager
{
  <T> T get (final AssetDescriptor <T> descriptor);

  <T> T get (final String fileName, final Class <T> type);

  void load (final AssetDescriptor <?> descriptor);

  <T> void load (final String fileName, final Class <T> type);

  <T> void load (final String fileName, final Class <T> type, final AssetLoaderParameters <T> parameters);

  void update ();

  float getProgressLoading ();

  void finishLoading (final String fileName);

  boolean isLoaded (final AssetDescriptor <?> descriptor);

  boolean isLoaded (final String fileName, final Class <?> type);

  boolean isLoaded (final String fileName);

  void unload (final AssetDescriptor <?> descriptor);

  void unload (final String fileName);

  void finishLoading (final AssetDescriptor<?> descriptor);

  void dispose ();
}
