package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;

import com.forerunnergames.peril.client.LocalServerCreator;
import com.forerunnergames.peril.client.controllers.EventBasedClientController;
import com.forerunnergames.peril.client.controllers.MultiplayerController;
import com.forerunnergames.peril.client.kryonet.KryonetClient;
import com.forerunnergames.peril.core.shared.application.EventBasedApplication;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Controller;
import com.forerunnergames.tools.common.net.Client;
import com.forerunnergames.tools.common.net.ClientController;
import com.forerunnergames.tools.common.net.ServerCreator;

/**
 * Creates the {@link com.badlogic.gdx.ApplicationListener} instance to be passed to all of the executable
 * sub-projects (android, desktop, html, & ios).
 */
public final class LibGdxGameFactory
{
  public static ApplicationListener create()
  {
    final ServerCreator serverCreator = new LocalServerCreator();
    final Client client = new KryonetClient();
    final ClientController clientController = new EventBasedClientController (client, KryonetRegistration.CLASSES);
    final Controller multiplayerController = new MultiplayerController (serverCreator, clientController, clientController);
    final Application application = new EventBasedApplication (clientController, multiplayerController);

    return new LibGdxGameWrapper (application);
  }

  private LibGdxGameFactory()
  {
    Classes.instantiationNotAllowed();
  }
}