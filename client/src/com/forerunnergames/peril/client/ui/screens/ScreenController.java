package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreen;
import com.forerunnergames.peril.client.ui.screens.game.play.PlayScreenChanger;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreenChanger;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenController extends ControllerAdapter
{
  private static final Logger log = LoggerFactory.getLogger (ScreenController.class);
  private final Game game;
  private BiMap <ScreenId, Screen> screens = HashBiMap.create (ScreenId.values().length);

  public ScreenController (final Game game)
  {
    Arguments.checkIsNotNull (game, "game");

    this.game = game;
  }

  @Override
  public void initialize()
  {
    screens.put (ScreenId.MAIN_MENU, new MainMenuScreen (new MainMenuScreenChanger (this)));
    screens.put (ScreenId.PLAY, new PlayScreen (new PlayScreenChanger (this)));

    setScreenTo (ScreenSettings.START_SCREEN);
  }

  public void setScreenTo (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsTrue (screens.containsKey (id), "Cannot find " + Screen.class.getSimpleName() + " with " + id.getClass().getSimpleName() + " [" + id + "].");

    final ScreenId previousId = getCurrentScreenId();

    game.setScreen (screens.get (id));

    log.info ("Changed from {} [{}] to {} [{}].", Screen.class.getSimpleName(), previousId, Screen.class.getSimpleName(), id);
  }

  private ScreenId getCurrentScreenId()
  {
    return screens.inverse().get (game.getScreen());
  }

  @Override
  public void shutDown()
  {
    for (final Screen screen : screens.values())
    {
      screen.dispose();
    }

    screens.clear();
  }
}
