package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenFactory;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.MultiplayerGameModesMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.MultiplayerClassicGameModeMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.peril.MultiplayerPerilGameModeMenuScreen;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ScreenFactory
{
  private final Skin skin;
  private final ScreenChanger screenChanger;
  private final ScreenSize screenSize;
  private final MouseInput mouseInput;
  private final MBassador <Event> eventBus;
  private final MenuScreenWidgetFactory menuScreenWidgetFactory;

  public ScreenFactory (final Skin skin,
                        final ScreenChanger screenChanger,
                        final ScreenSize screenSize,
                        final MouseInput mouseInput,
                        final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.skin = skin;
    this.screenChanger = screenChanger;
    this.screenSize = screenSize;
    this.mouseInput = mouseInput;
    this.eventBus = eventBus;
    this.menuScreenWidgetFactory = new MenuScreenWidgetFactory (skin);
  }

  public Screen create (final ScreenId screenId)
  {
    Arguments.checkIsNotNull (screenId, "screenId");

    switch (screenId)
    {
      case MAIN_MENU:
      {
        return new MainMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize);
      }
      case MULTIPLAYER_GAME_MODES_MENU:
      {
        return new MultiplayerGameModesMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize);
      }
      case MULTIPLAYER_CLASSIC_GAME_MODE_MENU:
      {
        return new MultiplayerClassicGameModeMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize);
      }
      case MULTIPLAYER_PERIL_GAME_MODE_MENU:
      {
        return new MultiplayerPerilGameModeMenuScreen (menuScreenWidgetFactory, screenChanger, screenSize);
      }
      case PLAY_CLASSIC:
      {
        return PlayScreenFactory.create (GameMode.CLASSIC, skin, screenChanger, screenSize, mouseInput, eventBus);
      }
      case PLAY_PERIL:
      {
        return PlayScreenFactory.create (GameMode.PERIL, skin, screenChanger, screenSize, mouseInput, eventBus);
      }
      default:
      {
        throw new IllegalStateException ("Unknown " + ScreenId.class.getSimpleName () + " [" + screenId + "].");
      }
    }
  }
}
