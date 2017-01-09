/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.core.model.io;

import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.io.DataLoader;

import org.junit.Test;

public abstract class DataLoaderTest <T>
{
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

  protected abstract DataLoader <T> createDataLoader ();

  protected abstract boolean verifyData (final T data);

  protected abstract String getTestDataFileName ();
}
