package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.assets.AssetUpdater;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreen;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.CountryCounterFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultPlayMapActorFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActorFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.PerilModePlayScreen;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.peril.PerilModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.loading.LoadingScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.loading.MenuToPlayLoadingScreen;
import com.forerunnergames.peril.client.ui.screens.loading.PlayToMenuLoadingScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.MultiplayerGameModesMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.MultiplayerClassicGameModeMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame.MultiplayerClassicGameModeCreateGameMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame.DefaultJoinGameServerHandler;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame.MultiplayerClassicGameModeJoinGameMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.peril.MultiplayerPerilGameModeMenuScreen;
import com.forerunnergames.peril.client.ui.screens.splash.SplashScreen;
import com.forerunnergames.peril.client.ui.screens.splash.SplashScreenWidgetFactory;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ScreenFactory
{
  private static final Screen NULL_SCREEN = new NullScreen ();
  private final ScreenChanger screenChanger;
  private final ScreenSize screenSize;
  private final MouseInput mouseInput;
  private final Batch batch;
  private final AssetManager assetManager;
  private final AssetUpdater assetUpdater;
  private final MBassador <Event> eventBus;
  private final MenuScreenWidgetFactory menuScreenWidgetFactory;
  private final PlayMapActorFactory playMapActorFactory;

  public ScreenFactory (final ScreenChanger screenChanger,
                        final ScreenSize screenSize,
                        final MouseInput mouseInput,
                        final Batch batch,
                        final AssetManager assetManager,
                        final AssetUpdater assetUpdater,
                        final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (assetUpdater, "assetUpdater");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenChanger = screenChanger;
    this.screenSize = screenSize;
    this.mouseInput = mouseInput;
    this.batch = batch;
    this.assetManager = assetManager;
    this.assetUpdater = assetUpdater;
    this.eventBus = eventBus;

    menuScreenWidgetFactory = new MenuScreenWidgetFactory (assetManager);
    playMapActorFactory = new DefaultPlayMapActorFactory (assetManager, screenSize, mouseInput, eventBus);
  }

  public Screen create (final ScreenId screenId)
  {
    Arguments.checkIsNotNull (screenId, "screenId");

    switch (screenId)
    {
      case NONE:
      {
        return NULL_SCREEN;
      }
      case SPLASH:
      {
        return new SplashScreen (new SplashScreenWidgetFactory (assetManager), screenChanger,
                new LibGdxScreenSize (Gdx.graphics, ScreenSettings.SPLASH_SCREEN_REFERENCE_WIDTH,
                        ScreenSettings.SPLASH_SCREEN_REFERENCE_HEIGHT),
                mouseInput, batch, assetUpdater, assetManager, eventBus);
      }
      case MAIN_MENU:
      {
        return new MainMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize, batch);
      }
      case MULTIPLAYER_GAME_MODES_MENU:
      {
        return new MultiplayerGameModesMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize, batch);
      }
      case MULTIPLAYER_CLASSIC_GAME_MODE_MENU:
      {
        return new MultiplayerClassicGameModeMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize, batch);
      }
      case MULTIPLAYER_PERIL_GAME_MODE_MENU:
      {
        return new MultiplayerPerilGameModeMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize, batch);
      }
      case MULTIPLAYER_CLASSIC_GAME_MODE_CREATE_GAME_MENU:
      {
        return new MultiplayerClassicGameModeCreateGameMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize,
                batch, CountryCounterFactory.create (GameMode.CLASSIC), eventBus);
      }
      case MULTIPLAYER_CLASSIC_GAME_MODE_JOIN_GAME_MENU:
      {
        return new MultiplayerClassicGameModeJoinGameMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize,
                batch, new DefaultJoinGameServerHandler (eventBus), eventBus);
      }
      case MENU_TO_PLAY_LOADING:
      {
        return new MenuToPlayLoadingScreen (new LoadingScreenWidgetFactory (assetManager), playMapActorFactory,
                screenChanger, screenSize, mouseInput, batch, assetManager, eventBus);
      }
      case PLAY_CLASSIC:
      {
        return new ClassicModePlayScreen (new ClassicModePlayScreenWidgetFactory (assetManager, playMapActorFactory),
                screenChanger, screenSize, mouseInput, batch, eventBus);
      }
      case PLAY_PERIL:
      {
        return new PerilModePlayScreen (new PerilModePlayScreenWidgetFactory (assetManager), screenChanger, screenSize,
                mouseInput, batch, eventBus);
      }
      case PLAY_TO_MENU_LOADING:
      {
        return new PlayToMenuLoadingScreen (new LoadingScreenWidgetFactory (assetManager), screenChanger, screenSize,
                mouseInput, batch, assetManager, eventBus);
      }
      default:
      {
        throw new IllegalStateException ("Unknown " + ScreenId.class.getSimpleName () + " [" + screenId + "].");
      }
    }
  }
}
