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

package com.forerunnergames.peril.integration.server;

import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.playmap.DefaultPlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.battle.DefaultBattleModel;
import com.forerunnergames.peril.core.model.playmap.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.peril.core.model.playmap.PlayMapModelFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.DefaultCountryIdResolver;
import com.forerunnergames.peril.core.model.playmap.io.PlayMapModelDataFactoryCreator;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.integration.core.CoreFactory;
import com.forerunnergames.peril.integration.core.CoreFactory.GameStateMachineConfig;
import com.forerunnergames.peril.server.application.ServerApplication;
import com.forerunnergames.peril.server.communicators.DefaultCoreCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultPlayerCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultSpectatorCommunicator;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.ServerController;
import com.forerunnergames.tools.net.server.configuration.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

public class TestServerApplicationFactory
{
  private static final PlayMapMetadata PLAY_MAP_METADATA = new DefaultPlayMapMetadata (
          GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_NAME, PlayMapType.STOCK, GameMode.CLASSIC);
  private static final int SPECTATOR_LIMIT = 0;
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
    final CountryFactory countries = PlayMapModelDataFactoryCreator.create (gameMode).createCountries (PLAY_MAP_METADATA);
    final ContinentFactory continents = PlayMapModelDataFactoryCreator.create (gameMode).createContinents (PLAY_MAP_METADATA, new DefaultCountryIdResolver (countries));
    final GameRules gameRules = GameRulesFactory.create (gameMode, playerLimit, winPercentage, countries.getCountryCount (), initialCountryAssignment);
    final PlayMapModelFactory playMapModelFactory = new DefaultPlayMapModelFactory (gameRules);
    final CountryGraphModel countryGraphModel = CountryGraphModel.disjointCountryGraphFrom (countries);
    final ContinentGraphModel continentGraphModel = ContinentGraphModel.disjointContinentGraphFrom (continents, countryGraphModel);
    final GameStateMachineConfig config = new GameStateMachineConfig ();
    final PlayMapModel playMapModel = playMapModelFactory.create (countryGraphModel, continentGraphModel);
    final BattleModel battleModel = new DefaultBattleModel(playMapModel);
    final GameModel gameModel = GameModel.builder (gameRules).playMapModel (playMapModel).battleModel (battleModel).eventBus (eventBus).build ();
    config.setGameModel (gameModel);
    final StateMachineEventHandler stateMachine = CoreFactory.createGameStateMachine (config);
    return newTestServer (eventBus, type, gameMode, PLAY_MAP_METADATA, gameRules, stateMachine, serverAddress, serverPort);
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

    return newTestServer (eventBus, type, GameMode.CLASSIC, PLAY_MAP_METADATA, gameRules, stateMachine, serverAddress,
                          serverPort);
  }

  private static TestServerApplication newTestServer (final MBassador <Event> eventBus,
                                                      final GameServerType type,
                                                      final GameMode gameMode,
                                                      final PlayMapMetadata playMapMetadata,
                                                      final GameRules gameRules,
                                                      final StateMachineEventHandler stateMachine,
                                                      final String serverAddress,
                                                      final int serverPort)
  {
    final String gameServerName = "TestGameServer-" + testGameServerId++;
    final int gameServerPort = serverPort;

    final KryonetServer server = new KryonetServer ();

    final AsyncExecution mainThreadExecutor = new AsyncExecution ();

    final ServerController serverController = new EventBasedServerController (server, gameServerPort,
            KryonetRegistration.CLASSES, eventBus, mainThreadExecutor);

    final GameConfiguration gameConfig = new DefaultGameConfiguration (gameMode, gameRules.getPlayerLimit (),
            SPECTATOR_LIMIT, gameRules.getWinPercentage (), gameRules.getInitialCountryAssignment (), playMapMetadata,
            gameRules);

    final ServerConfiguration serverConfig = new DefaultServerConfiguration (serverAddress, serverPort);

    final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (gameServerName, type,
            gameConfig, serverConfig);

    final MultiplayerController multiplayerController = new MultiplayerController (gameServerConfig, serverController,
            new DefaultPlayerCommunicator (serverController), new DefaultSpectatorCommunicator (serverController),
            new DefaultCoreCommunicator (eventBus), eventBus);

    final ServerApplication serverApplication = new ServerApplication (stateMachine, eventBus, mainThreadExecutor,
            serverController, multiplayerController);

    return new TestServerApplication (serverApplication, serverController, server, multiplayerController);
  }
}
