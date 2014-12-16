package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;

import com.forerunnergames.peril.client.LocalServerCreator;
import com.forerunnergames.peril.client.controllers.EventBasedClientController;
import com.forerunnergames.peril.client.controllers.MultiplayerController;
import com.forerunnergames.peril.client.kryonet.KryonetClient;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.controllers.Controller;
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
    final Client client = new KryonetClient();
    final ClientController clientController = new EventBasedClientController (client, KryonetRegistration.CLASSES);
    final ServerCreator serverCreator = new LocalServerCreator();
    final Controller multiplayerController = new MultiplayerController (serverCreator, clientController, clientController);
    final Application application = new ClientApplication (clientController, multiplayerController);
    final Game libGdxGame = new LibGdxGameWrapper (application);
    final Controller screenController = new ScreenController (libGdxGame);

    // We must use setter injection here because constructor injection won't work due to circular dependencies:
    // ScreenController depends on Game, Game depends on Application, & Application depends on ScreenController.
    // This is a design compromise that greatly simplifies both application & screen management code.
    application.add (screenController);

    return libGdxGame;
  }

  private LibGdxGameFactory()
  {
    Classes.instantiationNotAllowed();
  }
}