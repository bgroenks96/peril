package com.forerunnergames.peril.core.shared.io;

import com.google.common.collect.ImmutableBiMap;

public interface DataLoader <T, U>
{
  ImmutableBiMap <T, U> load (final String fileName);
}
