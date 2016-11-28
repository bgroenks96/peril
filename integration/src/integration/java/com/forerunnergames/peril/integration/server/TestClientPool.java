/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.integration.server;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import com.forerunnergames.peril.client.net.KryonetClient;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Strings;

import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.collections.Lists;

public class TestClientPool implements Iterable <TestClient>
{
  private static final Logger log = LoggerFactory.getLogger (TestClientPool.class);
  private static final int MAX_THREADS = 2;
  private final List <TestClient> clients = Collections.synchronizedList (new ArrayList <TestClient> ());
  private final ExecutorService clientThreadPool = Executors.newFixedThreadPool (MAX_THREADS);
  private final AtomicInteger pendingOperationCount = new AtomicInteger ();

  public TestClientPool ()
  {
  }

  @Override
  public Iterator <TestClient> iterator ()
  {
    return clients.iterator ();
  }

  public synchronized void connectNew (final ServerConfiguration serverConfig)
  {
    Arguments.checkIsNotNull (serverConfig, "serverConfig");

    pendingOperationCount.incrementAndGet ();
    clientThreadPool.execute (new Runnable ()
    {
      @Override
      public void run ()
      {
        final TestClient newClient = new TestClient (new KryonetClient ());
        newClient.initialize ();
        assertTrue (newClient.connect (serverConfig).isSuccessful ());
        log.debug ("Successfully connected client [{}]", newClient.getClientId ());
        clients.add (newClient);
        pendingOperationCount.decrementAndGet ();
      }
    });
  }

  public void connectNew (final ServerConfiguration serverConfig, final int count)
  {
    Arguments.checkIsNotNull (serverConfig, "serverConfig");
    Arguments.checkIsNotNegative (count, "count");

    for (int i = 0; i < count; i++)
    {
      log.debug ("Attempting to connect client {} to {}", i, serverConfig);
      connectNew (serverConfig);
    }
  }

  public synchronized void waitForAllClients ()
  {
    try
    {
      while (pendingOperationCount.get () > 0)
      {
        Thread.yield ();
        Thread.sleep (5);
      }
    }
    catch (final InterruptedException e)
    {
      log.warn ("Interrupted while waiting for pending client operations [pending count is {}]",
                pendingOperationCount.get ());
    }
  }

  /**
   * @return set of clients that did not receive the event
   */
  public <T extends Event> ImmutableSet <TestClient> waitForAllClientsToReceive (final Class <T> eventType)
  {
    return waitForAllClientsToReceive (eventType, TestClient.DEFAULT_WAIT_TIMEOUT_MS);
  }

  /**
   * @return set of clients that did not receive the event
   */
  public <T extends Event> ImmutableSet <TestClient> waitForAllClientsToReceive (final Class <T> eventType,
                                                                                 final long timeout)
  {
    Arguments.checkIsNotNull (eventType, "eventType");

    return waitForAllClientsToReceive (eventType, timeout, new ClientEventCallback <T> ()
    {
      @Override
      public void onEventReceived (final Optional <T> event, final TestClient client)
      {
        Arguments.checkIsNotNull (event, "event");
        Arguments.checkIsNotNull (client, "client");

        if (!event.isPresent ())
        {
          fail (Strings.format ("No event of type [{}] received by client [{}]", eventType, client));
        }
      }
    });
  }

  public <T extends Event> ImmutableSet <TestClient> waitForAllClientsToReceive (final Class <T> eventType,
                                                                                 final ClientEventCallback <T> callback)
  {
    return waitForAllClientsToReceive (eventType, TestClient.DEFAULT_WAIT_TIMEOUT_MS, callback);
  }

  /**
   * @return set of clients that did not receive the event
   */
  public <T extends Event> ImmutableSet <TestClient> waitForAllClientsToReceive (final Class <T> eventType,
                                                                                 final long timeout,
                                                                                 final ClientEventCallback <T> callback)
  {
    Arguments.checkIsNotNull (eventType, "eventType");
    Arguments.checkIsNotNull (callback, "callback");

    final ImmutableSet.Builder <TestClient> failed = ImmutableSet.builder ();

    for (final TestClient client : ImmutableList.copyOf (clients))
    {
      pendingOperationCount.incrementAndGet ();
      clientThreadPool.execute (new Runnable ()
      {
        @Override
        public void run ()
        {
          try
          {
            final Optional <T> event = client.waitForEventCommunication (eventType, timeout, false);
            if (!event.isPresent ()) failed.add (client);
            callback.onEventReceived (event, client);
          }
          catch (final Throwable t)
          {
            log.warn ("Executor caught error: ", t);
          }
          finally
          {
            pendingOperationCount.decrementAndGet ();
          }
        }
      });
    }
    waitForAllClients ();
    return failed.build ();
  }

  public int indexOf (final TestClient client)
  {
    Arguments.checkIsNotNull (client, "client");

    return clients.indexOf (client);
  }

  public TestClient get (final int clientIndex)
  {
    Arguments.checkIsNotNegative (clientIndex, "clientIndex");

    return clients.get (clientIndex);
  }

  public int count ()
  {
    return clients.size ();
  }

  public synchronized void send (final int clientIndex, final Event event)
  {
    Arguments.checkIsNotNegative (clientIndex, "clientIndex");
    Arguments.checkIsNotNull (event, "event");

    clients.get (clientIndex).sendEvent (event);
  }

  public void sendAll (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    for (final TestClient client : clients)
    {
      clientThreadPool.execute (new Runnable ()
      {
        @Override
        public void run ()
        {
          client.send (event);
        }
      });
    }
  }

  public synchronized <T extends Event> void registerCallback (final Class <T> paramType,
                                                               final ClientEventCallback <T> callback)
  {
    for (final TestClient client : clients)
    {
      client.registerCallback (paramType, callback);
    }
  }

  public synchronized void unregisterCallback (final ClientEventCallback <?> callback)
  {
    for (final TestClient client : clients)
    {
      client.unregisterCallback (callback);
    }
  }

  /**
   * Calls {@link TestClient#flushEventQueue()} for all clients in the pool.
   */
  public synchronized void flushAll ()
  {
    for (final TestClient client : clients)
    {
      client.flushEventQueue ();
    }
  }

  public synchronized void dispose (final int clientIndex)
  {
    Arguments.checkIsNotNegative (clientIndex, "clientIndex");

    clients.get (clientIndex).dispose ();
    clients.remove (clientIndex);
  }

  public void disposeAll ()
  {
    for (final TestClient client : clients)
    {
      client.dispose ();
    }
    clients.clear ();
  }

  /**
   * @return a new TestClientPool that holds references to all of this pool's clients except for the given client
   */
  public TestClientPool except (final TestClient client)
  {
    if (!clients.contains (client)) Exceptions.throwIllegalArg ("[{}] does not exist in this pool!", client);

    final List <TestClient> copy = Lists.newArrayList (clients);
    copy.remove (client);
    return new TestClientPool (copy);
  }

  private TestClientPool (final Collection <TestClient> clients)
  {
    Arguments.checkIsNotNull (clients, "clients");
    Arguments.checkHasNoNullElements (clients, "clients");

    this.clients.addAll (clients);
  }

  public interface ClientEventCallback <T extends Event>
  {
    void onEventReceived (Optional <T> event, TestClient client);
  }
}
