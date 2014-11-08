package com.forerunnergames.peril.client.io;

import com.google.common.collect.ImmutableMap;

public interface DataLoader <T, U>
{
  public ImmutableMap <T, U> load (final String fileName);
}
