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

package com.forerunnergames.peril.common.map;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class DefaultMapMetadata implements MapMetadata
{
  private final String name;
  private final MapType type;
  private final GameMode mode;

  public DefaultMapMetadata (final String name, final MapType type, final GameMode mode)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNull (mode, "mode");
    Preconditions.checkIsTrue (GameSettings.isValidMapName (name), Strings.format ("Invalid map name [{}].", name));

    this.name = name;
    this.type = type;
    this.mode = mode;
  }

  @Override
  public final String getName ()
  {
    return name;
  }

  @Override
  public final MapType getType ()
  {
    return type;
  }

  @Override
  public final GameMode getMode ()
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

    final MapMetadata mapMetadata = (MapMetadata) obj;

    return name.equals (mapMetadata.getName ()) && type == mapMetadata.getType () && mode == mapMetadata.getMode ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Type: {} | Mode: {}", getClass ().getSimpleName (), name, type, mode);
  }

  @RequiredForNetworkSerialization
  private DefaultMapMetadata ()
  {
    name = null;
    type = null;
    mode = null;
  }
}
