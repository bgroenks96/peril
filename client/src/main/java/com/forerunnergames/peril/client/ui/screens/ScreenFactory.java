package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.CountryCounterFactory;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.MultiplayerGameModesMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.MultiplayerClassicGameModeMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame.DefaultCreateGameHandler;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.creategame.MultiplayerClassicGameModeCreateGameMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame.MultiplayerClassicGameModeJoinGameMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.shared.DefaultJoinGameHandler;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.shared.JoinGameHandler;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.peril.MultiplayerPerilGameModeMenuScreen;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ScreenFactory
{
  private final ScreenChanger screenChanger;
  private final ScreenSize screenSize;
  private final MouseInput mouseInput;
  private final MBassador <Event> eventBus;
  private final Batch batch;
  private final MenuScreenWidgetFactory menuScreenWidgetFactory;
  private final JoinGameHandler joinGameHandler;
  private final AssetManager assetManager;

  public ScreenFactory (final ScreenChanger screenChanger,
                        final ScreenSize screenSize,
                        final MouseInput mouseInput,
                        final Batch batch,
                        final AssetManager assetManager,
                        final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenChanger = screenChanger;
    this.screenSize = screenSize;
    this.mouseInput = mouseInput;
    this.batch = batch;
    this.assetManager = assetManager;
    this.eventBus = eventBus;
    menuScreenWidgetFactory = new MenuScreenWidgetFactory (assetManager);
    joinGameHandler = new DefaultJoinGameHandler (screenChanger, eventBus);
  }

  public Screen create (final ScreenId screenId)
  {
    Arguments.checkIsNotNull (screenId, "screenId");

    switch (screenId)
    {
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
                batch, new DefaultCreateGameHandler (joinGameHandler, eventBus),
                CountryCounterFactory.create (GameMode.CLASSIC));
      }
      case MULTIPLAYER_CLASSIC_GAME_MODE_JOIN_GAME_MENU:
      {
        return new MultiplayerClassicGameModeJoinGameMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize,
                batch, joinGameHandler);
      }
      case PLAY_CLASSIC:
      {
        return PlayScreenFactory.create (GameMode.CLASSIC, screenChanger, screenSize, mouseInput, batch, assetManager,
                                         eventBus);
      }
      case PLAY_PERIL:
      {
        return PlayScreenFactory.create (GameMode.PERIL, screenChanger, screenSize, mouseInput, batch, assetManager,
                                         eventBus);
      }
      default:
      {
        throw new IllegalStateException ("Unknown " + ScreenId.class.getSimpleName () + " [" + screenId + "].");
      }
    }
  }
}
