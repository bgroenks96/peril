package com.forerunnergames.peril.core.shared.net.kryonet;

import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

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
    return address != null ? address.getAddress ().getHostAddress () : "";
  }

  @Override
  public int getPort ()
  {
    return address != null ? address.getPort () : -1;
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
    return String.format ("%1$s: Connection Id: %2$s | Address: %3$s", ((Object) this).getClass ().getSimpleName (),
                          connectionId, address);
  }

  @RequiredForNetworkSerialization
  private KryonetRemote ()
  {
    connectionId = -1;
    address = null;
  }
}
