package com.forerunnergames.peril.client.assets;

import java.io.File;

public interface S3AssetMetadata
{
  String getKey ();

  File getFile ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object o);

  @Override
  String toString ();
}
