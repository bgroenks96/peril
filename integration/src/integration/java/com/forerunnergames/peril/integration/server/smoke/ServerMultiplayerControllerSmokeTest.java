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

package com.forerunnergames.peril.integration.server.smoke;

import static org.testng.Assert.assertTrue;

import com.esotericsoftware.kryo.Kryo;

import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.integration.NetworkPortPool;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestServerApplication;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import java.util.Collection;
import java.util.HashSet;

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
  private final Kryo kryo;

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
    kryo = server.getKryo ();

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
    client.sendEvent (new HumanJoinGameServerRequestEvent ());
    client.waitForEventCommunication (JoinGameServerSuccessEvent.class, true);
  }

  @Test (dependsOnMethods = "testJoinServer")
  public void testJoinGame ()
  {
    client.sendEvent (new HumanPlayerJoinGameRequestEvent ("TestPlayer1"));
    client.waitForEventCommunication (PlayerJoinGameSuccessEvent.class, true);
  }

  @Test (dependsOnMethods = "testJoinGame")
  public void testSendingNetworkSerializableClasses ()
  {
    final Collection <Class <?>> failedClasses = new HashSet <> ();

    for (final Class <?> registeredClass : KryonetRegistration.CLASSES)
    {
      if (!registeredClass.isAssignableFrom (Event.class)) continue;

      try
      {
        final Object object = kryo.newInstance (registeredClass);

        if (object == null)
        {
          failedClasses.add (registeredClass);
          continue;
        }

        server.sendEventToAllClients ((Event) object);
      }
      catch (final InstantiationError e)
      {
        failedClasses.add (registeredClass);
      }
    }

    if (!failedClasses.isEmpty ()) log.warn ("\nFailed to instantiate the following classes: \n\n{}", failedClasses);
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
