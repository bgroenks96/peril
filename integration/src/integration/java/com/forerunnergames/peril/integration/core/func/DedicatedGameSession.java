package com.forerunnergames.peril.integration.core.func;

import static com.forerunnergames.peril.integration.TestUtil.withDefaultHandler;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.settings.NetworkSettings;
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
import com.forerunnergames.tools.net.DefaultExternalAddressResolver;
import com.forerunnergames.tools.net.ExternalAddressResolver;

import java.util.concurrent.atomic.AtomicBoolean;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DedicatedGameSession implements TestSession
{
  private static final Logger log = LoggerFactory.getLogger (DedicatedGameSession.class);
  public static final String FAKE_EXTERNAL_SERVER_ADDRESS = "0.0.0.0";
  private final ExternalAddressResolver externalAddressResolver = new DefaultExternalAddressResolver (
          NetworkSettings.EXTERNAL_IP_RESOLVER_URL, NetworkSettings.EXTERNAL_IP_RESOLVER_BACKUP_URL);
  private final NetworkPortPool portPool = NetworkPortPool.getInstance ();
  private final AtomicBoolean isShutDown = new AtomicBoolean ();
  private final MBassador <Event> eventBus = EventBusFactory.create (withDefaultHandler ());
  private final GameRules gameRules;
  private final String serverAddress;
  private final int serverPort;
  private final TestClientPool clientPool = new TestClientPool ();
  private GameModel gameModel;
  private StateMachineEventHandler stateMachine;
  private TestServerApplication serverApplication;

  public DedicatedGameSession (final GameRules gameRules, final String serverAddress)
  {
    Arguments.checkIsNotNull (gameRules, "gameRules");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");

    this.gameRules = gameRules;
    this.serverAddress = serverAddress;
    serverPort = portPool.getAvailablePort ();
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

  public GameModel getGameModel ()
  {
    return gameModel;
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
    clientPool.connectNew (externalAddressResolver.resolveIp (), serverPort, gameRules.getPlayerLimit ());
  }
}
