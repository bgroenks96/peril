package com.forerunnergames.peril.core.model.map.territory;

public interface TerritoryName
{
  String asFileName (final String fileExtension);

  String asString ();

  boolean is (final String name);

  boolean isNot (final String name);

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object o);

  @Override
  String toString ();

  boolean isUnknown ();
}
