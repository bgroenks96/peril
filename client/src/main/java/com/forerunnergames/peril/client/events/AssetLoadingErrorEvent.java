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

package com.forerunnergames.peril.client.events;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

import com.google.common.base.Throwables;

public class AssetLoadingErrorEvent implements LocalEvent
{
  private final AssetDescriptor <?> assetDescriptor;
  private final Throwable throwable;

  public AssetLoadingErrorEvent (final AssetDescriptor <?> assetDescriptor, final Throwable throwable)
  {
    Arguments.checkIsNotNull (assetDescriptor, "assetDescriptor");
    Arguments.checkIsNotNull (throwable, "throwable");

    this.assetDescriptor = assetDescriptor;
    this.throwable = throwable;
  }

  public AssetDescriptor <?> getAssetDescriptor ()
  {
    return assetDescriptor;
  }

  public String getFileName ()
  {
    return assetDescriptor.fileName;
  }

  public Class <?> getFileType ()
  {
    return assetDescriptor.type;
  }

  public FileHandle getFile ()
  {
    return assetDescriptor.file;
  }

  public Throwable getThrowable ()
  {
    return throwable;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}: [{}] | {}: [{}]", getClass ().getSimpleName (),
                           AssetDescriptor.class.getSimpleName (), assetDescriptor, Throwable.class.getSimpleName (),
                           Throwables.getStackTraceAsString (throwable));
  }
}
