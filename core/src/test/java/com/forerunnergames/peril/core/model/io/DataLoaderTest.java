package com.forerunnergames.peril.core.model.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.forerunnergames.peril.core.shared.io.DataLoader;

import com.google.common.collect.ImmutableBiMap;

import java.util.Collection;

import org.hamcrest.Matcher;

import org.junit.Test;

public abstract class DataLoaderTest <T, V>
{
  protected abstract DataLoader <T, V> createDataLoader ();

  protected abstract Collection <Matcher <? super V>> getDataMatchers ();

  protected abstract String getTestDataFileName ();

  @Test
  public void testLoadSuccessful ()
  {
    final DataLoader <T, V> loader = createDataLoader ();
    final ImmutableBiMap <T, V> actualData = loader.load (getTestDataFileName ());

    assertThat (actualData.values (), containsInAnyOrder (getDataMatchers ()));
  }

  @Test (expected = RuntimeException.class)
  public void testLoadFailsFileNotFound ()
  {
    final DataLoader <T, V> loader = createDataLoader ();
    loader.load ("non-existent-file");
  }
}
