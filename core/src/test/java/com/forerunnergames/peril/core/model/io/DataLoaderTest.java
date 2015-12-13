package com.forerunnergames.peril.core.model.io;

import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.io.DataLoader;

import org.junit.Test;

public abstract class DataLoaderTest <T>
{
  protected abstract DataLoader <T> createDataLoader ();

  protected abstract boolean verifyData (final T data);

  protected abstract String getTestDataFileName ();

  @Test
  public void testLoadSuccessful ()
  {
    final DataLoader <T> loader = createDataLoader ();
    final T actualData = loader.load (getTestDataFileName ());

    assertTrue (verifyData (actualData));
  }

  @Test (expected = RuntimeException.class)
  public void testLoadFailsFileNotFound ()
  {
    final DataLoader <T> loader = createDataLoader ();
    loader.load ("non-existent-file");
  }
}
