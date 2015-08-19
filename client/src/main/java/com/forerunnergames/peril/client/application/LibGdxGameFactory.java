package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

import com.forerunnergames.peril.client.assets.AssetController;
import com.forerunnergames.peril.client.assets.DefaultAssetLoader;
import com.forerunnergames.peril.client.assets.DefaultAssetUpdater;
import com.forerunnergames.peril.client.net.EventBasedClientController;
import com.forerunnergames.peril.client.net.MultiplayerController;
import com.forerunnergames.peril.client.io.CustomExternalFileHandleResolver;
import com.forerunnergames.peril.client.net.KryonetClient;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.music.MusicController;
import com.forerunnergames.peril.client.ui.music.MusicFactory;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.GameServerCreator;
import com.forerunnergames.peril.core.shared.net.LocalGameServerCreator;
import com.forerunnergames.peril.core.shared.net.kryonet.KryonetRegistration;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.client.Client;
import com.forerunnergames.tools.net.client.ClientController;

import de.matthiasmann.AsyncExecution;

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
    final AsyncExecution mainThreadExecutor = new AsyncExecution ();
    final ClientController clientController = new EventBasedClientController (client, KryonetRegistration.CLASSES, eventBus, mainThreadExecutor);
    final GameServerCreator gameServerCreator = new LocalGameServerCreator ();
    final Controller multiplayerController = new MultiplayerController (gameServerCreator, clientController, clientController, eventBus);
    final AssetManager assetManager = new AssetManager (new CustomExternalFileHandleResolver ());
    final Controller assetController = new AssetController (new DefaultAssetUpdater (), new DefaultAssetLoader (assetManager));
    final MusicController musicController = new MusicController (new MusicFactory (assetManager), new MusicSettings ());
    final Application application = new ClientApplication (mainThreadExecutor, assetController, clientController, multiplayerController, musicController);
    final Game libGdxGame = new LibGdxGameWrapper (application);
    final Controller screenController = new ScreenController (libGdxGame, musicController, assetManager, eventBus);
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
