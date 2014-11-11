package com.forerunnergames.peril.client.ui.screens.menus.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.ui.screens.AbstractScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenManager;

public final class MainMenuScreenChanger extends AbstractScreenChanger
{
  public MainMenuScreenChanger (final Game game, final ScreenManager screenManager)
  {
    super (game, screenManager);
  }

  @Override
  public void next()
  {
    setScreen (ScreenId.PLAY);
  }

  @Override
  public void previous()
  {
    Gdx.app.exit();
  }
}
