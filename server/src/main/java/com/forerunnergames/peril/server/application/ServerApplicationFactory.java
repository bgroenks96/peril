package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.GameRulesFactory;
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
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
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
    new JCommander (jArgs, args);

    final IBusConfiguration eventBusConfiguration = new BusConfiguration ()
            .addFeature (Feature.SyncPubSub.Default ())
            .addFeature (Feature.AsynchronousHandlerInvocation.Default ())
            .addFeature (Feature.AsynchronousMessageDispatch.Default ());

    final MBassador <Event> eventBus = new MBassador <> (eventBusConfiguration);

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
    final Controller multiplayerController = new MultiplayerController (playerModel, jArgs.serverName, jArgs.serverTcpPort, serverController, serverController);

    return new ServerApplication (serverController, multiplayerController);
  }
}
