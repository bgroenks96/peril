package com.forerunnergames.peril.server.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRemote;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.NetworkListener;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objenesis.strategy.StdInstantiatorStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KryonetServer extends com.esotericsoftware.kryonet.Server implements Server
{
  private static final Logger log = LoggerFactory.getLogger (KryonetServer.class);
  private final Map <NetworkListener, Listener> listeners = new HashMap <> ();
  private final Kryo kryo;
  private boolean isRunning = false;

  public KryonetServer ()
  {
    kryo = getKryo ();
    kryo.setInstantiatorStrategy (new StdInstantiatorStrategy ());
  }

  @Override
  public void add (final NetworkListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final Listener kryonetListener = new Listener ()
    {
      @Override
      public void connected (final Connection connection)
      {
        if (connection == null) return;

        listener.connected (new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }

      @Override
      public void disconnected (final Connection connection)
      {
        if (connection == null) return;

        listener.disconnected (new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }

      @Override
      public void received (final Connection connection, final Object object)
      {
        if (object == null || object instanceof FrameworkMessage) return;

        listener.received (object, new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }
    };

    addListener (kryonetListener);

    listeners.put (listener, kryonetListener);
  }

  @Override
  public void remove (final NetworkListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    removeListener (listeners.remove (listener));
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

    if (isRunning)
    {
      log.warn ("Cannot start the server because it's already running.");

      return;
    }

    start ();

    try
    {
      bind (tcpPort);

      isRunning = true;

      log.info ("Started the server on port [{}] (TCP).", tcpPort);
    }
    catch (final IOException e)
    {
      stop ();

      log.error ("Could not start the server on port [{}] (TCP). Reason: [{}].", tcpPort, Strings.toString (e));
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

  @Override
  public void stop ()
  {
    if (!isRunning) return;

    super.stop ();

    isRunning = false;

    log.info ("Stopped the server.");
  }

  private boolean addressMatches (final Connection connection, final Remote client)
  {
    return client.has (connection.getRemoteAddressTCP ()) || client.has (connection.getRemoteAddressUDP ());
  }

  private boolean idMatches (final int connectionId, final Remote client)
  {
    return client.has (connectionId);
  }

  private boolean isMatch (final Connection connection, final Remote client)
  {
    return idMatches (connection.getID (), client) && addressMatches (connection, client);
  }
}
