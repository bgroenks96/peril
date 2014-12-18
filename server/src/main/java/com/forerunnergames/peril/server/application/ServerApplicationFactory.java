package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.shared.application.EventBasedApplication;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.common.net.Server;
import com.forerunnergames.tools.common.net.ServerController;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerApplicationFactory
{
  private static Logger log = LoggerFactory.getLogger (ServerApplicationFactory.class);

  public static Application create (final String serverName, final int serverTcpPort, final int playerLimit)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");

    final IBusConfiguration eventBusConfiguration = new BusConfiguration()
            .addFeature (Feature.SyncPubSub.Default())
            .addFeature (Feature.AsynchronousHandlerInvocation.Default())
            .addFeature (Feature.AsynchronousMessageDispatch.Default());

    final MBassador <Event> eventBus = new MBassador <> (eventBusConfiguration);

    eventBus.addErrorHandler (new IPublicationErrorHandler()
    {
      @Override
      public void handleError (final PublicationError error)
      {
        log.error (error.toString(), error.getCause());
      }
    });

    final Server server = new KryonetServer();
    final ServerController serverController = new EventBasedServerController (server, serverTcpPort, KryonetRegistration.CLASSES, eventBus);
    final PlayerModel playerModel = new PlayerModel (playerLimit);
    final Controller multiplayerController = new MultiplayerController (playerModel, serverName, serverTcpPort, serverController, serverController);

    return new EventBasedApplication (serverController, multiplayerController);
  }
}