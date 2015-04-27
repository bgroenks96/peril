package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenFactory;
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
  private final MBassador <Event> eventBus;

  public ScreenFactory (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  public Screen create (final ScreenId screenId,
                        final ScreenChanger screenChanger,
                        final ScreenSize screenSize,
                        final MouseInput mouseInput,
                        final Skin skin)
  {
    Arguments.checkIsNotNull (screenId, "screenId");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (skin, "skin");

    switch (screenId)
    {
      case MAIN_MENU:
      {
        return new MainMenuScreen (screenChanger, screenSize, skin);
      }
      case MULTIPLAYER_GAME_MODES_MENU:
      {
        return new MultiplayerGameModesMenuScreen (screenChanger, screenSize, skin);
      }
      case MULTIPLAYER_CLASSIC_GAME_MODE_MENU:
      {
        return new MultiplayerClassicGameModeMenuScreen (screenChanger, screenSize, skin);
      }
      case MULTIPLAYER_PERIL_GAME_MODE_MENU:
      {
        return new MultiplayerPerilGameModeMenuScreen (screenChanger, screenSize, skin);
      }
      case PLAY_CLASSIC:
      {
        return PlayScreenFactory.create (screenChanger, screenSize, mouseInput, GameMode.CLASSIC, skin, eventBus);
      }
      case PLAY_PERIL:
      {
        return PlayScreenFactory.create (screenChanger, screenSize, mouseInput, GameMode.PERIL, skin, eventBus);
      }
      default:
      {
        throw new IllegalStateException ("Unknown screen id [" + screenId + "].");
      }
    }
  }
}
