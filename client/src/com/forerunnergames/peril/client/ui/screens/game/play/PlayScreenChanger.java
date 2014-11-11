package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.Game;

import com.forerunnergames.peril.client.ui.screens.AbstractScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenManager;

public final class PlayScreenChanger extends AbstractScreenChanger
{
  public PlayScreenChanger (final Game game, final ScreenManager screenManager)
  {
    super (game, screenManager);
  }

  @Override
  public void next()
  {
  }

  @Override
  public void previous()
  {
    setScreen (ScreenId.MAIN_MENU);
  }
}
