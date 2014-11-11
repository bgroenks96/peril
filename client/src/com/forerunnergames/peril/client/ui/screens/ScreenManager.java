package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreen;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenChanger;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreenChanger;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public final class ScreenManager
{
  private final BiMap <ScreenId, Screen> screens;

  public ScreenManager (final Game game)
  {
    Arguments.checkIsNotNull (game, "game");

    screens = ImmutableBiMap.of (
            ScreenId.MAIN_MENU, (Screen) new MainMenuScreen (new MainMenuScreenChanger (game, this)),
            ScreenId.PLAY, new PlayScreen (new PlayScreenChanger (game, this)));
  }

  public Screen get (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsTrue (screens.containsKey (id), "Cannot find " + Screen.class.getSimpleName() + " with " + id.getClass().getSimpleName() + " [" + id + "].");

    return screens.get (id);
  }

  public ScreenId get (final Screen screen)
  {
    Arguments.checkIsNotNull (screen, "screen");
    Arguments.checkIsTrue (screens.containsValue (screen), "Cannot find " + ScreenId.class.getSimpleName()  + " of " + screen.getClass().getSimpleName() + " [" + screen + "].");

    return screens.inverse().get (screen);
  }

  public void dispose()
  {
    for (final Screen screen : screens.values())
    {
      screen.dispose();
    }
  }
}
