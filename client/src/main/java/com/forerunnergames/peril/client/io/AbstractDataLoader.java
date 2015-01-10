package com.forerunnergames.peril.client.io;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractDataLoader <T, U> implements DataLoader <T, U>
{
  @Override
  public final ImmutableMap <T, U> load (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    initializeData (fileName);

    while (readData ())
    {
      saveData ();
    }

    return finalizeData ();
  }

  protected abstract ImmutableMap <T, U> finalizeData ();

  protected abstract void initializeData (final String fileName);

  protected abstract boolean readData ();

  protected abstract void saveData ();
}
