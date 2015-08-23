package com.forerunnergames.peril.common.io;

import com.google.common.collect.ImmutableBiMap;

public interface DataLoader <T, U>
{
  ImmutableBiMap <T, U> load (final String fileName);
}
