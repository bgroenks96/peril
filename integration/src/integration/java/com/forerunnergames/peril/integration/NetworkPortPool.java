package com.forerunnergames.peril.integration;

import com.esotericsoftware.minlog.Log;

import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.pool.PoolFactory;
import com.forerunnergames.tools.common.pool.Pools;
import com.forerunnergames.tools.common.pool.RecyclableObjectPool;

/**
 * Manages a limited pool of local network port numbers for concurrent use by test providers.
 */
public class NetworkPortPool
{
  private static final int PORT_POOL_SIZE = 5;
  private static NetworkPortPool provider;
  private final RecyclableObjectPool <Integer> portNumberPool;

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
      Log.warn ("Interrupted while waiting to acquire port: ", e);
    }
    return -1;
  }

  public void releasePort (final int value)
  {
    portNumberPool.release (value);
  }

  public static NetworkPortPool getInstance ()
  {
    // lazy initialize of singleton instance
    if (provider == null)
    {
      provider = new NetworkPortPool ();
    }
    return provider;
  }

  private NetworkPortPool ()
  {
    portNumberPool = Pools.createSoftRecyclablePool (int.class, new PoolFactory <Integer> ()
    {
      int portValue = NetworkSettings.DEFAULT_TCP_PORT;

      @Override
      public Integer make ()
      {
        return portValue++;
      }
    });
    portNumberPool.allocate (PORT_POOL_SIZE);

    provider = this;
  }
}
