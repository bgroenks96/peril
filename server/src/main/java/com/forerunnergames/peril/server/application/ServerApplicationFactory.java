package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.GameRulesFactory;
import com.forerunnergames.peril.core.model.state.GameStateMachine;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.peril.server.main.CommandLineArgs;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.Server;
import com.forerunnergames.tools.net.ServerController;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

// @formatter:off
public final class ServerApplicationFactory
{
  private static Logger log = LoggerFactory.getLogger (ServerApplicationFactory.class);

  public static Application create (final String... args)
  {
    final CommandLineArgs jArgs = new CommandLineArgs ();
    final JCommander jCommander = new JCommander (jArgs);

    jCommander.parse (args);

    final MBassador <Event> eventBus = new MBassador <> ();

    eventBus.addErrorHandler (new IPublicationErrorHandler ()
    {
      @Override
      public void handleError (final PublicationError error)
      {
        log.error (error.toString (), error.getCause ());
      }
    });

    final Server server = new KryonetServer ();
    final ServerController serverController = new EventBasedServerController (server, jArgs.serverTcpPort, KryonetRegistration.CLASSES, eventBus);
    final GameRules gameRules = GameRulesFactory.create (jArgs.gameMode, jArgs.playerLimit, jArgs.winPercentage, jArgs.totalCountryCount, jArgs.initialCountryAssignment);
    final PlayerModel playerModel = new PlayerModel (gameRules);
    final GameModel gameModel = new GameModel (playerModel, gameRules, eventBus);
    final GameStateMachine gameStateMachine = new GameStateMachine (gameModel);
    final Controller multiplayerController = new MultiplayerController (jArgs.serverName, jArgs.serverTcpPort, serverController, serverController, eventBus);

    return new ServerApplication (gameStateMachine, eventBus, serverController, multiplayerController);
  }
}
