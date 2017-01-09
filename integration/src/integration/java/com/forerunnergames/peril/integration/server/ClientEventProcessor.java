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

package com.forerunnergames.peril.integration.server;

import com.forerunnergames.peril.integration.TestMonitor;
import com.forerunnergames.peril.integration.TestUtil;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Event;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import org.testng.collections.Sets;

/**
 * Utility type for sequential event handling across multiple clients. ClientEventProcessor processes all pending events
 * of interest for each client before proceeding to the next client (in order) until an event of a particular type is
 * reached. vent callback functions should be registered with the clients via
 * {@link #registerCallback(Class, ClientEventCallback)} so that ClientEventProcessor knows which events to look for in
 * the client event queues.
 *
 * @author Brian Groenke
 */
public final class ClientEventProcessor
{
  private final ImmutableSortedSet <TestClient> orderedClients;
  private final Set <Class <? extends Event>> registeredTypes = Sets.newHashSet ();
  private final Set <Runnable> completionTasks = Sets.newHashSet ();

  public ClientEventProcessor (final Iterable <TestClient> clients)
  {
    orderedClients = TestUtil.sortClientsByPlayerTurnOrder (clients);
  }

  public synchronized <T extends Event> void registerCallback (final Class <T> type,
                                                               final ClientEventCallback <T> callback)
  {
    registeredTypes.add (type);
    for (final TestClient client : orderedClients)
    {
      client.registerCallback (type, callback);
    }
  }

  public synchronized void registerCompletionTask (final Runnable task)
  {
    completionTasks.add (task);
  }

  /**
   * Calls start(endProcessingEventType, null)
   *
   * @param endProcessingEventType
   */
  public void start (final Class <? extends Event> endProcessingEventType)
  {
    start (endProcessingEventType, null);
  }

  /**
   * Begins processing of client events. This method will cycle through clients by turn order until each has received an
   * event of the type specified by 'endProcessingEventType'. After processing finishes, all registered types/callbacks,
   * and completion tasks are cleared.
   *
   * @param endProcessingEventType
   * @param monitor
   *          the TestMonitor instance to call {@link TestMonitor#checkIn()} on when finished
   */
  public void start (final Class <? extends Event> endProcessingEventType, final TestMonitor monitor)
  {
    Executors.newSingleThreadExecutor ().execute (new Runnable ()
    {
      @Override
      public void run ()
      {
        runUntil (endProcessingEventType);
        if (monitor != null) monitor.checkIn ();
        for (final Runnable task : completionTasks)
        {
          task.run ();
        }

        completionTasks.clear ();
      }
    });

  }

  public ImmutableSortedSet <TestClient> getClientsSortedByTurnOrder ()
  {
    return orderedClients;
  }

  private synchronized void runUntil (final Class <? extends Event> endProcessingEventType)
  {
    // add end processing type to registered type set
    registeredTypes.add (endProcessingEventType);
    final ConcurrentLinkedQueue <TestClient> clientProcessingQueue = new ConcurrentLinkedQueue<> (orderedClients);
    while (!clientProcessingQueue.isEmpty ())
    {
      final TestClient nextClient = clientProcessingQueue.poll ();

      Optional <Event> maybe;
      try
      {
        maybe = poll (nextClient);
        if (maybe.isPresent ())
        {
          final Event event = maybe.get ();
          if (event.getClass ().equals (endProcessingEventType))
          {
            nextClient.clearCallbacks ();
            continue;
          }
        }
      }
      catch (final InterruptedException e)
      {
        e.printStackTrace ();
      }

      // re-enqueue client for processing
      clientProcessingQueue.offer (nextClient);
    }

    // clear registered types
    registeredTypes.clear ();
  }

  private Optional <Event> poll (final TestClient client) throws InterruptedException
  {
    while (client.hasEventOfTypeInQueue (registeredTypes))
    {
      final Event event = client.pollNextEvent ();
      if (matchesAny (event)) return Optional.of (event);
    }

    return Optional.absent ();
  }

  private boolean matchesAny (final Event event)
  {
    for (final Class <?> type : registeredTypes)
    {
      if (type.isInstance (event)) return true;
    }

    return false;
  }
}
