package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.menus.main.MainMenuScreen;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;

/**
 * Wraps the actual {@link com.forerunnergames.tools.common.Application} instance inside of a
 * {@link com.badlogic.gdx.Game}, which implements {@link com.badlogic.gdx.ApplicationListener} because
 * all of the executable sub-projects (android, desktop, html, & ios) must be passed an
 * {@link com.badlogic.gdx.ApplicationListener} instance. In other words, LibGDX demands ultimate control
 * over the client application, so the best way to deal with that is to wrap & delegate to the actual
 * {@link com.forerunnergames.tools.common.Application}.
 */
public final class LibGdxGameWrapper extends Game
{
  private final Application application;
  private Screen startScreen;

  LibGdxGameWrapper (final Application application)
  {
    Arguments.checkIsNotNull (application, "application");

    this.application = application;
  }

  @Override
  public void create()
  {
    application.initialize();
    Assets.load();
    startScreen = new MainMenuScreen();
    setScreen (startScreen);
  }

  @Override
  public void render() {
    if (application.shouldShutDown()) Gdx.app.exit();

    super.render();
  }

  @Override
  public void dispose()
  {
    super.dispose();
    startScreen.dispose();
    Assets.dispose();
    application.shutDown();
  }
}
