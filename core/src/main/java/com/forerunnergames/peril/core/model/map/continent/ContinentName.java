package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.Arguments;

public final class ContinentName implements TerritoryName
{
  private final String name;

  public ContinentName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    this.name = name;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public boolean isUnknown()
  {
    return name.isEmpty();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ContinentName that = (ContinentName) o;

    return name.equals (that.name);
  }

  @Override
  public int hashCode()
  {
    return name.hashCode();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), name);
  }
}
