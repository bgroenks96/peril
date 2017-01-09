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

package com.forerunnergames.peril.integration;

import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.pool.PoolFactory;
import com.forerunnergames.tools.common.pool.Pools;
import com.forerunnergames.tools.common.pool.RecyclableObjectPool;
import com.forerunnergames.tools.net.NetworkConstants;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a limited pool of local network port numbers for concurrent use by test providers.
 */
public final class NetworkPortPool
{
  private static final Logger log = LoggerFactory.getLogger (NetworkPortPool.class);
  private static final int PORT_POOL_SIZE = 20;
  // Thread-safe singleton cache.
  private static final Supplier <NetworkPortPool> SUPPLIER = Suppliers.memoize (new Supplier <NetworkPortPool> ()
  {
    @Override
    public NetworkPortPool get ()
    {
      return new NetworkPortPool ();
    }
  });
  private final RecyclableObjectPool <Integer> portNumberPool;

  // Thread-safe singleton cache.
  public static NetworkPortPool getInstance ()
  {
    return SUPPLIER.get ();
  }

  public int getAvailablePort ()
  {
    try
    {
      while (!portNumberPool.canAcquire ())
      {
        Thread.yield ();
        Thread.sleep (50);
      }
      return portNumberPool.acquire ().get ();
    }
    catch (final InterruptedException e)
    {
      log.warn ("Interrupted while waiting to acquire port: ", e);
    }
    return -1;
  }

  public void releasePort (final int value)
  {
    portNumberPool.release (value);
  }

  private static int findAvailablePortFrom (final int initialValue)
  {
    boolean found = false;
    int nextPort = initialValue;
    while (!found && nextPort < NetworkConstants.MAX_PORT)
    {
      try (final ServerSocket serv = new ServerSocket (nextPort))
      {
        log.trace ("Found available port {}", nextPort);
        found = true;
      }
      catch (final IOException e)
      {
        log.debug ("Port {} not available! Checking {}...", nextPort, ++nextPort);
      }
    }
    return nextPort;
  }

  private NetworkPortPool ()
  {
    portNumberPool = Pools.createSoftRecyclablePool (int.class, new PoolFactory <Integer> ()
    {
      int portValue = NetworkSettings.DEFAULT_TCP_PORT;

      @Override
      public Integer make ()
      {
        portValue = findAvailablePortFrom (portValue);
        return portValue++;
      }
    });
    portNumberPool.allocate (PORT_POOL_SIZE);
  }
}
