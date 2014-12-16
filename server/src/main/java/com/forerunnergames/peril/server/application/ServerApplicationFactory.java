package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.shared.application.EventBasedApplication;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.common.net.Server;
import com.forerunnergames.tools.common.net.ServerController;

public final class ServerApplicationFactory
{
  public static Application create (final String serverName, final int serverTcpPort, final int playerLimit)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");

    final Server server = new KryonetServer();
    final ServerController serverController = new EventBasedServerController (server, serverTcpPort, KryonetRegistration.CLASSES);
    final PlayerModel playerModel = new PlayerModel (playerLimit);
    final Controller multiplayerController = new MultiplayerController (playerModel, serverName, serverTcpPort, serverController, serverController);

    return new EventBasedApplication (serverController, multiplayerController);
  }
}