package com.forerunnergames.peril.integration.server.smoke;

import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.common.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.integration.NetworkPortPool;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestServerApplication;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

@Test (groups = { "server", "smoke" })
public class ServerMultiplayerControllerSmokeTest
{
  private static final Logger log = LoggerFactory.getLogger (ServerMultiplayerControllerSmokeTest.class);
  private final MBassador <Event> eventBus;
  private final TestServerApplication server;
  private final TestClient client;
  private final String serverAddr;
  private final int serverPort;

  @Factory (dataProvider = "environmentProvider", dataProviderClass = Providers.class)
  public ServerMultiplayerControllerSmokeTest (final MBassador <Event> eventBus,
                                               final TestServerApplication server,
                                               final TestClient client,
                                               final ServerConfiguration serverConfig)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (server, "server");
    Arguments.checkIsNotNull (client, "client");

    this.eventBus = eventBus;
    this.server = server;
    this.client = client;
    serverAddr = serverConfig.getServerAddress ();
    serverPort = serverConfig.getServerTcpPort ();

    client.initialize ();
    server.start ();
  }

  @Test
  public void testConnectToServer ()
  {
    log.debug ("Attempting to connect to server at: {}:{}", serverAddr, serverPort);
    assertTrue (client.connect (serverAddr, serverPort).isSuccessful ());
  }

  @Test (dependsOnMethods = "testConnectToServer")
  public void testJoinServer ()
  {
    client.sendEvent (new JoinGameServerRequestEvent ());
    client.waitForEventCommunication (JoinGameServerSuccessEvent.class, true);
  }

  @AfterClass
  public void tearDown ()
  {
    log.trace ("Shutting down client/server...");
    if (client != null) client.dispose ();
    if (server != null) server.shutDown ();
    NetworkPortPool.getInstance ().releasePort (serverPort);
  }
}
