package com.forerunnergames.peril.server.application;

import com.beust.jcommander.JCommander;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.StateMachineActionHandler;
import com.forerunnergames.peril.core.model.io.DefaultCountryIdResolver;
import com.forerunnergames.peril.core.model.io.PlayMapModelDataFactory;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.GameRulesFactory;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.peril.server.communicators.DefaultCoreCommunicator;
import com.forerunnergames.peril.server.communicators.PlayerCommunicator;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.peril.server.main.CommandLineArgs;
import com.forerunnergames.tools.common.Application;
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
  public static Application create (final String... args)
  {
    final CommandLineArgs jArgs = new CommandLineArgs ();
    final JCommander jCommander = new JCommander (jArgs);

    jCommander.parse (args);

    final MBassador <Event> eventBus = EventBusFactory.create ();

    final Server server = new KryonetServer ();

    final AsyncExecution mainThreadExecutor = new AsyncExecution ();

    final ServerController serverController = new EventBasedServerController (server, jArgs.serverTcpPort,
            KryonetRegistration.CLASSES, eventBus, mainThreadExecutor);

    final ImmutableSet <Country> countries = PlayMapModelDataFactory.createCountries ();
    final ImmutableSet <Continent> continents = PlayMapModelDataFactory
            .createContinents (new DefaultCountryIdResolver (countries));

    final GameRules gameRules = GameRulesFactory.create (jArgs.gameMode, jArgs.playerLimit, jArgs.winPercentage,
                                                         countries.size (), jArgs.initialCountryAssignment);

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

    final GameConfiguration gameConfig = new DefaultGameConfiguration (jArgs.gameMode, gameRules.getPlayerLimit (),
            gameRules.getWinPercentage (), gameRules.getInitialCountryAssignment ());

    final ExternalAddressResolver externalAddressResolver = new DefaultExternalAddressResolver (
            NetworkSettings.EXTERNAL_IP_RESOLVER_URL);

    final ServerConfiguration serverConfig = new DefaultServerConfiguration (externalAddressResolver.resolveIp (),
            jArgs.serverTcpPort);

    final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (jArgs.gameServerName,
            jArgs.gameServerType, gameConfig, serverConfig);

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
