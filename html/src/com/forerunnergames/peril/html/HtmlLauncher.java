package com.forerunnergames.peril.html;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import com.forerunnergames.peril.client.application.LibGdxGameFactory;

public final class HtmlLauncher extends GwtApplication
{
  @Override
  public GwtApplicationConfiguration getConfig()
  {
    return new GwtApplicationConfiguration (640, 480);
  }

  @Override
  public ApplicationListener getApplicationListener()
  {
    return LibGdxGameFactory.create();
  }
}