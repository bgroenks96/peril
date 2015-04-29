package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.peril;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;

public final class MultiplayerPerilGameModeMenuScreen extends AbstractMenuScreen
{
  public MultiplayerPerilGameModeMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                             final ScreenChanger screenChanger,
                                             final ScreenSize screenSize)
  {
    super (widgetFactory, screenChanger, screenSize);
  }

  @Override
  protected void addTitle ()
  {
    addTitleWithSubtitle ("MULTIPLAYER", "PERIL MODE");
  }

  @Override
  protected void addMenuChoices ()
  {
    addMenuChoice ("CREATE GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (InputEvent event, float x, float y)
      {
        toScreen (ScreenId.PLAY_PERIL);
      }
    });

    addMenuChoice ("JOIN GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (InputEvent event, float x, float y)
      {
        toScreen (ScreenId.PLAY_PERIL);
      }
    });
  }

  @Override
  protected void onEscape ()
  {
    toScreen (ScreenId.MULTIPLAYER_GAME_MODES_MENU);
  }

  @Override
  protected void addButtons ()
  {
    addBackButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (InputEvent event, float x, float y)
      {
        toScreen (ScreenId.MULTIPLAYER_GAME_MODES_MENU);
      }
    });
  }
}
