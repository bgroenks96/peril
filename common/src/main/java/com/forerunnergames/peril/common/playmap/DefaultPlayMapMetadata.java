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

package com.forerunnergames.peril.common.playmap;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayMapMetadata implements PlayMapMetadata
{
  private final String name;
  private final PlayMapType type;
  private final GameMode mode;
  private final String dirName;
  private final PlayMapDirectoryType dirType;

  public DefaultPlayMapMetadata (final String name,
                                 final PlayMapType type,
                                 final GameMode mode,
                                 final String dirName,
                                 final PlayMapDirectoryType dirType)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNull (mode, "mode");
    Arguments.checkIsNotNull (dirName, "dirName");
    Arguments.checkIsNotNull (dirType, "dirType");
    Preconditions.checkIsTrue (GameSettings.isValidPlayMapName (name),
                               Strings.format ("Invalid play map name [{}].", name));

    this.name = name;
    this.type = type;
    this.mode = mode;
    this.dirName = dirName;
    this.dirType = dirType;
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public String getDirName ()
  {
    return dirName;
  }

  @Override
  public boolean isDirType (final PlayMapDirectoryType dirType)
  {
    Arguments.checkIsNotNull (dirType, "dirType");

    return this.dirType == dirType;
  }

  @Override
  public PlayMapType getType ()
  {
    return type;
  }

  @Override
  public GameMode getMode ()
  {
    return mode;
  }

  @Override
  public int hashCode ()
  {
    int result = name.hashCode ();
    result = 31 * result + type.hashCode ();
    result = 31 * result + mode.hashCode ();
    return result;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    final PlayMapMetadata metadata = (PlayMapMetadata) obj;

    return name.equals (metadata.getName ()) && type == metadata.getType () && mode == metadata.getMode ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Type: {} | Mode: {} | DirName: {} | DirType: {}",
                           getClass ().getSimpleName (), name, type, mode, dirName, dirType);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayMapMetadata ()
  {
    name = null;
    type = null;
    mode = null;
    dirName = null;
    dirType = null;
  }
}
