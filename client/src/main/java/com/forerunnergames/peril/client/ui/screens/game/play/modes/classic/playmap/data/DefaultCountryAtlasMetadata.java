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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class DefaultCountryAtlasMetadata implements CountryAtlasMetadata
{
  private final AssetDescriptor <TextureAtlas> assetDescriptor;
  private final MapMetadata mapMetadata;

  public DefaultCountryAtlasMetadata (final AssetDescriptor <TextureAtlas> assetDescriptor,
                                      final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (assetDescriptor, "assetDescriptor");
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    this.assetDescriptor = assetDescriptor;
    this.mapMetadata = mapMetadata;
  }

  @Override
  public AssetDescriptor <TextureAtlas> getAssetDescriptor ()
  {
    return assetDescriptor;
  }

  @Override
  public MapMetadata getMapMetadata ()
  {
    return mapMetadata;
  }

  @Override
  public int hashCode ()
  {
    int result = assetDescriptor.hashCode ();
    result = 31 * result + mapMetadata.hashCode ();
    return result;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    final DefaultCountryAtlasMetadata that = (DefaultCountryAtlasMetadata) obj;

    return assetDescriptor.fileName.equals (that.assetDescriptor.fileName) && mapMetadata.equals (that.mapMetadata);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}: {} | {}: {}", getClass ().getSimpleName (),
                           assetDescriptor.getClass ().getSimpleName (), assetDescriptor,
                           mapMetadata.getClass ().getSimpleName (), mapMetadata);
  }
}
