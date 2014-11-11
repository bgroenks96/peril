package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.forerunnergames.tools.common.Arguments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScreenChanger implements ScreenChanger
{
  protected final Logger log = LoggerFactory.getLogger (getClass());
  private final Game game;
  private final ScreenManager screenManager;

  protected AbstractScreenChanger (final Game game, final ScreenManager screenManager)
  {
    Arguments.checkIsNotNull (game, "game");
    Arguments.checkIsNotNull (screenManager, "screenManager");

    this.game = game;
    this.screenManager = screenManager;
  }

  protected void setScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    final ScreenId previousId = screenManager.get (game.getScreen());

    game.setScreen (screenManager.get (id));

    log.info ("Changed from {} [{}] to {} [{}].", Screen.class.getSimpleName(), previousId, Screen.class.getSimpleName(), id);
  }
}
