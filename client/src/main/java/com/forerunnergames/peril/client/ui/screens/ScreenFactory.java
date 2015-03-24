package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenFactory;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreenMusic;
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

  public Screen create (final ScreenId screenId, final ScreenController screenController, final MouseInput mouseInput)
  {
    Arguments.checkIsNotNull (screenId, "screenId");
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");

    switch (screenId)
    {
      case MAIN_MENU:
      {
        return new MainMenuScreen (screenController, new MainMenuScreenMusic ());
      }
      case PLAY_CLASSIC:
      {
        return PlayScreenFactory.create (screenController, mouseInput, GameMode.CLASSIC, eventBus);
      }
      case PLAY_PERIL:
      {
        return PlayScreenFactory.create (screenController, mouseInput, GameMode.PERIL, eventBus);
      }
      default:
      {
        throw new IllegalStateException ("Unknown screen id [" + screenId + "].");
      }
    }
  }
}
