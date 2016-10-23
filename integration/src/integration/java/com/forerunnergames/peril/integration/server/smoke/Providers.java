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

import com.forerunnergames.peril.client.net.KryonetClient;
import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.integration.NetworkPortPool;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestServerApplication;
import com.forerunnergames.peril.integration.server.TestServerApplicationFactory;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.DefaultInternalAddressResolver;
import com.forerunnergames.tools.net.InternalAddressResolver;
import com.forerunnergames.tools.net.server.configuration.DefaultServerConfiguration;

import net.engio.mbassy.bus.MBassador;

import org.testng.annotations.DataProvider;

public class Providers
{
  private static final String FAKE_EXTERNAL_SERVER_ADDRESS = "0.0.0.0";
  private static final InternalAddressResolver INTERNAL_ADDRESS_RESOLVER = new DefaultInternalAddressResolver ();
  private static final NetworkPortPool PORT_POOL = NetworkPortPool.getInstance ();

  @DataProvider (name = "environmentProvider", parallel = true)
  public static Object[][] environmentProvider ()
  {
    // @formatter:off
    return new Object [][] {
      genParamsDedicated (),
      genParamsHostNPlay ()
    };
    // @formatter:on
  }

  private static Object[] genParamsDedicated ()
  {
    final MBassador <Event> eventBus = EventBusFactory.create ();
    final int port = PORT_POOL.getAvailablePort ();
    final TestServerApplication server = TestServerApplicationFactory
            .createTestServer (eventBus, GameServerType.DEDICATED, ClassicGameRules.DEFAULT_PLAYER_LIMIT,
                               FAKE_EXTERNAL_SERVER_ADDRESS, port);
    final TestClient client = new TestClient (new KryonetClient ());
    return new Object [] { eventBus, server, client,
                          new DefaultServerConfiguration (INTERNAL_ADDRESS_RESOLVER.resolveIp (), port) };
  }

  private static Object[] genParamsHostNPlay ()
  {
    final MBassador <Event> eventBus = EventBusFactory.create ();
    final int port = PORT_POOL.getAvailablePort ();
    final TestServerApplication server = TestServerApplicationFactory
            .createTestServer (eventBus, GameServerType.HOST_AND_PLAY, ClassicGameRules.DEFAULT_PLAYER_LIMIT,
                               FAKE_EXTERNAL_SERVER_ADDRESS, port);
    final TestClient client = new TestClient (new KryonetClient ());
    return new Object [] { eventBus, server, client, new DefaultServerConfiguration ("localhost", port) };
  }
}
