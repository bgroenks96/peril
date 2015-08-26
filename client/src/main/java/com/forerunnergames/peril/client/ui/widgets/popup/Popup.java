package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.tools.common.Message;

public interface Popup
{
  void show ();

  void hide ();

  void setMessage (final Message message);

  boolean isShown ();

  Actor asActor ();
}
