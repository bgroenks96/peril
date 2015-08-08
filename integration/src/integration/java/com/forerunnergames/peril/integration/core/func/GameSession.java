package com.forerunnergames.peril.integration.core.func;

import static com.forerunnergames.peril.integration.TestUtil.withDefaultHandler;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.GameModelBuilder;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
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

import java.util.concurrent.atomic.AtomicBoolean;

import net.engio.mbassy.bus.MBassador;

public class GameSession implements TestSession
{
  public static final int DEFAULT_SERVER_PORT = NetworkSettings.DEFAULT_TCP_PORT;
  private final AtomicBoolean isShutDown = new AtomicBoolean ();
  private final MBassador <Event> eventBus = EventBusFactory.create (withDefaultHandler ());
  private final GameServerType serverType;
  private final GameRules gameRules;
  private final int serverPort;
  private final TestClientPool clientPool = new TestClientPool ();
  private GameModel gameModel;
  private GameStateMachine stateMachine;
  private TestServerApplication serverApplication;

  public GameSession (final GameServerType serverType, final GameRules gameRules, final int serverPort)
  {
    Arguments.checkIsNotNull (serverType, "serverType");
    Arguments.checkIsNotNull (gameRules, "gameRules");
    Arguments.checkIsNotNegative (serverPort, "serverPort");

    this.serverType = serverType;
    this.gameRules = gameRules;
    this.serverPort = serverPort;
  }

  public GameSession ()
  {
    this (GameServerType.HOST_AND_PLAY, createDefaultTestGameRules (), DEFAULT_SERVER_PORT);
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
    serverApplication = ServerFactory.createTestServer (eventBus, serverType, gameRules, stateMachine, serverPort);
    serverApplication.start ();
  }

  private void initializeClients ()
  {
    clientPool.connectNew ("localhost", serverPort, gameRules.getPlayerLimit ());
  }

  private static GameRules createDefaultTestGameRules ()
  {
    return new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.MANUAL).build ();
  }
}
