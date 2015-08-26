package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.client.events.CreateGameEvent;
import com.forerunnergames.peril.client.events.JoinGameEvent;
import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActorFactory;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListenerAdapter;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoadingScreen extends InputAdapter implements Screen
{
  private static final Logger log = LoggerFactory.getLogger (LoadingScreen.class);
  private static final float ONE_HALF = 1.0f / 2.0f;
  private static final float ONE_THIRD = 1.0f / 3.0f;
  private static final float ONE_SIXTH = 1.0f / 6.0f;
  private static final float ONE_NINTH = 1.0f / 9.0f;
  private static final float PROGRESS_BAR_ANIMATION_DURATION_SECONDS = 1.0f;
  private static final float PROGRESS_BAR_STEP_SIZE = 0.1f;
  private final WidgetFactory widgetFactory;
  private final PlayMapActorFactory playMapActorFactory;
  private final ScreenChanger screenChanger;
  private final MouseInput mouseInput;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final InputProcessor inputProcessor;
  private final JoinGameServerHandler joinGameServerHandler;
  private final CreateGameServerHandler createGameServerHandler;
  private final CreateGameServerListener createGameServerListener;
  private final ProgressBar progressBar;
  private final Popup errorPopup;
  private boolean isLoadingMap = false;
  @Nullable
  private GameServerConfiguration gameServerConfiguration = null;
  @Nullable
  private ClientConfiguration clientConfiguration = null;
  @Nullable
  private ImmutableSet <PlayerPacket> playersInGame = null;
  private float overallLoadingProgressPercent = 0.0f;
  private float currentMapLoadingProgressPercent = 0.0f;
  private float previousMapLoadingProgressPercent = 0.0f;
  private boolean createdGameFirst = false;

  public LoadingScreen (final LoadingScreenWidgetFactory widgetFactory,
                        final PlayMapActorFactory playMapActorFactory,
                        final ScreenChanger screenChanger,
                        final ScreenSize screenSize,
                        final MouseInput mouseInput,
                        final Batch batch,
                        final AssetManager assetManager,
                        final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (playMapActorFactory, "playMapActorFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;
    this.playMapActorFactory = playMapActorFactory;
    this.screenChanger = screenChanger;
    this.mouseInput = mouseInput;
    this.eventBus = eventBus;
    joinGameServerHandler = new DefaultJoinGameServerHandler (eventBus);
    createGameServerHandler = new DefaultCreateGameServerHandler (joinGameServerHandler, eventBus);
    progressBar = widgetFactory.createProgressBar (PROGRESS_BAR_STEP_SIZE);
    progressBar.setAnimateDuration (PROGRESS_BAR_ANIMATION_DURATION_SECONDS);

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (widgetFactory.createBackground ());

    final Table foregroundTable = new Table ().top ();
    foregroundTable.add ().height (870);
    foregroundTable.row ();
    foregroundTable.add (widgetFactory.createLabel ("LOADING", Align.center, "loading-text")).size (700, 62);
    foregroundTable.row ().bottom ();
    foregroundTable.add (progressBar).size (700, 20).padBottom (128);

    rootStack.add (foregroundTable);

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport, batch);

    errorPopup = widgetFactory.createErrorPopup (stage, new PopupListenerAdapter ()
    {
      @Override
      public void onSubmit ()
      {
        screenChanger.toPreviousScreenOr (ScreenId.MAIN_MENU);
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

        resetLoadingProgress ();
      }

      @Override
      public void onCreateFinish (final GameServerConfiguration configuration)
      {
        Arguments.checkIsNotNull (configuration, "configuration");

        log.trace ("onCreateFinish: {} [{}]", GameServerConfiguration.class.getSimpleName (), configuration);

        increaseLoadingProgressBy (ONE_THIRD);
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
            errorPopup.setMessage (new DefaultMessage (Strings.format ("{}", reason)));
            errorPopup.show ();
            eventBus.publish (new QuitGameEvent ());
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

        resetLoadingProgress ();
      }

      @Override
      public void onConnectToServerSuccess (final ServerConfiguration configuration)
      {
        Arguments.checkIsNotNull (configuration, "configuration");

        log.trace ("onConnectToServerSuccess: {} [{}]", ServerConfiguration.class.getSimpleName (), configuration);

        increaseLoadingProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
      }

      @Override
      public void onJoinGameServerSuccess (final GameServerConfiguration gameServerConfiguration,
                                           final ClientConfiguration clientConfiguration,
                                           final ImmutableSet <PlayerPacket> playersInGame)
      {
        Arguments.checkIsNotNull (gameServerConfiguration, "gameServerConfiguration");
        Arguments.checkIsNotNull (clientConfiguration, "clientConfiguration");
        Arguments.checkIsNotNull (playersInGame, "playersInGame");
        Arguments.checkHasNoNullElements (playersInGame, "playersInGame");

        log.trace ("onJoinGameServerSuccess: {} [{}], {} [{}], Player In Game [{}]",
                   GameServerConfiguration.class.getSimpleName (), gameServerConfiguration,
                   ClientConfiguration.class.getSimpleName (), clientConfiguration, playersInGame);

        increaseLoadingProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
      }

      @Override
      public void onPlayerJoinGameSuccess (final PlayerPacket player)
      {
        Arguments.checkIsNotNull (player, "player");

        log.trace ("onPlayerJoinGameSuccess: Player [{}]", player);

        increaseLoadingProgressBy (createdGameFirst ? ONE_NINTH : ONE_SIXTH);
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
            errorPopup.setMessage (new DefaultMessage (Strings.format ("{}", reason)));
            errorPopup.show ();
            eventBus.publish (new QuitGameEvent ());
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
            errorPopup.setMessage (new DefaultMessage (Strings.format ("{}", reason)));
            errorPopup.show ();
            eventBus.publish (new QuitGameEvent ());
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
            errorPopup.setMessage (new DefaultMessage (Strings.format ("{}", asText (reason, playerName))));
            errorPopup.show ();
            eventBus.publish (new QuitGameEvent ());
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

        LoadingScreen.this.gameServerConfiguration = gameServerConfiguration;
        LoadingScreen.this.clientConfiguration = clientConfiguration;
        LoadingScreen.this.playersInGame = playersInGame;

        Gdx.app.postRunnable (new Runnable ()
        {
          @Override
          public void run ()
          {
            startLoadingMap (gameServerConfiguration.getMapMetadata ());
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
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0, 0, 0, 1);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    stage.act (delta);
    stage.draw ();

    if (!loadingMap ()) return;

    updateMapLoadingProgress ();

    if (mapLoadingProgressIncreased ()) increaseLoadingProgressBy (convert (getMapLoadingProgressIncrease ()));
    if (isFinishedLoadingMap ()) goToPlayScreen ();
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

    isLoadingMap = false;
    gameServerConfiguration = null;
    clientConfiguration = null;
    playersInGame = null;
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
        handleErrorDuringMapLoading (Strings
                .format ("There was a problem loading a resource for {} map \'{}\'.\n\nResource Name: {}\nResource Type: {}\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                         gameServerConfiguration != null ? gameServerConfiguration.getMapType ().name ().toLowerCase () : "",
                         gameServerConfiguration != null ? Strings.toProperCase (gameServerConfiguration.getMapName ()) : "",
                         event.getFileName (), event.getFileType ().getSimpleName (),
                         Throwables.getRootCause (event.getThrowable ()).getMessage (),
                         Strings.toString (event.getThrowable ())));
        // @formatter:on
      }
    });
  }

  private static void hideCursor ()
  {
    Gdx.input.setCursorImage (null, 0, 0);
  }

  private static String asText (final PlayerJoinGameDeniedEvent.Reason reason, final String playerName)
  {
    switch (reason)
    {
      case GAME_IS_FULL:
      {
        return "This game is already full.";
      }
      case DUPLICATE_SELF_IDENTITY:
      {
        return "You have already joined this game.";
      }
      case DUPLICATE_ID:
      {
        return "Your id is already taken by another player.";
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
      default:
      {
        return "Unknown";
      }
    }
  }

  private boolean loadingMap ()
  {
    return isLoadingMap;
  }

  private void goToPlayScreen ()
  {
    if (gameServerConfiguration == null) throw new IllegalStateException (Strings
            .format ("Cannot go to play screen because {} is null.", GameServerConfiguration.class.getSimpleName ()));

    if (clientConfiguration == null) throw new IllegalStateException (Strings
            .format ("Cannot go to play screen because {} is null.", ClientConfiguration.class.getSimpleName ()));

    if (playersInGame == null) throw new IllegalStateException (
            Strings.format ("Cannot go to play screen because playersInGame is null.",
                            ClientConfiguration.class.getSimpleName ()));

    final PlayMapActor playMapActor = playMapActorFactory.create (gameServerConfiguration.getMapMetadata ());
    final Event playGameEvent = new PlayGameEvent (gameServerConfiguration, clientConfiguration, playersInGame,
            playMapActor);

    resetLoadingProgress ();

    switch (gameServerConfiguration.getGameMode ())
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
        throw new UnsupportedOperationException (
                Strings.format ("Unsupported {}: [{}].", GameMode.class.getSimpleName (),
                                gameServerConfiguration.getGameMode ()));
      }
    }

    eventBus.publish (playGameEvent);
  }

  private void startLoadingMap (final MapMetadata mapMetadata)
  {
    try
    {
      isLoadingMap = true;
      playMapActorFactory.loadAssets (mapMetadata);
    }
    catch (final PlayMapLoadingException e)
    {
      // @formatter:off
      handleErrorDuringMapLoading (Strings
              .format ("There was a problem loading resources for {} map \'{}\'.\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                       gameServerConfiguration != null ? gameServerConfiguration.getMapType ().name ().toLowerCase () : "",
                       gameServerConfiguration != null ? Strings.toProperCase (gameServerConfiguration.getMapName ()) : "",
                       Throwables.getRootCause (e).getMessage (), Strings.toString (e)));
      // @formatter:on
    }
  }

  private void handleErrorDuringMapLoading (final String message)
  {
    errorPopup.setMessage (new DefaultMessage (message));
    errorPopup.show ();

    if (gameServerConfiguration != null) unloadMap (gameServerConfiguration.getMapMetadata ());

    eventBus.publish (new QuitGameEvent ());
  }

  private void unloadMap (final MapMetadata mapMetadata)
  {
    playMapActorFactory.destroy (mapMetadata);

    isLoadingMap = false;
  }

  private boolean isFinishedLoadingMap ()
  {
    if (!isLoadingMap) throw new IllegalStateException (
            "Cannot check whether map is finished loading because map assets are not being loaded.");

    if (gameServerConfiguration == null) throw new IllegalStateException (
            Strings.format ("Cannot check whether map is finished loading because {} is null.",
                            GameServerConfiguration.class.getSimpleName ()));

    return progressBar.getVisualPercent () >= 1.0f
            && playMapActorFactory.isFinishedLoadingAssets (gameServerConfiguration.getMapMetadata ());
  }

  private void updateMapLoadingProgress ()
  {
    if (!isLoadingMap) throw new IllegalStateException (
            "Cannot get map loading progress percent because map assets are not being loaded.");

    if (gameServerConfiguration == null) throw new IllegalStateException (
            Strings.format ("Cannot get map loading progress percent because {} is null.",
                            GameServerConfiguration.class.getSimpleName ()));

    previousMapLoadingProgressPercent = currentMapLoadingProgressPercent;

    currentMapLoadingProgressPercent = playMapActorFactory
            .getAssetLoadingProgressPercent (gameServerConfiguration.getMapMetadata ());
  }

  private boolean mapLoadingProgressIncreased ()
  {
    return currentMapLoadingProgressPercent > previousMapLoadingProgressPercent;
  }

  private float getMapLoadingProgressIncrease ()
  {
    return currentMapLoadingProgressPercent - previousMapLoadingProgressPercent;
  }

  private float convert (final float mapLoadingProgressIncrease)
  {
    return createdGameFirst ? mapLoadingProgressIncrease * ONE_THIRD : mapLoadingProgressIncrease * ONE_HALF;
  }

  private void increaseLoadingProgressBy (final float percent)
  {
    overallLoadingProgressPercent += percent;

    progressBar.setValue (overallLoadingProgressPercent);

    log.debug ("Overall loading progress: {} (increased by {}).", overallLoadingProgressPercent, percent);
  }

  private void resetLoadingProgress ()
  {
    overallLoadingProgressPercent = 0.0f;
    progressBar.setAnimateDuration (0.0f);
    progressBar.setValue (0.0f);
    progressBar.setAnimateDuration (PROGRESS_BAR_ANIMATION_DURATION_SECONDS);
  }

  private void showCursor ()
  {
    Gdx.input.setCursorImage (widgetFactory.createNormalCursor (),
                              Math.round (InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.x),
                              Math.round (InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.y));
  }
}
