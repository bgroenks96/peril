package com.forerunnergames.peril.client.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRemote;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.Time;
import com.forerunnergames.tools.common.Utils;
import com.forerunnergames.tools.net.NetworkListener;
import com.forerunnergames.tools.net.client.Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KryonetClient extends com.esotericsoftware.kryonet.Client implements Client
{
  private static final Logger log = LoggerFactory.getLogger (KryonetClient.class);
  private final Map <NetworkListener, Listener> networkToKryonetListeners = new HashMap <> ();
  private final Kryo kryo;
  private boolean isRunning = false;

  public KryonetClient ()
  {
    super (NetworkSettings.CLIENT_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES,
           NetworkSettings.CLIENT_SERIALIZATION_READ_BUFFER_SIZE_BYTES);

    kryo = getKryo ();

    KryonetRegistration.initialize (kryo);
    KryonetRegistration.registerCustomSerializers (kryo);
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
        networkListener.connected (new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }

      @Override
      public void disconnected (final Connection connection)
      {
        networkListener.disconnected (new KryonetRemote (connection.getID (), connection.getRemoteAddressTCP ()));
      }

      @Override
      public void received (final Connection connection, final Object object)
      {
        if (object instanceof FrameworkMessage) return;

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

    log.debug ("Registered class [{}] with the server for network serialization.", type);
  }

  @Override
  public void shutDown ()
  {
    disconnect ();
    stop ();
  }

  @Override
  public Result <String> connect (final String address, final int tcpPort, final int timeoutMs, final int maxAttempts)
  {
    Arguments.checkIsNotNull (address, "address");
    Arguments.checkIsNotNegative (tcpPort, "tcpPort");
    Arguments.checkIsNotNegative (timeoutMs, "timeoutMs");
    Arguments.checkIsNotNegative (maxAttempts, "maxAttempts");

    if (isConnected ())
    {
      log.warn ("Cannot connect to the server because you are already connected.");

      return Result.failure ("You are already connected to the server.");
    }

    log.info ("Connecting to server at address [{}] & port [{}] (TCP)...", address, tcpPort);

    int connectionAttempts = 0;
    Result <String> result = Result.failure ("No connection attempt was made.");

    while (!isConnected () && connectionAttempts < maxAttempts)
    {
      Utils.sleep (Time.Seconds (1));

      ++connectionAttempts;

      log.info ("[{}] connection attempt...", Strings.toMixedOrdinal (connectionAttempts));

      result = connect (address, tcpPort, timeoutMs);
    }

    return result;
  }

  @Override
  public void disconnect ()
  {
    if (!isConnected ()) return;

    close ();
  }

  @Override
  public void send (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    if (!isRunning)
    {
      log.warn ("Prevented sending object [{}] to the server because the client hasn't been started.", object);

      return;
    }

    if (!isConnected ())
    {
      log.warn ("Prevented sending object [{}] to the server because the client is disconnected.", object);

      return;
    }

    sendTCP (object);

    log.debug ("Sent object [{}] to the server", object);
  }

  @Override
  public void start ()
  {
    super.start ();

    isRunning = true;

    log.info ("Started the client");
  }

  @Override
  public void stop ()
  {
    if (!isRunning) return;

    super.stop ();

    isRunning = false;

    log.info ("Stopped the client");
  }

  private Result <String> connect (final String address, final int tcpPort, final int timeoutMs)
  {
    log.info ("Attempting to connect to server with address [{}] on port [{}] (TCP).", address, tcpPort);

    try
    {
      connect (timeoutMs, address, tcpPort);

      // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <String> type.
      return isConnected () ? Result.<String> success () : Result.failure ("Unknown");
    }
    catch (final IOException e)
    {
      log.info ("Failed to connect to server with address [{}] on port [{}] (TCP).", address, tcpPort);
      log.debug ("Failure reason: [{}]", Strings.toString (e));

      return Result.failure ("Could not connect to server with address [" + address + "] on port [" + tcpPort
              + "] (TCP). Details:\n\n" + Strings.toString (e));
    }
  }
}
