package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface Popup
{
  void show ();

  void hide ();

  boolean isShown ();

  Actor asActor ();
}
