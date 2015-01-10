package com.forerunnergames.peril.client.application;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.core.shared.application.EventBasedApplication;
import com.forerunnergames.tools.common.controllers.Controller;

public final class ClientApplication extends EventBasedApplication
{
  public ClientApplication (final Controller... controllers)
  {
    super (controllers);
  }

  @Override
  public void initialize ()
  {
    Assets.load ();

    super.initialize ();
  }

  @Override
  public void update ()
  {
    super.update ();

    if (shouldShutDown ()) Gdx.app.exit ();
  }

  @Override
  public void shutDown ()
  {
    super.shutDown ();

    Assets.dispose ();
  }
}
