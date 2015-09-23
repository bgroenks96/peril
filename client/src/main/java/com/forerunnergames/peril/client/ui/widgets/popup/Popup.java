package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.EventListener;

import com.forerunnergames.tools.common.Message;

public interface Popup
{
  void show ();

  void hide ();

  void setMessage (final Message message);

  boolean isShown ();

  void addListener (final EventListener listener);

  void update (final float delta);
}
