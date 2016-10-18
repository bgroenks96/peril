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

package com.forerunnergames.peril.integration.core.func;

import static com.forerunnergames.peril.integration.TestUtil.withDefaultHandler;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.integration.NetworkPortPool;
import com.forerunnergames.peril.integration.TestSessions.TestSession;
import com.forerunnergames.peril.integration.core.CoreFactory;
import com.forerunnergames.peril.integration.core.CoreFactory.GameStateMachineConfig;
import com.forerunnergames.peril.integration.server.ServerFactory;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestServerApplication;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.DefaultInternalAddressResolver;
import com.forerunnergames.tools.net.InternalAddressResolver;

import java.util.concurrent.atomic.AtomicBoolean;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DedicatedGameSession implements TestSession
{
  public static final String FAKE_EXTERNAL_SERVER_ADDRESS = "0.0.0.0";
  private static final Logger log = LoggerFactory.getLogger (DedicatedGameSession.class);
  private final InternalAddressResolver internalAddressResolver = new DefaultInternalAddressResolver ();
  private final NetworkPortPool portPool = NetworkPortPool.getInstance ();
  private final AtomicBoolean isShutDown = new AtomicBoolean ();
  private final MBassador <Event> eventBus = EventBusFactory.create (withDefaultHandler ());
  private final GameRules gameRules;
  private final String serverAddress;
  private final String internalServerAddress;
  private final String sessionName;
  private final int serverPort;
  private final TestClientPool clientPool = new TestClientPool ();
  private GameModel gameModel;
  private StateMachineEventHandler stateMachine;
  private TestServerApplication serverApplication;

  public DedicatedGameSession (final String sessionName, final String serverAddress, final GameRules gameRules)
  {
    Arguments.checkIsNotNull (sessionName, "sessionName");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNull (gameRules, "gameRules");

    this.sessionName = sessionName;
    this.gameRules = gameRules;
    this.serverAddress = serverAddress;

    serverPort = portPool.getAvailablePort ();
    internalServerAddress = internalAddressResolver.resolveIp ();
  }

  @Override
  public void start ()
  {
    initializeServer ();
    initializeClients ();
  }

  @Override
  public void shutDown ()
  {
    log.info ("Shutting down server application [{}:{}]", serverAddress, serverPort);
    clientPool.disposeAll ();
    serverApplication.shutDown ();
    portPool.releasePort (serverPort);
    isShutDown.set (true);
  }

  @Override
  public boolean isShutDown ()
  {
    return isShutDown.get ();
  }

  @Override
  public String getName ()
  {
    return sessionName;
  }

  public GameModel getGameModel ()
  {
    return gameModel;
  }

  public GameRules getRules ()
  {
    return gameRules;
  }

  public StateMachineEventHandler getStateMachine ()
  {
    return stateMachine;
  }

  public int getServerPort ()
  {
    return serverPort;
  }

  public TestServerApplication getTestServerApplication ()
  {
    return serverApplication;
  }

  public TestClientPool getTestClientPool ()
  {
    return clientPool;
  }

  public int getTestClientCount ()
  {
    return clientPool.count ();
  }

  private void initializeServer ()
  {
    gameModel = GameModel.builder (gameRules).eventBus (eventBus).build ();
    final GameStateMachineConfig config = new GameStateMachineConfig ();
    config.setGameModel (gameModel);
    stateMachine = CoreFactory.createGameStateMachine (config);
    serverApplication = ServerFactory.createTestServer (eventBus, GameServerType.DEDICATED, gameRules, stateMachine,
                                                        serverAddress, serverPort);
    log.trace ("Starting server application [{}] on port {}", serverAddress, serverPort);
    serverApplication.start ();
  }

  private void initializeClients ()
  {
    log.trace ("Connecting {} clients to server [{}]", gameRules.getPlayerLimit (), serverPort);
    clientPool.connectNew (internalServerAddress, serverPort, gameRules.getPlayerLimit ());
  }

  @Override
  public String toString ()
  {
    return Strings
            .format ("{}: Name: {} | Server Address: {}:{} | CountrySelectionMode: {}", getClass ().getSimpleName (),
                     sessionName, internalServerAddress, serverPort, gameRules.getInitialCountryAssignment ());
  }
}
