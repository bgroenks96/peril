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

package com.forerunnergames.peril.common.net.kryonet;

import com.esotericsoftware.minlog.Log;

import com.forerunnergames.tools.common.Classes;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * Utility class to route Kryonet library log messages through SLF4J.
 */
public final class KryonetLogging
{
  // Thread-safe singleton cache.
  private static final Supplier <Log.Logger> SUPPLIER = Suppliers.memoize (new Supplier <Log.Logger> ()
  {
    @Override
    public Log.Logger get ()
    {
      return new KryonetLogger ();
    }
  });

  public static void initialize ()
  {
    Log.setLogger (SUPPLIER.get ());
  }

  private KryonetLogging ()
  {
    Classes.instantiationNotAllowed ();
  }
}
