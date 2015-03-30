package com.forerunnergames.peril.core.model.map.territory;

import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractTerritoryName implements TerritoryName
{
  private final String name;

  protected AbstractTerritoryName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    this.name = name;
  }

  @Override
  public String asString ()
  {
    return name;
  }

  @Override
  public boolean is (String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return this.name.equals (name);
  }

  @Override
  public boolean isNot (String name)
  {
    return !is (name);
  }

  @Override
  public boolean isUnknown ()
  {
    return name.isEmpty ();
  }

  @Override
  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o == null || getClass () != o.getClass ()) return false;

    AbstractTerritoryName that = (AbstractTerritoryName) o;

    return name.equals (that.name);
  }

  @Override
  public int hashCode ()
  {
    return name.hashCode ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), name);
  }
}
