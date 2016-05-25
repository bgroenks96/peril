/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.client.events.CreateGameEvent;
import com.forerunnergames.peril.client.events.JoinGameEvent;
import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMapFactory;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame.CreateGameServerHandler;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame.CreateGameServerListener;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame.DefaultCreateGameServerHandler;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame.DefaultJoinGameServerHandler;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame.JoinGameServerHandler;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.PublicationEvent;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MenuToPlayLoadingScreen extends InputAdapter implements Screen
{
  private static final Logger log = LoggerFactory.getLogger (MenuToPlayLoadingScreen.class);
  private static final String LOADING_LABEL_TEXT = "LOADING";
  private static final float ONE_HALF = 1.0f / 2.0f;
  private static final float ONE_THIRD = 1.0f / 3.0f;
  private static final float ONE_SIXTH = 1.0f / 6.0f;
  private static final float ONE_NINTH = 1.0f / 9.0f;
  private static final float PROGRESS_BAR_ANIMATION_DURATION_SECONDS = 1.0f;
  private static final float PROGRESS_BAR_STEP_SIZE = 0.1f;
  private final PlayMapFactory playMapFactory;
  private final ScreenChanger screenChanger;
  private final MouseInput mouseInput;
  private final AssetManager assetManager;
  private final MBassador <Event> eventBus;
  private final Cursor normalCursor;
  private final Stage stage;
  private final InputProcessor inputProcessor;
  private final JoinGameServerHandler joinGameServerHandler;
  private final CreateGameServerHandler createGameServerHandler;
  private final CreateGameServerListener createGameServerListener;
  private final ProgressBar progressBar;
  private final Dialog quitDialog;
  private final Dialog errorDialog;
  private final List <ServerEvent> incomingServerEvents = new ArrayList <> ();
  private final Set <PlayerPacket> playersInGame = new HashSet <> ();
  private boolean isLoading = false;
  @Nullable
  private GameServerConfiguration gameServerConfiguration = null;
  @Nullable
  private ClientConfiguration clientConfiguration = null;
  private float overallLoadingProgressPercent = 0.0f;
  private float currentLoadingProgressPercent = 0.0f;
  private float previousLoadingProgressPercent = 0.0f;
  private boolean createdGameFirst = false;
  private boolean isResettingLoadingProgress = false;
  @Nullable
  private Runnable resetLoadingProgressCompletionRunnable = null;

  public MenuToPlayLoadingScreen (final LoadingScreenWidgetFactory widgetFactory,
                                  final PlayMapFactory playMapFactory,
                                  final ScreenChanger screenChanger,
                                  final ScreenSize screenSize,
                                  final MouseInput mouseInput,
                                  final Batch batch,
                                  final AssetManager assetManager,
                                  final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (playMapFactory, "playMapFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMapFactory = playMapFactory;
    this.screenChanger = screenChanger;
    this.mouseInput = mouseInput;
    this.assetManager = assetManager;
    this.eventBus = eventBus;

    joinGameServerHandler = new DefaultJoinGameServerHandler (eventBus);
    createGameServerHandler = new DefaultCreateGameServerHandler (joinGameServerHandler, eventBus);

    normalCursor = widgetFactory.createNormalCursor ();
    progressBar = widgetFactory.createProgressBar (PROGRESS_BAR_STEP_SIZE);
    progressBar.setAnimateDuration (PROGRESS_BAR_ANIMATION_DURATION_SECONDS);

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (widgetFactory.createBackground ());

    // @formatter:off
    final Table foregroundTable = new Table ().top ();
    foregroundTable.add ().height (870);
    foregroundTable.row ();
    foregroundTable.add (widgetFactory.createLabel (LOADING_LABEL_TEXT, Align.center, StyleSettings.LOADING_SCREEN_LOADING_TEXT_LABEL_STYLE)).size (700, 62);
    foregroundTable.row ().bottom ();
    foregroundTable.add (progressBar).size (700, 20).padBottom (128);
    // @formatter:on

    rootStack.add (foregroundTable);

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport, batch);

    // @formatter:off
    quitDialog = widgetFactory.createQuitDialog ("Are you sure you want to quit the current game?", stage, new DialogListenerAdapter ()
    {
      @Override
      public void onSubmit ()
      {
        isLoading = false;
        eventBus.publishAsync (new QuitGameEvent ());
        unloadPlayMapAssets ();
        resetLoadingProgress (new Runnable ()
        {
          @Override
          public void run ()
          {
            screenChanger.toScreen (ScreenId.PLAY_TO_MENU_LOADING);
          }
        });
      }
    });
    // @formatter:on

    errorDialog = widgetFactory.createErrorDialog (stage, new DialogListenerAdapter ()
    {
      @Override
      public void onShow ()
      {
        isLoading = false;
        unloadPlayMapAssets ();
        eventBus.publishAsync (new QuitGameEvent ());
      }

      @Override
      public void onSubmit ()
      {
        resetLoadingProgress (new Runnable ()
        {
          @Override
          public void run ()
          {
            screenChanger.toScreen (ScreenId.PLAY_TO_MENU_LOADING);
          }
        });
      }
    });

    createGameServerListener = new CreateGameServerListener ()
    {
      @Override
      public void onCreateStart (final GameServerConfiguration configuration, final String playerName)
      {
        Arguments.checkIsNotNull (configuration, "configuration");
        Arguments.checkIsNotNull (playerName, "playerName");

        log.trace ("onCreateStart: {} [{}], Player Name [{}]", GameServerConfiguration.class.getSimpleName (),
                   configuration, playerName);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            resetLoadingProgress ();
          }
        });
      }

      @Override
      public void onCreateFinish (final GameServerConfiguration configuration)
      {
        Arguments.checkIsNotNull (configuration, "configuration");

        log.trace ("onCreateFinish: {} [{}]", GameServerConfiguration.class.getSimpleName (), configuration);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            increaseLoadingProgressBy (ONE_THIRD);
          }
        });
      }

      @Override
      public void onCreateFailure (final GameServerConfiguration configuration, final String reason)
      {
        Arguments.checkIsNotNull (configuration, "configuration");
        Arguments.checkIsNotNull (reason, "reason");

        log.trace ("onCreateFailure: {} [{}], Reason [{}]", GameServerConfiguration.class.getSimpleName (),
                   configuration, reason);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            handleError (Strings.format ("{}", reason));
          }
        });
      }

      @Override
      public void onJoinStart (final String playerName, final ServerConfiguration configuration)
      {
        Arguments.checkIsNotNull (configuration, "configuration");

        log.trace ("onJoinStart: Player Name [{}], {} [{}] ", playerName, ServerConfiguration.class.getSimpleName (),
                   configuration);

        if (createdGameFirst) return;

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            resetLoadingProgress ();
          }
        });
      }

      @Override
      public void onConnectToServerSuccess (final ServerConfiguration configuration)
      {
        Arguments.checkIsNotNull (configuration, "configuration");

        log.trace ("onConnectToServerSuccess: {} [{}]", ServerConfiguration.class.getSimpleName (), configuration);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            increaseLoadingProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
          }
        });
      }

      @Override
      public void onJoinGameServerSuccess (final GameServerConfiguration gameServerConfiguration,
                                           final ClientConfiguration clientConfiguration)
      {
        Arguments.checkIsNotNull (gameServerConfiguration, "gameServerConfiguration");
        Arguments.checkIsNotNull (clientConfiguration, "clientConfiguration");
        Arguments.checkIsNotNull (playersInGame, "playersInGame");
        Arguments.checkHasNoNullElements (playersInGame, "playersInGame");

        log.trace ("onJoinGameServerSuccess: {} [{}], {} [{}], Player In Game [{}]",
                   GameServerConfiguration.class.getSimpleName (), gameServerConfiguration,
                   ClientConfiguration.class.getSimpleName (), clientConfiguration, playersInGame);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            increaseLoadingProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
          }
        });
      }

      @Override
      public void onPlayerJoinGameSuccess (final PlayerPacket player, final ImmutableSet <PlayerPacket> playersInGame)
      {
        Arguments.checkIsNotNull (player, "player");
        Arguments.checkIsNotNull (playersInGame, "playersInGame");
        Arguments.checkHasNoNullElements (playersInGame, "playersInGame");

        log.trace ("onPlayerJoinGameSuccess: Player [{}]", player);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            increaseLoadingProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
          }
        });
      }

      @Override
      public void onConnectToServerFailure (final ServerConfiguration configuration, final String reason)
      {
        Arguments.checkIsNotNull (configuration, "configuration");
        Arguments.checkIsNotNull (reason, "reason");

        log.trace ("onConnectToServerFailure: {} [{}], Reason [{}]", ServerConfiguration.class.getSimpleName (),
                   configuration, reason);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            handleError (Strings.format ("{}", reason));
          }
        });
      }

      @Override
      public void onJoinGameServerFailure (final ClientConfiguration configuration, final String reason)
      {
        Arguments.checkIsNotNull (configuration, "configuration");
        Arguments.checkIsNotNull (reason, "reason");

        log.trace ("onJoinGameServerFailure: {} [{}], Reason [{}]", ClientConfiguration.class.getSimpleName (),
                   configuration, reason);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            handleError (Strings.format ("{}", reason));
          }
        });
      }

      @Override
      public void onPlayerJoinGameFailure (final String playerName, final PlayerJoinGameDeniedEvent.Reason reason)
      {
        Arguments.checkIsNotNull (playerName, "playerName");
        Arguments.checkIsNotNull (reason, "reason");

        log.trace ("onPlayerJoinGameFailure: Player Name [{}], Reason [{}]", playerName, reason);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            handleError (Strings.format ("{}", asText (reason, playerName)));
          }
        });
      }

      @Override
      public void onJoinFinish (final GameServerConfiguration gameServerConfiguration,
                                final ClientConfiguration clientConfiguration,
                                final ImmutableSet <PlayerPacket> playersInGame)
      {
        Arguments.checkIsNotNull (gameServerConfiguration, "gameServerConfiguration");
        Arguments.checkIsNotNull (clientConfiguration, "clientConfiguration");
        Arguments.checkIsNotNull (playersInGame, "playersInGame");

        log.trace ("onJoinFinish: {} [{}], {} [{}], Players In Game [{}]",
                   GameServerConfiguration.class.getSimpleName (), gameServerConfiguration,
                   ClientConfiguration.class.getSimpleName (), clientConfiguration, playersInGame);

        MenuToPlayLoadingScreen.this.gameServerConfiguration = gameServerConfiguration;
        MenuToPlayLoadingScreen.this.clientConfiguration = clientConfiguration;
        MenuToPlayLoadingScreen.this.playersInGame.addAll (playersInGame);

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            startLoading (gameServerConfiguration.getMapMetadata ());
          }
        });
      }
    };

    stage.addActor (rootStack);

    stage.addListener (new ClickListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event,
                                final float x,
                                final float y,
                                final int pointer,
                                final int button)
      {
        stage.setKeyboardFocus (event.getTarget ());

        return false;
      }
    });

    stage.addCaptureListener (new InputListener ()
    {
      @Override
      public boolean keyDown (final InputEvent event, final int keycode)
      {
        switch (keycode)
        {
          case Input.Keys.ESCAPE:
          {
            quitDialog.show ();

            return false;
          }
          default:
          {
            return false;
          }
        }
      }
    });

    final InputProcessor preInputProcessor = new InputAdapter ()
    {
      @Override
      public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
      {
        stage.setKeyboardFocus (null);

        return false;
      }
    };

    inputProcessor = new InputMultiplexer (preInputProcessor, stage, this);
  }

  @Override
  public void show ()
  {
    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);

    stage.mouseMoved (mouseInput.x (), mouseInput.y ());

    quitDialog.refreshAssets ();
    errorDialog.refreshAssets ();
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0, 0, 0, 1);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    quitDialog.update (delta);
    errorDialog.update (delta);

    stage.act (delta);
    stage.draw ();

    if (resettingLoadingProgress () && resetLoadingProgressCompleted ()) endResetLoadingProgress ();
    if (!loading ()) return;

    updateLoadingProgress ();

    if (loadingProgressIncreased ()) increaseLoadingProgressBy (convert (getLoadingProgressIncrease ()));
    if (isFinishedLoading ()) goToPlayScreen ();
  }

  @Override
  public void resize (final int width, final int height)
  {
    stage.getViewport ().update (width, height, true);
    stage.getViewport ().setScreenPosition (InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_X,
                                            InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_Y);
  }

  @Override
  public void pause ()
  {
  }

  @Override
  public void resume ()
  {
  }

  @Override
  public void hide ()
  {
    eventBus.unsubscribe (this);

    stage.unfocusAll ();

    Gdx.input.setInputProcessor (null);

    hideCursor ();

    quitDialog.hide (null);
    errorDialog.hide (null);

    isLoading = false;
    gameServerConfiguration = null;
    clientConfiguration = null;
    playersInGame.clear ();
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);
    stage.dispose ();
  }

  @Handler
  void onEvent (final CreateGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        createdGameFirst = true;
        createGameServerHandler.create (event.getServerName (), event.getGameConfiguration (), event.getPlayerName (),
                                        createGameServerListener);
      }
    });
  }

  @Handler
  void onEvent (final JoinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        createdGameFirst = false;
        joinGameServerHandler.join (event.getPlayerName (), event.getServerAddress (), createGameServerListener);
      }
    });
  }

  @Handler
  void onEvent (final AssetLoadingErrorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {


      @Override
      public void run ()
      {
        // @formatter:off
        handleError (
                Strings.format ("A crash file has been created in \"{}\".\n\nThere was a problem loading a game " +
                        "resource.\n\nResource Name: {}\nResource Type: {}\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                        CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY, event.getFileName (),
                        event.getFileType ().getSimpleName (),
                        Throwables.getRootCause (event.getThrowable ()).getMessage (),
                        Strings.toString (event.getThrowable ())));
        // @formatter:on
      }
    });
  }

  // After having joined the game, server events start arriving intended for the play screen,
  // while the play map is still loading on this screen (play screen not active yet - cannot receive events).
  // Collect unhandled server events, which will be published after the play screen is active.
  @Handler
  void onEvent (final PublicationEvent unhandledEvent)
  {
    Arguments.checkIsNotNull (unhandledEvent, "unhandledEvent");

    final Object message = unhandledEvent.getMessage ();

    if (!(message instanceof ServerEvent))
    {
      log.warn ("Not collecting unhandled event for play screen: [{}]", message);
      return;
    }

    final ServerEvent event = (ServerEvent) message;

    log.debug ("Collecting unhandled event for play screen: [{}]", event);

    incomingServerEvents.add (event);
  }

  private static void hideCursor ()
  {
    Gdx.graphics.setSystemCursor (Cursor.SystemCursor.Arrow);
  }

  private static String asText (final PlayerJoinGameDeniedEvent.Reason reason, final String playerName)
  {
    switch (reason)
    {
      case GAME_IS_FULL:
      {
        return "This game is already full.";
      }
      case DUPLICATE_NAME:
      {
        return "Your name, " + playerName + ", is already taken by another player.";
      }
      case DUPLICATE_COLOR:
      {
        return "Your color is already taken by another player.";
      }
      case DUPLICATE_TURN_ORDER:
      {
        return "Your turn order is already taken by another player.";
      }
      case INVALID_NAME:
      {
        return "Your player name is invalid.";
      }
      default:
      {
        return "Unknown";
      }
    }
  }

  private boolean loading ()
  {
    return isLoading;
  }

  private void goToPlayScreen ()
  {
    assert gameServerConfiguration != null;
    assert clientConfiguration != null;

    final PlayMap playMap;

    try
    {
      playMap = playMapFactory.create (gameServerConfiguration.getMapMetadata ());
    }
    catch (final PlayMapLoadingException e)
    {
      // @formatter:off
      handleError (
              Strings.format ("A crash file has been created in \"{}\".\n\nThere was a problem loading resources " +
                      "for {} map \'{}\'.\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                      CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                      gameServerConfiguration != null ? gameServerConfiguration.getMapType ().name ().toLowerCase () : "",
                      gameServerConfiguration != null ? Strings.toProperCase (gameServerConfiguration.getMapName ()) : "",
                      Throwables.getRootCause (e).getMessage (), Strings.toString (e)));
      // @formatter:on
      return;
    }

    final Event playGameEvent = new PlayGameEvent (gameServerConfiguration, clientConfiguration,
            ImmutableSet.copyOf (playersInGame), playMap);

    unloadMenuAssets ();

    final GameMode mode = gameServerConfiguration.getGameMode ();

    isLoading = false;

    resetLoadingProgress (new Runnable ()
    {
      @Override
      public void run ()
      {
        switch (mode)
        {
          case CLASSIC:
          {
            screenChanger.toScreen (ScreenId.PLAY_CLASSIC);
            break;
          }
          case PERIL:
          {
            screenChanger.toScreen (ScreenId.PLAY_PERIL);
            break;
          }
          default:
          {
            throw new UnsupportedOperationException (Strings.format ("Unsupported {}: [{}].",
                                                                     GameMode.class.getSimpleName (),
                                                                     gameServerConfiguration.getGameMode ()));
          }
        }

        // The play screen is now active & can therefore receive events.

        eventBus.publish (playGameEvent);

        for (final ServerEvent event : incomingServerEvents)
        {
          eventBus.publish (event);
        }
      }
    });
  }

  private void startLoading (final MapMetadata mapMetadata)
  {
    isLoading = true;
    currentLoadingProgressPercent = 0.0f;

    loadPlayMapAssetsAsync (mapMetadata);
    loadPlayScreenAssetsAsync ();
  }

  private void handleError (final String message)
  {
    log.error (message);

    errorDialog.setMessage (new DefaultMessage (message));
    errorDialog.show ();
  }

  private void unloadMenuAssets ()
  {
    for (final AssetDescriptor <?> descriptor : AssetSettings.MENU_SCREEN_ASSET_DESCRIPTORS)
    {
      if (!assetManager.isLoaded (descriptor)) continue;
      assetManager.unload (descriptor);
    }
  }

  private void unloadPlayMapAssets ()
  {
    if (gameServerConfiguration == null)
    {
      log.warn ("Not unloading {} assets (null {}).", PlayMap.class.getSimpleName (),
                GameServerConfiguration.class.getSimpleName ());
      return;
    }

    playMapFactory.destroy (gameServerConfiguration.getMapMetadata ());
  }

  private void loadPlayScreenAssetsAsync ()
  {
    for (final AssetDescriptor <?> descriptor : AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ASSET_DESCRIPTORS)
    {
      assetManager.load (descriptor);
    }
  }

  private void loadPlayMapAssetsAsync (final MapMetadata mapMetadata)
  {
    try
    {
      playMapFactory.loadAssets (mapMetadata);
    }
    catch (final PlayMapLoadingException e)
    {
      // @formatter:off
      handleError (
              Strings.format ("A crash file has been created in \"{}\".\n\nThere was a problem loading resources " +
                      "for {} map \'{}\'.\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                      CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                      gameServerConfiguration != null ? gameServerConfiguration.getMapType ().name ().toLowerCase () : "",
                      gameServerConfiguration != null ? Strings.toProperCase (gameServerConfiguration.getMapName ()) : "",
                      Throwables.getRootCause (e).getMessage (), Strings.toString (e)));
      // @formatter:on
    }
  }

  private boolean isFinishedLoading ()
  {
    assert isLoading;
    assert gameServerConfiguration != null;

    return progressBar.getVisualPercent () >= 1.0f && assetManager.getProgressLoading () >= 1.0f
            && playMapFactory.isFinishedLoadingAssets (gameServerConfiguration.getMapMetadata ());
  }

  private void updateLoadingProgress ()
  {
    assert isLoading;
    assert gameServerConfiguration != null;

    previousLoadingProgressPercent = currentLoadingProgressPercent;

    currentLoadingProgressPercent = (playMapFactory.getAssetLoadingProgressPercent (gameServerConfiguration
            .getMapMetadata ()) + assetManager.getProgressLoading ()) / 2.0f;
  }

  private boolean loadingProgressIncreased ()
  {
    return currentLoadingProgressPercent > previousLoadingProgressPercent;
  }

  private float getLoadingProgressIncrease ()
  {
    return currentLoadingProgressPercent - previousLoadingProgressPercent;
  }

  private float convert (final float loadingProgressIncrease)
  {
    return loadingProgressIncrease * (createdGameFirst ? ONE_THIRD : ONE_HALF);
  }

  private void increaseLoadingProgressBy (final float percent)
  {
    overallLoadingProgressPercent += percent;

    progressBar.setValue (overallLoadingProgressPercent);

    log.debug ("Overall loading progress: {} (increased by {}).", overallLoadingProgressPercent, percent);
  }

  private boolean resettingLoadingProgress ()
  {
    return isResettingLoadingProgress;
  }

  private boolean resetLoadingProgressCompleted ()
  {
    return progressBar.getVisualValue () <= 0.0f;
  }

  private void endResetLoadingProgress ()
  {
    isResettingLoadingProgress = false;
    progressBar.setAnimateDuration (PROGRESS_BAR_ANIMATION_DURATION_SECONDS);

    if (resetLoadingProgressCompletionRunnable == null) return;

    resetLoadingProgressCompletionRunnable.run ();
    resetLoadingProgressCompletionRunnable = null;
  }

  private void resetLoadingProgress ()
  {
    resetLoadingProgress (null);
  }

  private void resetLoadingProgress (@Nullable final Runnable completionRunnable)
  {
    overallLoadingProgressPercent = 0.0f;
    progressBar.setAnimateDuration (0.0f);
    progressBar.setValue (0.0f);
    isResettingLoadingProgress = true;
    resetLoadingProgressCompletionRunnable = completionRunnable;
  }

  private void showCursor ()
  {
    Gdx.graphics.setCursor (normalCursor);
  }
}
