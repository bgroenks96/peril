package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;

import com.forerunnergames.peril.client.LocalServerCreator;
import com.forerunnergames.peril.client.controllers.EventBasedClientController;
import com.forerunnergames.peril.client.controllers.MultiplayerController;
import com.forerunnergames.peril.client.kryonet.KryonetClient;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.tools.common.Controller;
import com.forerunnergames.tools.common.net.Client;
import com.forerunnergames.tools.common.net.ClientController;
import com.forerunnergames.tools.common.net.ServerCreator;

public final class ClientApplicationFactory
{
  public static ApplicationListener create()
  {
    final Client client = new KryonetClient();
    final ClientController clientController = new EventBasedClientController (client, KryonetRegistration.CLASSES);
    final ServerCreator serverCreator = new LocalServerCreator();
    final Controller multiplayerController = new MultiplayerController (serverCreator, clientController, clientController);

    return new ClientApplication (clientController, multiplayerController);
  }
}