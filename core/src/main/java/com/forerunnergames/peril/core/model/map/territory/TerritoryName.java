package com.forerunnergames.peril.core.model.map.territory;

public interface TerritoryName
{
  public String getName ();

  @Override
  public int hashCode ();

  @Override
  public boolean equals (final Object o);

  @Override
  public String toString ();

  public boolean isUnknown ();
}
