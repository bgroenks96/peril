package com.forerunnergames.peril.core.model.map.territory;

public interface TerritoryName
{
  public String getName();
  public boolean isUnknown();
  @Override
  public boolean equals (final Object o);
  @Override
  public int hashCode();
  @Override
  public String toString();
}
