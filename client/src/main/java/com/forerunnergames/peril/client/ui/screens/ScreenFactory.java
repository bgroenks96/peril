package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenFactory;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreenMusic;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class ScreenFactory
{
  public static Screen create (final ScreenId screenId,
                               final ScreenController screenController,
                               final MouseInput mouseInput)
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
      case PLAY:
      {
        return PlayScreenFactory.create (screenController, mouseInput);
      }
      default:
      {
        throw new IllegalStateException ("Unknown screen id [" + screenId + "].");
      }
    }
  }

  private ScreenFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
