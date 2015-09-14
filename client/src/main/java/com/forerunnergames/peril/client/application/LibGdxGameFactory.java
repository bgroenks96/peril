package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

import com.forerunnergames.peril.client.assets.AssetController;
import com.forerunnergames.peril.client.assets.AssetLoadingErrorDeadEventHandler;
import com.forerunnergames.peril.client.assets.AssetManagerFactory;
import com.forerunnergames.peril.client.assets.DefaultAssetLoader;
import com.forerunnergames.peril.client.assets.DefaultAssetUpdater;
import com.forerunnergames.peril.client.net.EventBasedClientController;
import com.forerunnergames.peril.client.net.KryonetClient;
import com.forerunnergames.peril.client.net.MultiplayerController;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.music.MusicController;
import com.forerunnergames.peril.client.ui.music.MusicFactory;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.common.eventbus.DeadEventHandler;
import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.eventbus.ThrowingPublicationErrorHandler;
import com.forerunnergames.peril.common.net.GameServerCreator;
import com.forerunnergames.peril.common.net.LocalGameServerCreator;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.client.Client;
import com.forerunnergames.tools.net.client.ClientController;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;

/**
 * Creates the {@link ApplicationListener} instance to be passed to all of the executable sub-projects (android,
 * desktop, & ios).
 */
public final class LibGdxGameFactory
{
  public static ApplicationListener create ()
  {
    // @formatter:off

    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type cast.
    final MBassador <Event> eventBus =
            EventBusFactory.create (
                    ImmutableSet.<IPublicationErrorHandler> of (new ThrowingPublicationErrorHandler ()),
                    ImmutableSet.<DeadEventHandler> of (new AssetLoadingErrorDeadEventHandler ()));

    final Client client = new KryonetClient ();
    final AsyncExecution mainThreadExecutor = new AsyncExecution ();
    final ClientController clientController = new EventBasedClientController (client, KryonetRegistration.CLASSES, eventBus, mainThreadExecutor);
    final GameServerCreator gameServerCreator = new LocalGameServerCreator ();
    final Controller multiplayerController = new MultiplayerController (gameServerCreator, clientController, clientController, eventBus);
    final AssetManager assetManager = AssetManagerFactory.create (eventBus);
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
