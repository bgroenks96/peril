package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.peril.core.shared.application.EventBasedApplication;
import com.forerunnergames.tools.common.Controller;

public final class ClientApplication extends EventBasedApplication implements ApplicationListener
{
  private final Game game;
  private Screen startScreen;

  ClientApplication (final Controller... controllers)
  {
    super (controllers);

    this.game = new Game()
    {
      @Override
      public void create()
      {
        initialize();
        Assets.load();
        startScreen = new MainMenuScreen();
        setScreen (startScreen);
      }

      @Override
      public void dispose()
      {
        startScreen.dispose();
        Assets.dispose();
        shutDown();
      }
    };
  }

  @Override
  public void create()
  {
    game.create();
  }

  @Override
  public void resize (int width, int height)
  {
    game.resize (width, height);
  }

  @Override
  public void render()
  {
    game.render();
  }

  @Override
  public void pause()
  {
    game.pause();
  }

  @Override
  public void resume()
  {
    game.resume();
  }

  @Override
  public void dispose()
  {
    game.dispose();
  }
}
