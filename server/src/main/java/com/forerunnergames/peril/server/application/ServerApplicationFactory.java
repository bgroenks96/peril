package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.GameRulesFactory;
import com.forerunnergames.peril.core.model.state.GameStateMachine;
import com.forerunnergames.peril.core.shared.application.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.controllers.PlayerCommunicator;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.peril.server.main.CommandLineArgs;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.Server;
import com.forerunnergames.tools.net.ServerController;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;

import com.beust.jcommander.JCommander;

public final class ServerApplicationFactory
{
  public static Application create (final String... args)
  {
    final CommandLineArgs jArgs = new CommandLineArgs ();
    final JCommander jCommander = new JCommander (jArgs);

    jCommander.parse (args);

    // @formatter:off
    final MBassador <Event> eventBus = EventBusFactory.create ();
    final Server server = new KryonetServer ();
    final ServerController serverController = new EventBasedServerController (server, jArgs.serverTcpPort, KryonetRegistration.CLASSES, eventBus);
    final GameRules gameRules = GameRulesFactory.create (jArgs.gameMode, jArgs.playerLimit, jArgs.winPercentage, jArgs.totalCountryCount, jArgs.initialCountryAssignment);
    final PlayerModel playerModel = new PlayerModel (gameRules);
    final PlayMapModel playMapModel = new PlayMapModel (ImmutableSet.<Country> of (), gameRules);
    final GameModel gameModel = new GameModel (playerModel, playMapModel, gameRules, eventBus);
    final GameStateMachine gameStateMachine = new GameStateMachine (gameModel);
    final GameConfiguration config = new DefaultGameConfiguration (jArgs.gameMode, gameRules.getPlayerLimit (), gameRules.getWinPercentage (), gameRules.getTotalCountryCount (), gameRules.getInitialCountryAssignment ());
    final Controller multiplayerController = new MultiplayerController (jArgs.serverName, jArgs.serverTcpPort, config, serverController, new PlayerCommunicator(serverController), eventBus);
    // @formatter:on

    return new ServerApplication (gameStateMachine, eventBus, serverController, multiplayerController);
  }
}
