package com.forerunnergames.peril.client.ui.screens.menus.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListenerAdapter;

public final class MainMenuScreen extends AbstractMenuScreen
{
  private final Popup quitPopup;

  public MainMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                         final ScreenChanger screenChanger,
                         final ScreenSize screenSize,
                         final Cursor normalCursor,
                         final Batch batch)
  {
    super (widgetFactory, screenChanger, screenSize, normalCursor, batch);

    quitPopup = createQuitPopup ("Are you sure you want to quit?", new PopupListenerAdapter ()
    {
      @Override
      public void onSubmit ()
      {
        Gdx.app.exit ();
      }
    });

    addTitle ("MAIN MENU", Align.left, 60);

    addMenuChoiceSpacer (42);

    addMenuChoice ("SINGLE PLAYER", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Implement
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("MULTIPLAYER", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.MULTIPLAYER_GAME_MODES_MENU);
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("SETTINGS", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Implement
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("QUIT", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        quitPopup.show ();
      }
    });
  }

  @Override
  protected void onEscape ()
  {
    quitPopup.show ();
  }
}
