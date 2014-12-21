package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreen;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenMusic;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreenMusic;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class ScreenFactory
{
  public static Screen create (final ScreenId screenId, final ScreenController screenController)
  {
    Arguments.checkIsNotNull (screenId, "screenId");
    Arguments.checkIsNotNull (screenController, "screenController");

    switch (screenId)
    {
      case MAIN_MENU:
      {
        return new MainMenuScreen (screenController, new MainMenuScreenMusic());
      }
      case PLAY:
      {
        return new PlayScreen (screenController, new PlayScreenMusic());
      }
      default:
      {
        throw new IllegalStateException ("Unknown screen id [" + screenId + "].");
      }
    }
  }

  private ScreenFactory()
  {
    Classes.instantiationNotAllowed();
  }
}
