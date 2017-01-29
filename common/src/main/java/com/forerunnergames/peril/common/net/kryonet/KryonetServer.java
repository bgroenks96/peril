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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.Server;
import com.forerunnergames.tools.net.server.remote.RemoteClient;
import com.forerunnergames.tools.net.server.remote.RemoteClientListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KryonetServer extends com.esotericsoftware.kryonet.Server implements Server
{
  private static final Logger log = LoggerFactory.getLogger (KryonetServer.class);
  private final Map <Integer, RemoteClient> connectionIdsToRemoteClients = new HashMap <> ();
  private final Map <RemoteClientListener, Listener> remoteClientToKryonetListeners = new HashMap <> ();
  private final Kryo kryo;
  private boolean isRunning = false;

  public KryonetServer ()
  {
    super (NetworkSettings.SERVER_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES,
           NetworkSettings.SERVER_SERIALIZATION_READ_BUFFER_SIZE_BYTES);

    kryo = getKryo ();

    KryonetLogging.initialize ();
    KryonetRegistration.initialize (kryo);
    KryonetRegistration.registerCustomSerializers (kryo);
  }

  @Override
  public void stop ()
  {
    if (!isRunning) return;

    super.stop ();

    isRunning = false;

    log.info ("Stopped the server.");
  }

  @Override
  public void add (final RemoteClientListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final Listener kryonetListener = new Listener ()
    {
      @Override
      public void connected (final Connection connection)
      {
        Arguments.checkIsNotNull (connection, "connection");

        RemoteClient client = connectionIdsToRemoteClients.get (connection.getID ());

        if (client != null)
        {
          log.error ("Duplicate client [{}] connected! Connection: [{}]. Not notifying listener: [{}].", client,
                     connection, listener);
          return;
        }

        client = new KryonetRemoteClient (connection.getID (), connection.getRemoteAddressTCP ());
        connectionIdsToRemoteClients.put (connection.getID (), client);

        listener.connected (client);
      }

      @Override
      public void disconnected (final Connection connection)
      {
        Arguments.checkIsNotNull (connection, "connection");

        final RemoteClient client = connectionIdsToRemoteClients.get (connection.getID ());

        if (client == null)
        {
          log.error ("Non-connected client disconnected! Connection: [{}]. Not notifying listener: [{}].", connection,
                     listener);
          return;
        }

        connectionIdsToRemoteClients.remove (connection.getID ());

        listener.disconnected (client);
      }

      @Override
      public void received (final Connection connection, @Nullable final Object object)
      {
        Arguments.checkIsNotNull (connection, "connection");

        final RemoteClient client = connectionIdsToRemoteClients.get (connection.getID ());

        if (client == null)
        {
          log.error ("Received object [{}] from non-connected client! Connection: [{}]. Not notifying listener: [{}].",
                     object, connection, listener);
          return;
        }

        if (object instanceof FrameworkMessage) return;

        if (object == null)
        {
          log.warn ("Received null object from client: [{}].", connection.getRemoteAddressTCP ());
          return;
        }

        listener.received (client, object);
      }
    };

    addListener (kryonetListener);

    remoteClientToKryonetListeners.put (listener, kryonetListener);
  }

  @Override
  public void remove (final RemoteClientListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    removeListener (remoteClientToKryonetListeners.remove (listener));
  }

  @Override
  public void register (final Class <?> type)
  {
    Arguments.checkIsNotNull (type, "type");

    kryo.register (type);

    log.trace ("Registered class [{}] with the server for network serialization.", type);
  }

  @Override
  public void start (final int port)
  {
    Arguments.checkIsNotNegative (port, "port");

    start (new InetSocketAddress (port));
  }

  @Override
  public void start (final InetSocketAddress addressWithPort)
  {
    Arguments.checkIsNotNull (addressWithPort, "addressWithPort");

    if (isRunning)
    {
      log.warn ("Cannot start the server because it's already running.");

      return;
    }

    start ();

    try
    {
      bind (addressWithPort, null);

      isRunning = true;

      log.info ("Started the server on address [{}] & port [{}] (TCP).", addressWithPort.getAddress (),
                addressWithPort.getPort ());
    }
    catch (final IOException e)
    {
      super.stop ();

      isRunning = false;

      log.error ("Could not start the server on address [{}] & port [{}] (TCP). Reason: [{}].",
                 addressWithPort.getAddress (), addressWithPort.getPort (), Strings.toString (e));
    }
  }

  @Override
  public void shutDown ()
  {
    if (!isRunning) return;

    close ();
    stop ();

    log.info ("Shut down the server.");
  }

  @Override
  public boolean isConnected (final RemoteClient client)
  {
    if (!isRunning) return false;

    for (final Connection connection : getConnections ())
    {
      if (isMatch (connection, client)) return true;
    }

    return false;
  }

  @Override
  public void disconnect (final RemoteClient client)
  {
    if (!isRunning) return;

    for (final Connection connection : getConnections ())
    {
      if (isMatch (connection, client)) connection.close ();
    }

    log.info ("Disconnected client [{}] from server.", client);
  }

  @Override
  public void disconnectAll ()
  {
    if (!isRunning) return;

    close ();

    log.info ("Disconnected all clients.");
  }

  @Override
  public void sendTo (final RemoteClient client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    if (!isRunning)
    {
      log.warn ("Prevented sending object [{}] to client [{}] because the server isn't running.", object, client);

      return;
    }

    if (!isConnected (client))
    {
      log.warn ("Prevented sending object [{}] to client [{}] because that client isn't connected to the server.",
                object, client);

      return;
    }

    sendToTCP (client.getConnectionId (), object);

    log.debug ("Sent object [{}] to client [{}].", object, client);
  }

  @Override
  public void sendToAll (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    if (!isRunning)
    {
      log.warn ("Prevented sending object [{}] to all clients because the server isn't running.", object);

      return;
    }

    sendToAllTCP (object);

    log.debug ("Sent [{}] to all clients.", object);
  }

  @Override
  public void sendToAllExcept (final RemoteClient client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    if (!isRunning)
    {
      log.warn ("Prevented sending object [{}] to all clients except [{}] because the server isn't running.", object,
                client);

      return;
    }

    sendToAllExceptTCP (client.getConnectionId (), object);

    log.debug ("Sent object [{}] to all clients except [{}].", object, client);
  }

  private static boolean addressMatches (final Connection connection, final RemoteClient client)
  {
    return client.has (connection.getRemoteAddressTCP ()) || client.has (connection.getRemoteAddressUDP ());
  }

  private static boolean idMatches (final int connectionId, final RemoteClient client)
  {
    return client.hasConnectionId (connectionId);
  }

  private boolean isMatch (final Connection connection, final RemoteClient client)
  {
    return idMatches (connection.getID (), client) && addressMatches (connection, client);
  }
}
