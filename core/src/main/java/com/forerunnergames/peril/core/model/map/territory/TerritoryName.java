package com.forerunnergames.peril.core.model.map.territory;

public interface TerritoryName
{
  String getName ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object o);

  @Override
  String toString ();

  boolean isUnknown ();
}
