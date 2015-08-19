package com.forerunnergames.peril.integration.server.smoke;

import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.client.net.KryonetClient;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.peril.integration.server.ServerFactory;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestServerApplication;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.DefaultExternalAddressResolver;
import com.forerunnergames.tools.net.ExternalAddressResolver;
import com.forerunnergames.tools.net.server.ServerConfiguration;
import com.forerunnergames.tools.net.server.UnknownServerConfiguration;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

public class ServerMultiplayerControllerSmokeTest
{
  private static final Logger log = LoggerFactory.getLogger (ServerMultiplayerControllerSmokeTest.class);
  // --- local test group names --- //
  private static final String DEDICATED = "serverType:dedicated";
  private static final String HOSTNPLAY = "serverType:hostnplay";
  // ------------------------------ //
  private final ExternalAddressResolver externalAddressResolver = new DefaultExternalAddressResolver (
          NetworkSettings.EXTERNAL_IP_RESOLVER_URL, NetworkSettings.EXTERNAL_IP_RESOLVER_BACKUP_URL);
  private final String fakeExternalServerAddress = "0.0.0.0";
  private final int serverPort = NetworkSettings.DEFAULT_TCP_PORT;
  private MBassador <Event> eventBus;
  private TestServerApplication server;
  private TestClient client;

  @BeforeGroups (value = { DEDICATED })
  public void setUpDedicated ()
  {
    log.trace ("Setting up test client/server for test group [{}]...", DEDICATED);
    eventBus = EventBusFactory.create ();
    server = ServerFactory.createTestServer (eventBus, GameServerType.DEDICATED, ClassicGameRules.DEFAULT_PLAYER_LIMIT,
                                             fakeExternalServerAddress, serverPort);
    client = new TestClient (new KryonetClient ());
    client.initialize ();
    server.start ();
  }

  @BeforeGroups (value = { HOSTNPLAY })
  public void setUpHostNPlay ()
  {
    log.trace ("Setting up test client/server for test group [{}]...", HOSTNPLAY);
    eventBus = EventBusFactory.create ();
    server = ServerFactory.createTestServer (eventBus, GameServerType.HOST_AND_PLAY,
                                             ClassicGameRules.DEFAULT_PLAYER_LIMIT, fakeExternalServerAddress,
                                             serverPort);
    client = new TestClient (new KryonetClient ());
    client.initialize ();
    server.start ();
  }

  // note that the 'dedicated' tests will only work when run on the CI server
  @Test (groups = { DEDICATED })
  public void testConnectToDedicatedServer ()
  {
    assertTrue (client.connect (externalAddressResolver.resolveIp (), serverPort).isSuccessful ());
  }

  @Test (groups = { DEDICATED })
  public void testJoinDedicatedServer ()
  {
    joinServer ();
  }

  @Test (groups = { HOSTNPLAY })
  public void testConnectToHostNPlayServer ()
  {
    assertTrue (client.connect ("localhost", serverPort).isSuccessful ());
  }

  @Test (groups = { HOSTNPLAY })
  public void testJoinHostNPlayServer ()
  {
    joinServer ();
  }

  @AfterGroups (value = { DEDICATED, HOSTNPLAY })
  public void tearDown ()
  {
    log.trace ("Shutting down client/server...");
    if (client != null) client.dispose ();
    if (server != null) server.shutDown ();
  }

  private void joinServer ()
  {
    final ServerConfiguration config = new UnknownServerConfiguration ();
    client.sendEvent (new JoinGameServerRequestEvent (config));
    client.waitForEventCommunication (JoinGameServerSuccessEvent.class, true);
  }
}
