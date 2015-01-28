package com.forerunnergames.peril.client.io;

import com.google.common.collect.ImmutableBiMap;

public interface DataLoader <T, U>
{
  public ImmutableBiMap <T, U> load (final String fileName);
}
