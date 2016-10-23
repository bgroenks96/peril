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

package com.forerunnergames.peril.server.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.common.net.kryonet.KryonetLogging;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.net.kryonet.KryonetRemote;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.NetworkListener;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KryonetServer extends com.esotericsoftware.kryonet.Server implements Server
{
  private static final Logger log = LoggerFactory.getLogger (KryonetServer.class);
  private final Map <NetworkListener, Listener> networkToKryonetListeners = new HashMap <> ();
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
  public void add (final NetworkListener networkListener)
  {
    Arguments.checkIsNotNull (networkListener, "networkListener");

    final Listener kryonetListener = new Listener ()
    {
      @Override
      public void connected (final Connection connection)
      {
        if (connection == null) return;

        networkListener.connected (new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }

      @Override
      public void disconnected (final Connection connection)
      {
        if (connection == null) return;

        networkListener.disconnected (new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }

      @Override
      public void received (final Connection connection, final Object object)
      {
        if (object instanceof FrameworkMessage) return;
        if (object == null)
        {
          log.warn ("Received null object from client: [{}].", connection.getRemoteAddressTCP ());
          return;
        }

        networkListener.received (object, new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }
    };

    addListener (kryonetListener);

    networkToKryonetListeners.put (networkListener, kryonetListener);
  }

  @Override
  public void remove (final NetworkListener networkListener)
  {
    Arguments.checkIsNotNull (networkListener, "networkListener");

    removeListener (networkToKryonetListeners.remove (networkListener));
  }

  @Override
  public void register (final Class <?> type)
  {
    Arguments.checkIsNotNull (type, "type");

    kryo.register (type);

    log.trace ("Registered class [{}] with the server for network serialization.", type);
  }

  @Override
  public void start (final int tcpPort)
  {
    Arguments.checkIsNotNegative (tcpPort, "tcpPort");

    start (new InetSocketAddress (tcpPort));
  }

  @Override
  public void start (final InetSocketAddress addressWithTcpPort)
  {
    Arguments.checkIsNotNull (addressWithTcpPort, "addressWithTcpPort");

    if (isRunning)
    {
      log.warn ("Cannot start the server because it's already running.");

      return;
    }

    start ();

    try
    {
      bind (addressWithTcpPort, null);

      isRunning = true;

      log.info ("Started the server on address [{}] & port [{}] (TCP).", addressWithTcpPort.getAddress (),
                addressWithTcpPort.getPort ());
    }
    catch (final IOException e)
    {
      super.stop ();

      isRunning = false;

      log.error ("Could not start the server on address [{}] & port [{}] (TCP). Reason: [{}].",
                 addressWithTcpPort.getAddress (), addressWithTcpPort.getPort (), Strings.toString (e));
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
  public boolean isConnected (final Remote client)
  {
    if (!isRunning) return false;

    for (final Connection connection : getConnections ())
    {
      if (isMatch (connection, client)) return true;
    }

    return false;
  }

  @Override
  public void disconnect (final Remote client)
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
  public void sendTo (final Remote client, final Object object)
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
  public void sendToAllExcept (final Remote client, final Object object)
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

  private static boolean addressMatches (final Connection connection, final Remote client)
  {
    return client.has (connection.getRemoteAddressTCP ()) || client.has (connection.getRemoteAddressUDP ());
  }

  private static boolean idMatches (final int connectionId, final Remote client)
  {
    return client.hasConnectionId (connectionId);
  }

  private boolean isMatch (final Connection connection, final Remote client)
  {
    return idMatches (connection.getID (), client) && addressMatches (connection, client);
  }
}
