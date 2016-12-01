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

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.AbstractClientController;
import com.forerunnergames.tools.net.client.Client;
import com.forerunnergames.tools.net.client.remote.RemoteServer;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient extends AbstractClientController
{
  public static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
  public static final int DEFAULT_WAIT_TIMEOUT_MS = 6000;
  private static final Logger log = LoggerFactory.getLogger (TestClient.class);
  private static final int DEFAULT_MAX_ATTEMPTS = 2;
  private static final AtomicInteger clientCount = new AtomicInteger ();
  private final int clientId = clientCount.getAndIncrement ();
  private final ExecutorService exec = Executors.newCachedThreadPool ();
  private final BlockingQueue <Event> inboundEventQueue = new LinkedBlockingQueue<> ();
  private final Multimap <Class <?>, ClientEventCallback <?>> callbacks;
  private PlayerPacket player;

  public TestClient (final Client client)
  {
    super (client, KryonetRegistration.CLASSES);

    callbacks = Multimaps.synchronizedListMultimap (ArrayListMultimap.<Class <?>, ClientEventCallback <?>> create ());
  }

  @Override
  protected void onConnectionTo (final RemoteServer server)
  {
    Arguments.checkIsNotNull (server, "server");
  }

  @Override
  protected void onDisconnectionFrom (final RemoteServer server)
  {
    Arguments.checkIsNotNull (server, "server");
  }

  @Override
  protected void onCommunication (final RemoteServer server, final Object object)
  {
    Arguments.checkIsNotNull (server, "server");
    Arguments.checkIsNotNull (object, "object");

    final Event event = (Event) object;
    log.trace ("[{}] Event received: [{}]", this, event);
    inboundEventQueue.add (event);
  }

  public Result <String> connect (final ServerConfiguration serverConfig)
  {
    Arguments.checkIsNotNull (serverConfig, "serverConfig");

    return connectNow (serverConfig, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_MAX_ATTEMPTS);
  }

  public void sendEvent (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    if (!isConnected ()) throw new IllegalStateException ("Test client not yet connected!");
    send (event);
  }

  public void dispose ()
  {
    shutDown ();
  }

  public <T extends Event> void registerCallback (final Class <T> paramType, final ClientEventCallback <T> callback)
  {
    callbacks.put (paramType, callback);
  }

  public <T extends Event> boolean unregisterCallback (final ClientEventCallback <T> callback)
  {
    boolean removed = false;
    for (final Class <?> key : callbacks.keySet ())
    {
      removed = removed | callbacks.remove (key, callback);
    }

    return removed;
  }

  public void clearCallbacks ()
  {
    callbacks.clear ();
  }

  /**
   * Retrieves the next item in the inbound event queue; blocks if necessary.
   *
   * @return the next event in the queue
   * @throws InterruptedException
   */
  public Event pollNextEvent () throws InterruptedException
  {
    final Event next = inboundEventQueue.take ();
    updatePlayerDataIfPresent (next);
    invokeCallbacks (next);
    return next;
  }

  public <T> Optional <T> waitForEventCommunication (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return waitForEventCommunication (type, DEFAULT_WAIT_TIMEOUT_MS, false);
  }

  public <T> Optional <T> waitForEventCommunication (final Class <T> type, final boolean exceptionOnFail)
  {
    Arguments.checkIsNotNull (type, "type");

    return waitForEventCommunication (type, DEFAULT_WAIT_TIMEOUT_MS, exceptionOnFail);
  }

  public <T> Optional <T> waitForEventCommunication (final Class <T> type,
                                                     final long waitTimeoutMillis,
                                                     final boolean exceptionOnFail)
  {
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNegative (waitTimeoutMillis, "waitTimeoutMillis");

    final Exchanger <T> exchanger = new Exchanger<> ();
    final AtomicBoolean keepAlive = new AtomicBoolean (true);
    exec.execute (new Runnable ()
    {
      @Override
      public void run ()
      {
        try
        {
          while (keepAlive.get ())
          {
            final Event next = pollNextEvent ();
            if (type.isInstance (next))
            {
              log.debug ("[{}] Found event match: {}", TestClient.this, next);
              exchanger.exchange (type.cast (next));
              keepAlive.set (false);
            }
            else
            {
              log.warn ("[{}] Unexpected event: {} | Expected: {}", TestClient.this, next, type.getSimpleName ());
            }
          }
        }
        catch (final InterruptedException e)
        {
          log.warn ("Listener thread timed out on exchange.");
        }
      }
    });
    try
    {
      final T event = exchanger.exchange (null, waitTimeoutMillis, TimeUnit.MILLISECONDS);
      return Optional.fromNullable (event);
    }
    catch (InterruptedException | TimeoutException e)
    {
      if (exceptionOnFail) throw new IllegalStateException (Strings.format ("No events received of type {}.", type));
      return Optional.absent ();
    }
    finally
    {
      keepAlive.set (false);
    }
  }

  public boolean assertNoEventsReceived (final long waitTimeoutMillis)
  {
    Arguments.checkIsNotNegative (waitTimeoutMillis, "waitTimeoutMillis");

    if (inboundEventQueue.size () > 0) return false;
    try
    {
      Thread.sleep (waitTimeoutMillis);
    }
    catch (final InterruptedException e)
    {
      log.warn ("Interrupted during wait.");
    }
    return inboundEventQueue.isEmpty ();
  }

  public Optional <Event> getNextEventPendingInQueue ()
  {
    return Optional.fromNullable (inboundEventQueue.peek ());
  }

  public boolean nextEventHasType (final Class <?> type)
  {
    Arguments.checkIsNotNull (type, "type");

    final Optional <Event> nextEvent = getNextEventPendingInQueue ();
    if (!nextEvent.isPresent ()) return false;
    return type.isInstance (nextEvent.get ());
  }

  public boolean hasEventOfTypeInQueue (final Class <?>... types)
  {
    Arguments.checkIsNotNull (types, "types");

    for (final Event next : inboundEventQueue)
    {
      for (final Class <?> type : types)
      {
        if (type.isInstance (next)) return true;
      }
    }

    return false;
  }

  public boolean hasEventOfTypeInQueue (final Iterable <Class <? extends Event>> types)
  {
    return hasEventOfTypeInQueue (Iterables.toArray (types, Class.class));
  }

  public int getClientId ()
  {
    return clientId;
  }

  public PlayerPacket getPlayer ()
  {
    if (player == null) Exceptions.throwIllegalState ("Player has not been set.");
    return player;
  }

  public void setPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public void flushEventQueue ()
  {
    while (inboundEventQueue.size () > 0)
    {
      try
      {
        pollNextEvent ();
      }
      catch (final InterruptedException e)
      {
        e.printStackTrace ();
      }
    }
  }

  @SuppressWarnings ({ "unchecked", "rawtypes" })
  protected <T extends Event> void invokeCallbacks (final T event)
  {
    if (!callbacks.containsKey (event.getClass ())) return;
    final Collection <ClientEventCallback <?>> callbacks = this.callbacks.get (event.getClass ());
    log.debug ("Dispatching event of type [{}] to {} registered callback handlers...",
               event.getClass ().getSimpleName (), callbacks.size ());
    for (final ClientEventCallback callback : callbacks)
    {
      callback.onEventReceived (Optional.of (event), this);
    }
  }

  // automatically update player data from player events, if present
  protected void updatePlayerDataIfPresent (final Event event)
  {
    if (!(event instanceof PlayerEvent) || player == null) return;

    final PlayerEvent playerEvent = (PlayerEvent) event;
    if (!playerEvent.getPerson ().is (player)) return;
    player = playerEvent.getPerson ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}-{}", getClass ().getSimpleName (), clientId);
  }
}
