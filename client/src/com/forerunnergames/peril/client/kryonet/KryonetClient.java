package com.forerunnergames.peril.client.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRemote;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.Time;
import com.forerunnergames.tools.common.Utils;
import com.forerunnergames.tools.common.net.Client;
import com.forerunnergames.tools.common.net.NetworkListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objenesis.strategy.StdInstantiatorStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KryonetClient extends com.esotericsoftware.kryonet.Client implements Client
{
  private static final Logger log = LoggerFactory.getLogger (KryonetClient.class);
  private final Map <NetworkListener, Listener> listeners = new HashMap <NetworkListener, Listener>();
  private final Kryo kryo;
  private boolean isRunning = false;

  public KryonetClient()
  {
    kryo = getKryo();
    kryo.setInstantiatorStrategy (new StdInstantiatorStrategy());
  }

  @Override
  public void add (final NetworkListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final Listener kryonetListener = new Listener()
    {
      @Override
      public void connected (final Connection connection)
      {
        listener.connected (new KryonetRemote (connection.getID(), connection.getRemoteAddressTCP()));
      }

      @Override
      public void disconnected (final Connection connection)
      {
        listener.disconnected (new KryonetRemote (connection.getID(), connection.getRemoteAddressTCP()));
      }

      @Override
      public void received (final Connection connection, final Object object)
      {
        if (! (object instanceof FrameworkMessage))
        {
          listener.received (object, new KryonetRemote (connection.getID(), connection.getRemoteAddressTCP()));
        }
      }
    };

    addListener (kryonetListener);

    listeners.put (listener, kryonetListener);
  }

  @Override
  public Result connect (final String address, final int tcpPort, final int timeoutMs, final int maxAttempts)
  {
    Arguments.checkIsNotNull (address, "address");
    Arguments.checkIsNotNegative (tcpPort, "tcpPort");
    Arguments.checkIsNotNegative (timeoutMs, "timeoutMs");
    Arguments.checkIsNotNegative (maxAttempts, "maxAttempts");

    if (isConnected())
    {
      log.warn ("Cannot connect to the server because you are already connected.");

      return Result.failure ("You are already connected to the server.");
    }

    log.info ("Connecting to server at address [{}] & port [{}] (TCP)...", address, tcpPort);

    int connectionAttempts = 0;
    Result result = Result.failure ("No connection attempt was made.");

    while (! isConnected() && connectionAttempts < maxAttempts)
    {
      Utils.sleep (Time.Seconds (1));

      ++connectionAttempts;

      log.info ("[{}] connection attempt...", Strings.toMixedOrdinal (connectionAttempts));

      result = connect (address, tcpPort, timeoutMs);
    }

    return result;
  }

  private Result connect (final String address, final int tcpPort, final int timeoutMs)
  {
    log.info ("Attempting to connect to server with address [{}] on port [{}] (TCP).", address, tcpPort);

    try
    {
      connect (timeoutMs, address, tcpPort);

      return isConnected() ? Result.success() : Result.failure ("Unknown");
    }
    catch (final IOException e)
    {
      log.info ("Failed to connect to server with address [{}] on port [{}] (TCP).", address, tcpPort);
      log.debug ("Failure reason: [{}]", Strings.toString (e));

      return Result.failure ("Could not connect to server with address [" + address + "] on port [" + tcpPort +
              "] (TCP). Details:\n\n" + Strings.toString (e));
    }
  }

  @Override
  public void disconnect()
  {
    if (! isConnected()) return;

    close();
  }

  @Override
  public void register (final Class <?> type)
  {
    Arguments.checkIsNotNull (type, "type");

    kryo.register (type);

    log.debug ("Registered class [{}] with the server for network serialization", type);
  }

  @Override
  public void remove (final NetworkListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    removeListener (listeners.remove (listener));
  }

  @Override
  public void send (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    if (! isRunning)
    {
      log.warn ("Prevented sending object [" + object + "] to the server because the client hasn't been started.");

      return;
    }

    if (! isConnected())
    {
      log.warn ("Prevented sending object [" + object + "] to the server because the client is disconnected.");

      return;
    }

    sendTCP (object);

    log.debug ("Sent object [{}] to the server", object);
  }

  @Override
  public void shutDown()
  {
    disconnect();
    stop();
  }

  @Override
  public void start()
  {
    super.start();

    isRunning = true;

    log.info ("Started the client");
  }

  @Override
  public void stop()
  {
    if (! isRunning) return;

    super.stop();

    isRunning = false;

    log.info ("Stopped the client");
  }
}