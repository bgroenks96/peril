package com.forerunnergames.peril.integration.core.func;

import static com.forerunnergames.peril.integration.TestUtil.withDefaultHandler;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.GameModelBuilder;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.state.GameStateMachine;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.peril.integration.TestSessionProvider.TestSession;
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

public class DedicatedGameSession implements TestSession
{
  public static final String FAKE_EXTERNAL_SERVER_ADDRESS = "0.0.0.0";
  public static final int DEFAULT_SERVER_PORT = NetworkSettings.DEFAULT_TCP_PORT;
  private final ExternalAddressResolver externalAddressResolver = new DefaultExternalAddressResolver (
          NetworkSettings.EXTERNAL_IP_RESOLVER_URL);
  private final AtomicBoolean isShutDown = new AtomicBoolean ();
  private final MBassador <Event> eventBus = EventBusFactory.create (withDefaultHandler ());
  private final GameRules gameRules;
  private final String serverAddress;
  private final int serverPort;
  private final TestClientPool clientPool = new TestClientPool ();
  private GameModel gameModel;
  private GameStateMachine stateMachine;
  private TestServerApplication serverApplication;

  public DedicatedGameSession (final GameRules gameRules, final String serverAddress, final int serverPort)
  {
    Arguments.checkIsNotNull (gameRules, "gameRules");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverPort, "serverPort");

    this.gameRules = gameRules;
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
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
    clientPool.disposeAll ();
    serverApplication.shutDown ();
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

  public GameStateMachine getStateMachine ()
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
    gameModel = new GameModelBuilder (gameRules).eventBus (eventBus).build ();
    final GameStateMachineConfig config = new GameStateMachineConfig ();
    config.setGameModel (gameModel);
    stateMachine = CoreFactory.createGameStateMachine (config);
    serverApplication = ServerFactory.createTestServer (eventBus, GameServerType.DEDICATED, gameRules, stateMachine,
                                                        serverAddress, serverPort);
    serverApplication.start ();
  }

  private void initializeClients ()
  {
    clientPool.connectNew (externalAddressResolver.resolveIp (), serverPort, gameRules.getPlayerLimit ());
  }
}
