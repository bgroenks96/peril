package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.io.MapMetadataFinder;
import com.forerunnergames.peril.common.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.net.settings.NetworkSettings;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.StateMachineActionHandler;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.io.CoreMapMetadataFinderFactory;
import com.forerunnergames.peril.core.model.map.io.DefaultCountryIdResolver;
import com.forerunnergames.peril.core.model.map.io.PlayMapModelDataFactoryCreator;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.server.communicators.DefaultCoreCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.peril.server.main.CommandLineArgs;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.DefaultExternalAddressResolver;
import com.forerunnergames.tools.net.ExternalAddressResolver;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.Server;
import com.forerunnergames.tools.net.server.ServerConfiguration;
import com.forerunnergames.tools.net.server.ServerController;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

public final class ServerApplicationFactory
{
  public static Application create (final CommandLineArgs args)
  {
    Arguments.checkIsNotNull (args, "args");

    final MBassador <Event> eventBus = EventBusFactory.create ();

    final Server server = new KryonetServer ();

    final AsyncExecution mainThreadExecutor = new AsyncExecution ();

    final ServerController serverController = new EventBasedServerController (server, args.serverTcpPort,
            KryonetRegistration.CLASSES, eventBus, mainThreadExecutor);

    final MapMetadataFinder mapMetadataFinder = CoreMapMetadataFinderFactory.create (args.gameMode);
    final MapMetadata mapMetadata = mapMetadataFinder.find (args.mapName);

    final ImmutableSet <Country> countries = PlayMapModelDataFactoryCreator.create (args.gameMode)
            .createCountries (mapMetadata);

    final ImmutableSet <Continent> continents = PlayMapModelDataFactoryCreator.create (args.gameMode)
            .createContinents (mapMetadata, new DefaultCountryIdResolver (countries));

    final GameRules gameRules = GameRulesFactory.create (args.gameMode, args.playerLimit, args.winPercentage,
                                                         countries.size (), args.initialCountryAssignment);

    final PlayMapModel playMapModel = new DefaultPlayMapModel (countries, continents, gameRules);
    final PlayerModel playerModel = new DefaultPlayerModel (gameRules);
    final PlayerTurnModel playerTurnModel = new DefaultPlayerTurnModel (playerModel.getPlayerLimit ());
    final GameModel.Builder gameHandlerBuilder = GameModel.builder (gameRules);
    gameHandlerBuilder.playMapModel (playMapModel);
    gameHandlerBuilder.playerModel (playerModel);
    gameHandlerBuilder.playerTurnModel (playerTurnModel);
    gameHandlerBuilder.eventBus (eventBus);
    final StateMachineActionHandler gameModel = new StateMachineActionHandler (gameHandlerBuilder.build ());
    final StateMachineEventHandler gameStateMachine = new StateMachineEventHandler (gameModel);

    final GameConfiguration gameConfig = new DefaultGameConfiguration (args.gameMode, gameRules.getPlayerLimit (),
            gameRules.getWinPercentage (), gameRules.getInitialCountryAssignment (), mapMetadata);

    final ExternalAddressResolver externalAddressResolver = new DefaultExternalAddressResolver (
            NetworkSettings.EXTERNAL_IP_RESOLVER_URL, NetworkSettings.EXTERNAL_IP_RESOLVER_BACKUP_URL);

    final ServerConfiguration serverConfig = new DefaultServerConfiguration (externalAddressResolver.resolveIp (),
            args.serverTcpPort);

    final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (args.gameServerName,
            args.gameServerType, gameConfig, serverConfig);

    final Controller multiplayerController = new MultiplayerController (gameServerConfig, serverController,
            new PlayerCommunicator (serverController), new DefaultCoreCommunicator (eventBus), eventBus);

    return new ServerApplication (gameStateMachine, eventBus, mainThreadExecutor, serverController,
            multiplayerController);
  }

  private ServerApplicationFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
