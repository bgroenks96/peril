package com.forerunnergames.peril.common.io;

import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractDataLoader <T> implements DataLoader <T>
{
  @Override
  public final T load (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    initializeData (fileName);

    while (readData ())
    {
      saveData ();
    }

    return finalizeData ();
  }

  protected abstract T finalizeData ();

  protected abstract void initializeData (final String fileName);

  protected abstract boolean readData ();

  protected abstract void saveData ();
}
