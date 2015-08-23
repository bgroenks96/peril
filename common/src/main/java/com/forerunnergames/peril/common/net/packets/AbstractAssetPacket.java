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

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public boolean hasName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return name.equals (name);
  }

  @Override
  public boolean doesNotHaveName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return !hasName (name);
  }

  @Override
  public boolean hasId (final UUID id)
  {
    return this.id.equals (id);
  }

  @Override
  public boolean doesNotHaveId (final UUID id)
  {
    return !hasId (id);
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
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    return obj instanceof AbstractAssetPacket && ((AbstractAssetPacket) obj).id == id;
  }

  @Override
  public int hashCode ()
  {
    return id.hashCode ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Id: {}", getClass ().getSimpleName (), name, id);
  }

  @RequiredForNetworkSerialization
  private AbstractAssetPacket ()
  {
    name = null;
    id = null;
  }
}
