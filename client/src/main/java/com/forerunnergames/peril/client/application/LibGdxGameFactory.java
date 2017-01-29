/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;

import com.forerunnergames.peril.client.assets.AssetController;
import com.forerunnergames.peril.client.assets.AssetLoadingErrorUnhandledEventHandler;
import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.assets.AssetManagerFactory;
import com.forerunnergames.peril.client.assets.AssetUpdater;
import com.forerunnergames.peril.client.assets.AssetUpdaterFactory;
import com.forerunnergames.peril.client.net.EventBasedClientController;
import com.forerunnergames.peril.client.net.MultiplayerController;
import com.forerunnergames.peril.client.ui.music.MusicController;
import com.forerunnergames.peril.client.ui.music.MusicFactory;
import com.forerunnergames.peril.client.ui.music.MusicVolumeController;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenFactoryCreator;
import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.eventbus.ThrowingPublicationErrorHandler;
import com.forerunnergames.peril.common.eventbus.UnhandledEventHandler;
import com.forerunnergames.peril.common.net.GameServerCreator;
import com.forerunnergames.peril.common.net.LocalGameServerCreator;
import com.forerunnergames.peril.common.net.kryonet.KryonetClient;
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
    // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic type casts.
    final MBassador <Event> eventBus = EventBusFactory
            .create (ImmutableSet.<IPublicationErrorHandler> of (new ThrowingPublicationErrorHandler ()),
                     ImmutableSet.<UnhandledEventHandler> of (new AssetLoadingErrorUnhandledEventHandler ()));
    final Client client = new KryonetClient ();
    final AsyncExecution mainThreadExecutor = new AsyncExecution ();
    final ClientController clientController = new EventBasedClientController (client, KryonetRegistration.CLASSES,
            eventBus, mainThreadExecutor);
    final GameServerCreator gameServerCreator = new LocalGameServerCreator ();
    final Controller multiplayerController = new MultiplayerController (gameServerCreator, clientController,
            clientController, eventBus);
    final AssetManager assetManager = AssetManagerFactory.create (eventBus);
    final Controller assetController = new AssetController (assetManager);
    final MusicVolumeController musicVolumeController = new MusicVolumeController ();
    final MusicController musicController = new MusicController (new MusicFactory (assetManager),
            musicVolumeController);
    final Application application = new ClientApplication (mainThreadExecutor, assetController, clientController,
            multiplayerController, musicController, musicVolumeController);
    final Game libGdxGame = new LibGdxGameWrapper (application);
    final AssetUpdater assetUpdater = AssetUpdaterFactory.create ();
    final ScreenFactoryCreator screenFactoryCreator = new ScreenFactoryCreator (assetManager, assetUpdater, eventBus);
    final Controller screenController = new ScreenController (libGdxGame, musicController, screenFactoryCreator);

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
