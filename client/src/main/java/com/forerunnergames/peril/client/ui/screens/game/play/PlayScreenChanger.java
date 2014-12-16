package com.forerunnergames.peril.client.ui.screens.game.play;

import com.forerunnergames.peril.client.ui.screens.AbstractScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenController;

public final class PlayScreenChanger extends AbstractScreenChanger
{
  public PlayScreenChanger (final ScreenController screenController)
  {
    super (screenController);
  }

  @Override
  public void previous()
  {
    setScreenTo (ScreenId.MAIN_MENU);
  }
}
