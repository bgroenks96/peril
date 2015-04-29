package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;

import com.forerunnergames.peril.client.LocalGameServerCreator;
import com.forerunnergames.peril.client.controllers.EventBasedClientController;
import com.forerunnergames.peril.client.controllers.MultiplayerController;
import com.forerunnergames.peril.client.kryonet.KryonetClient;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.music.MusicController;
import com.forerunnergames.peril.client.ui.music.MusicFactory;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.core.shared.application.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.GameServerCreator;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.Client;
import com.forerunnergames.tools.net.ClientController;

import net.engio.mbassy.bus.MBassador;

/**
 * Creates the {@link com.badlogic.gdx.ApplicationListener} instance to be passed to all of the executable sub-projects
 * (android, desktop, & ios).
 */
public final class LibGdxGameFactory
{
  public static ApplicationListener create ()
  {
    final MBassador <Event> eventBus = EventBusFactory.create ();

    // @formatter:off
    final Client client = new KryonetClient ();
    final ClientController clientController = new EventBasedClientController (client, KryonetRegistration.CLASSES, eventBus);
    final GameServerCreator gameServerCreator = new LocalGameServerCreator ();
    final Controller multiplayerController = new MultiplayerController (gameServerCreator, clientController, clientController, eventBus);
    final MusicController musicController = new MusicController (new MusicFactory (), new MusicSettings ());
    final Application application = new ClientApplication (clientController, multiplayerController, musicController);
    final Game libGdxGame = new LibGdxGameWrapper (application);
    final Controller screenController = new ScreenController (libGdxGame, musicController, eventBus);
    // @formatter:on

    // We must use setter injection here because constructor injection won't work due to circular dependencies:
    // ScreenController depends on Game, Game depends on Application, & Application depends on ScreenController.
    // This is a design compromise that greatly simplifies both application & screen management code.
    application.add (screenController);

    return libGdxGame;
  }

  private LibGdxGameFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
