package com.forerunnergames.peril.integration.server;

import static org.testng.Assert.fail;

import com.forerunnergames.peril.client.net.KryonetClient;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClientPool
{
  private static final Logger log = LoggerFactory.getLogger (TestClientPool.class);
  private static final int MAX_THREADS = 2;
  private final List <TestClient> clients = Collections.synchronizedList (new ArrayList <TestClient> ());
  private final ExecutorService clientThreadPool = Executors.newFixedThreadPool (MAX_THREADS);
  private final AtomicInteger pendingOperationCount = new AtomicInteger ();

  public synchronized void connectNew (final String serverAddress, final int serverPort)
  {
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverPort, "serverPort");

    pendingOperationCount.incrementAndGet ();
    clientThreadPool.execute (new Runnable ()
    {
      @Override
      public void run ()
      {
        final TestClient newClient = new TestClient (new KryonetClient ());
        newClient.initialize ();
        newClient.connect (serverAddress, serverPort);
        log.debug ("Successfully connected client [{}]", newClient.getClientId ());
        clients.add (newClient);
        pendingOperationCount.decrementAndGet ();
      }
    });
  }

  public void connectNew (final String serverAddress, final int serverPort, final int count)
  {
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverPort, "serverPort");
    Arguments.checkIsNotNegative (count, "count");

    for (int i = 0; i < count; i++)
    {
      log.debug ("Attempting to connect client {} to {}:{}", i, serverAddress, serverPort);
      connectNew (serverAddress, serverPort);
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

  public <T> void waitForAllClientsToReceive (final Class <T> eventType)
  {
    Arguments.checkIsNotNull (eventType, "eventType");

    waitForAllClientsToReceive (eventType, new ClientEventCallback <T> ()
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

  public <T> void waitForAllClientsToReceive (final Class <T> eventType, final ClientEventCallback <T> callback)
  {
    Arguments.checkIsNotNull (eventType, "eventType");
    Arguments.checkIsNotNull (callback, "callback");

    for (final TestClient client : clients)
    {
      pendingOperationCount.incrementAndGet ();
      clientThreadPool.execute (new Runnable ()
      {
        @Override
        public void run ()
        {
          try
          {
            final Optional <T> event = client.waitForEventCommunication (eventType, false);
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

  public interface ClientEventCallback <T>
  {
    void onEventReceived (Optional <T> event, TestClient client);
  }
}
