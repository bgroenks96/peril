package com.forerunnergames.peril.core.shared.net.packets;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractAssetPacket implements AssetPacket
{
  private final String name;
  private final int id;

  protected AbstractAssetPacket (final String name, final int id)
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
  public boolean hasId (final int id)
  {
    return this.id == id;
  }

  @Override
  public boolean doesNotHaveId (final int id)
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
    return id;
  }

  @RequiredForNetworkSerialization
  private AbstractAssetPacket ()
  {
    name = null;
    id = 0;
  }
}
