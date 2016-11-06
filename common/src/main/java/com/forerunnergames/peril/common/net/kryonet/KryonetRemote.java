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

package com.forerunnergames.peril.common.net.kryonet;

import com.forerunnergames.tools.net.Remote;

import java.net.InetSocketAddress;
import java.util.Objects;

import javax.annotation.Nullable;

public final class KryonetRemote implements Remote
{
  private final int connectionId;
  @Nullable
  private final InetSocketAddress address;

  public KryonetRemote (final int connectionId, @Nullable final InetSocketAddress address)
  {
    this.connectionId = connectionId;
    this.address = address;
  }

  @Override
  public int hashCode ()
  {
    return connectionId;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (!(obj instanceof KryonetRemote)) return false;

    final KryonetRemote that = (KryonetRemote) obj;

    return connectionId == that.connectionId;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Connection Id: %2$s | Address: %3$s", getClass ().getSimpleName (), connectionId,
                          address);
  }

  @Override
  public int getConnectionId ()
  {
    return connectionId;
  }

  @Override
  public boolean hasConnectionId (final int connectionId)
  {
    return connectionId == this.connectionId;
  }

  @Override
  public boolean hasAddress ()
  {
    return address != null;
  }

  @Override
  public boolean hasPort ()
  {
    return address != null;
  }

  @Override
  public boolean hasAddressAndPort ()
  {
    return address != null;
  }

  @Override
  public boolean hasPort (final int port)
  {
    return address != null && address.getPort () == port;
  }

  @Override
  public boolean has (final InetSocketAddress address)
  {
    return Objects.equals (this.address, address);
  }

  @Override
  public boolean is (final Remote remote)
  {
    return equals (remote);
  }

  @Override
  public boolean isNot (final Remote remote)
  {
    return !is (remote);
  }

  @Override
  public String getAddress ()
  {
    return address != null && address.getAddress () != null ? address.getAddress ().getHostAddress () : "";
  }

  @Override
  public int getPort ()
  {
    return address != null ? address.getPort () : -1;
  }
}
