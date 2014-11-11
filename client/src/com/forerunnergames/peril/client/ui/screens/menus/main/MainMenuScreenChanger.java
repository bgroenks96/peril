package com.forerunnergames.peril.client.ui.screens.menus.main;

import com.forerunnergames.peril.client.ui.screens.AbstractScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenController;

public final class MainMenuScreenChanger extends AbstractScreenChanger
{
  public MainMenuScreenChanger (final ScreenController screenController)
  {
    super (screenController);
  }

  @Override
  public void next()
  {
    setScreenTo (ScreenId.PLAY);
  }
}
