package com.forerunnergames.peril.integration.server;

import static org.testng.Assert.fail;

import com.forerunnergames.peril.client.kryonet.KryonetClient;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Optional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClientPool
{
  private final List <TestClient> clients = new CopyOnWriteArrayList <> ();
  private final AtomicInteger clientCount = new AtomicInteger ();
  private final ForkJoinPool clientThreadPool = new ForkJoinPool (Runtime.getRuntime ().availableProcessors (),
          ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

  public int connectNew (final String serverAddress, final int serverPort)
  {
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverPort, "serverPort");

    final int newClientIndex = clientCount.getAndIncrement ();
    clientThreadPool.execute (new Runnable ()
    {
      @Override
      public void run ()
      {
        final TestClient newClient = new TestClient (new KryonetClient ());
        newClient.initialize ();
        newClient.connect (serverAddress, serverPort);
        clients.add (newClientIndex, newClient);
      }
    });
    return newClientIndex;
  }

  public void connectNew (final String serverAddress, final int serverPort, final int count)
  {
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverPort, "serverPort");
    Arguments.checkIsNotNegative (count, "count");

    for (int i = 0; i < count; i++)
    {
      connectNew (serverAddress, serverPort);
    }
  }

  public void waitForAllClients ()
  {
    while (!clientThreadPool.isQuiescent ())
    {
      Thread.yield ();
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
      clientThreadPool.execute (new Runnable ()
      {
        @Override
        public void run ()
        {
          final Optional <T> event = client.waitForEventCommunication (eventType, false);
          callback.onEventReceived (event, client);
        }
      });
    }
    waitForAllClients ();
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

  public void send (final int clientIndex, final Event event)
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

  public void dispose (final int clientIndex)
  {
    Arguments.checkIsNotNegative (clientIndex, "clientIndex");

    clients.get (clientIndex).dispose ();
    clients.remove (clientIndex);
    clientCount.getAndDecrement ();
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
