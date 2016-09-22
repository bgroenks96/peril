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

package com.forerunnergames.peril.common.net.packets;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public abstract class AbstractAssetPacket implements AssetPacket
{
  private final String name;
  private final UUID id;

  protected AbstractAssetPacket (final String name, final UUID id)
  {
    Arguments.checkIsNotNull (name, "name");

    this.name = name;
    this.id = id;
  }

  @RequiredForNetworkSerialization
  protected AbstractAssetPacket ()
  {
    name = null;
    id = null;
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public boolean doesNotHaveId (final UUID id)
  {
    return !hasId (id);
  }

  @Override
  public boolean doesNotHaveName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return !hasName (name);
  }

  @Override
  public boolean hasName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return this.name.equals (name);
  }

  @Override
  public boolean hasId (final UUID id)
  {
    return this.id.equals (id);
  }

  @Override
  public boolean is (final AssetPacket packet)
  {
    Arguments.checkIsNotNull (packet, "packet");

    return equals (packet);
  }

  @Override
  public boolean isNot (final AssetPacket packet)
  {
    Arguments.checkIsNotNull (packet, "packet");

    return !is (packet);
  }

  @Override
  public int hashCode ()
  {
    return id.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    return obj instanceof AbstractAssetPacket && ((AbstractAssetPacket) obj).id.equals (id);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Id: {}", getClass ().getSimpleName (), name, id);
  }
}
