package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;

public final class MultiplayerGameModesMenuScreen extends AbstractMenuScreen
{
  public MultiplayerGameModesMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                         final ScreenChanger screenChanger,
                                         final ScreenSize screenSize)
  {
    super (widgetFactory, screenChanger, screenSize);

    addTitle ("MULTIPLAYER", Align.bottomLeft, 40);
    addTitle ("GAME MODES", Align.topLeft, 40);

    addMenuChoiceSpacer (22);

    addMenuChoice ("CLASSIC", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_MENU);
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("PERIL", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.MULTIPLAYER_PERIL_GAME_MODE_MENU);
      }
    });

    addBackButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.MAIN_MENU);
      }
    });
  }

  @Override
  protected void onEscape ()
  {
    toScreen (ScreenId.MAIN_MENU);
  }
}
