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

import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.AbstractClientController;
import com.forerunnergames.tools.net.client.Client;

import com.google.common.base.Optional;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient extends AbstractClientController
{
  private static final Logger log = LoggerFactory.getLogger (TestClient.class);
  private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
  private static final int DEFAULT_WAIT_TIMEOUT_MS = 6000;
  private static final int DEFAULT_MAX_ATTEMPTS = 2;
  private static final AtomicInteger clientCount = new AtomicInteger ();
  private final int clientId = clientCount.getAndIncrement ();
  private final ExecutorService exec = Executors.newCachedThreadPool ();
  private final ConcurrentLinkedQueue <Event> inboundEventQueue = new ConcurrentLinkedQueue <> ();
  private PlayerPacket player;

  public TestClient (final Client client)
  {
    super (client, KryonetRegistration.CLASSES);
  }

  public Result <String> connect (final String addr, final int tcpPort)
  {
    Arguments.checkIsNotNull (addr, "addr");
    Arguments.checkIsNotNegative (tcpPort, "tcpPort");

    return connect (addr, tcpPort, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_MAX_ATTEMPTS);
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
    Arguments.checkIsNotNegative (waitTimeoutMillis, "waitTimeoutMillis");

    Arguments.checkIsNotNull (type, "type");

    final Exchanger <T> exchanger = new Exchanger <> ();
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
            final Event next = inboundEventQueue.poll ();
            if (next == null)
            {
              Thread.sleep (10);
              continue;
            }
            log.debug ("[{}] Event received: Type: {} | Expected: {}", this, next.getClass (), type);
            log.trace ("[{}] Event data: [{}]", this, next);
            if (type.isInstance (next))
            {
              exchanger.exchange (type.cast (next));
              keepAlive.set (false);
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

  public int getClientId ()
  {
    return clientId;
  }

  public void setPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public PlayerPacket getPlayer ()
  {
    if (player == null) Exceptions.throwIllegalState ("Player has not been set.");
    return player;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}-{}", getClass ().getSimpleName (), clientId);
  }

  @Override
  protected void onConnectionTo (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");
  }

  @Override
  protected void onDisconnectionFrom (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");
  }

  @Override
  protected void onCommunication (final Object object, final Remote server)
  {
    Arguments.checkIsNotNull (object, "object");
    Arguments.checkIsNotNull (server, "server");

    inboundEventQueue.offer ((Event) object);
  }
}
