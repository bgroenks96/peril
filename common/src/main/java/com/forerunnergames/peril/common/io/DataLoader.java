package com.forerunnergames.peril.common.io;

public interface DataLoader <T>
{
  T load (final String fileName);
}
