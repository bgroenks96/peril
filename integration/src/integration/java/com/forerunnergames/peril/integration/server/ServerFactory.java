package com.forerunnergames.peril.integration.server;

import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.map.DefaultMapMetadata;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.map.PlayMapModelFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.DefaultCountryIdResolver;
import com.forerunnergames.peril.core.model.map.io.PlayMapModelDataFactoryCreator;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.integration.core.CoreFactory;
import com.forerunnergames.peril.integration.core.CoreFactory.GameStateMachineConfig;
import com.forerunnergames.peril.server.application.ServerApplication;
import com.forerunnergames.peril.server.communicators.DefaultCoreCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.Server;
import com.forerunnergames.tools.net.server.ServerConfiguration;
import com.forerunnergames.tools.net.server.ServerController;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

public class ServerFactory
{
  private static final MapMetadata MAP_METADATA = new DefaultMapMetadata (GameSettings.DEFAULT_CLASSIC_MODE_MAP_NAME,
          MapType.STOCK, GameMode.CLASSIC);
  private static volatile int testGameServerId = 0;

  public static TestServerApplication createTestServer (final MBassador <Event> eventBus,
                                                        final GameServerType type,
                                                        final int playerLimit,
                                                        final String serverAddress,
                                                        final int serverPort)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverPort, "serverPort");

    // @formatter:off
    final GameMode gameMode = GameMode.CLASSIC;
    final int winPercentage = ClassicGameRules.DEFAULT_WIN_PERCENTAGE;
    final InitialCountryAssignment initialCountryAssignment = InitialCountryAssignment.RANDOM;
    final CountryFactory countries = PlayMapModelDataFactoryCreator.create (gameMode).createCountries (MAP_METADATA);
    final ContinentFactory continents = PlayMapModelDataFactoryCreator.create (gameMode).createContinents (MAP_METADATA, new DefaultCountryIdResolver (countries));
    final GameRules gameRules = GameRulesFactory.create (gameMode, playerLimit, winPercentage, countries.getCountryCount (), initialCountryAssignment);
    final PlayMapModelFactory playMapModelFactory = new DefaultPlayMapModelFactory (gameRules);
    final CountryMapGraphModel countryMapGraphModel = CountryMapGraphModel.disjointCountryGraphFrom (countries);
    final ContinentMapGraphModel continentMapGraphModel = ContinentMapGraphModel.disjointContinentGraphFrom (continents, countryMapGraphModel);
    final GameStateMachineConfig config = new GameStateMachineConfig ();
    final GameModel gameModel = GameModel.builder (gameRules).playMapModel (playMapModelFactory.create (countryMapGraphModel, continentMapGraphModel)).eventBus (eventBus).build ();
    config.setGameModel (gameModel);
    final StateMachineEventHandler stateMachine = CoreFactory.createGameStateMachine (config);
    return newTestServer (eventBus, type, gameMode, MAP_METADATA, gameRules, stateMachine, serverAddress, serverPort);
    // @formatter:on
  }

  public static TestServerApplication createTestServer (final MBassador <Event> eventBus,
                                                        final GameServerType type,
                                                        final GameRules gameRules,
                                                        final StateMachineEventHandler stateMachine,
                                                        final String serverAddress,
                                                        final int serverPort)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (type, "type");
    Arguments.checkIsNotNull (gameRules, "gameRules");
    Arguments.checkIsNotNull (stateMachine, "stateMachine");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverPort, "serverPort");

    return newTestServer (eventBus, type, GameMode.CLASSIC, MAP_METADATA, gameRules, stateMachine, serverAddress,
                          serverPort);
  }

  private static TestServerApplication newTestServer (final MBassador <Event> eventBus,
                                                      final GameServerType type,
                                                      final GameMode gameMode,
                                                      final MapMetadata mapMetadata,
                                                      final GameRules gameRules,
                                                      final StateMachineEventHandler stateMachine,
                                                      final String serverAddress,
                                                      final int serverPort)
  {
    final String gameServerName = "TestGameServer-" + testGameServerId++;
    final int gameServerPort = serverPort;

    final Server server = new KryonetServer ();

    final AsyncExecution mainThreadExecutor = new AsyncExecution ();

    final ServerController serverController = new EventBasedServerController (server, gameServerPort,
            KryonetRegistration.CLASSES, eventBus, mainThreadExecutor);

    final GameConfiguration gameConfig = new DefaultGameConfiguration (gameMode, gameRules.getPlayerLimit (),
            gameRules.getWinPercentage (), gameRules.getInitialCountryAssignment (), mapMetadata);

    final ServerConfiguration serverConfig = new DefaultServerConfiguration (serverAddress, serverPort);

    final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (gameServerName, type,
            gameConfig, serverConfig);

    final MultiplayerController multiplayerController = new MultiplayerController (gameServerConfig, serverController,
            new PlayerCommunicator (serverController), new DefaultCoreCommunicator (eventBus), eventBus);

    final ServerApplication serverApplication = new ServerApplication (stateMachine, eventBus, mainThreadExecutor,
            serverController, multiplayerController);
    return new TestServerApplication (serverApplication, multiplayerController);
  }
}
