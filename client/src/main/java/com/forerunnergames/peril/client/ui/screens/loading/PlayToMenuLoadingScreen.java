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

package com.forerunnergames.peril.client.ui.screens.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.UnloadPlayMapRequestEvent;
import com.forerunnergames.peril.client.events.UnloadPlayScreenAssetsRequestEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMapFactory;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayToMenuLoadingScreen extends AbstractLoadingScreen
{
  private static final Logger log = LoggerFactory.getLogger (PlayToMenuLoadingScreen.class);
  private static final ResetProgressListener EXIT_APPLICATION_LISTENER = new ExitApplicationListener ();
  private final ResetProgressListener goToPreviousScreenOrMainMenuListener = new GoToPreviousScreenOrMainMenuListener ();
  private final PlayMapFactory playMapFactory;

  public PlayToMenuLoadingScreen (final LoadingScreenWidgetFactory widgetFactory,
                                  final ScreenChanger screenChanger,
                                  final ScreenSize screenSize,
                                  final MouseInput mouseInput,
                                  final Batch batch,
                                  final AssetManager assetManager,
                                  final MBassador <Event> eventBus,
                                  final PlayMapFactory playMapFactory)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus, assetManager,
           LoadingScreenStyle.builder ().build ());

    Arguments.checkIsNotNull (playMapFactory, "playMapFactory");

    this.playMapFactory = playMapFactory;
  }

  @Override
  public void show ()
  {
    super.show ();
    statusWithProgressPercent ("Loading menu assets...");
    loadAssetsAsync (AssetSettings.MENU_SCREEN_ASSET_DESCRIPTORS);
  }

  @Override
  protected void onProgressFinished ()
  {
    resetProgress (goToPreviousScreenOrMainMenuListener);
  }

  @Override
  protected void onQuitDialogSubmit ()
  {
    publishAsync (new QuitGameEvent ());
    resetProgress (EXIT_APPLICATION_LISTENER);
  }

  @Override
  protected void onErrorDialogSubmit ()
  {
    resetProgress (goToPreviousScreenOrMainMenuListener);
  }

  @Override
  protected void onErrorDialogShow ()
  {
    publishAsync (new QuitGameEvent ());
  }

  @Handler
  void onEvent (final UnloadPlayScreenAssetsRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        unloadPlayScreenAssetsSync (event.getGameMode ());
        resumeLoadingMenuAssetsStatus ();
      }
    });
  }

  @Handler
  void onEvent (final UnloadPlayMapRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        if (event.isNullPlayMapMetadata ()) return;

        unloadPlayMapAssetsSync (event.getPlayMapMetadata ());
        resumeLoadingMenuAssetsStatus ();
      }
    });
  }

  private void unloadPlayScreenAssetsSync (final GameMode mode)
  {
    status ("Unloading play screen assets...");
    unloadAssetsSync (AssetSettings.fromGameMode (mode));
  }

  private void unloadPlayMapAssetsSync (final PlayMapMetadata playMapMetadata)
  {
    status ("Unloading map \"{}\"...", playMapMetadata.getName ());
    playMapFactory.destroy (playMapMetadata);
  }

  private void resumeLoadingMenuAssetsStatus ()
  {
    if (!isFinishedLoadingAssets (AssetSettings.MENU_SCREEN_ASSET_DESCRIPTORS))
    {
      statusWithProgressPercent ("Loading menu assets...");
    }
  }

  private static final class ExitApplicationListener implements ResetProgressListener
  {
    @Override
    public void onResetProgressComplete ()
    {
      Gdx.app.exit ();
    }
  }

  private final class GoToPreviousScreenOrMainMenuListener implements ResetProgressListener
  {
    @Override
    public void onResetProgressComplete ()
    {
      toPreviousScreenOrSkipping (ScreenId.MAIN_MENU, ScreenId.PLAY_CLASSIC, ScreenId.PLAY_PERIL,
                                  ScreenId.MENU_TO_PLAY_LOADING, ScreenId.PLAY_TO_MENU_LOADING, ScreenId.SPLASH);
    }
  }
}
