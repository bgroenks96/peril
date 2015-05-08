package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;

public final class MultiplayerClassicGameModeMenuScreen extends AbstractMenuScreen
{
  public MultiplayerClassicGameModeMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                               final ScreenChanger screenChanger,
                                               final ScreenSize screenSize)
  {
    super (widgetFactory, screenChanger, screenSize);

    addTitle ("MULTIPLAYER", Align.bottomLeft, 40);
    addTitle ("CLASSIC MODE", Align.topLeft, 40);

    addMenuChoiceSpacer (22);

    addMenuChoice ("CREATE GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_CREATE_GAME_MENU);
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("JOIN GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_JOIN_GAME_MENU);
      }
    });

    addBackButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.MULTIPLAYER_GAME_MODES_MENU);
      }
    });
  }

  @Override
  protected void onEscape ()
  {
    toScreen (ScreenId.MULTIPLAYER_GAME_MODES_MENU);
  }
}
