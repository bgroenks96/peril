package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface Popup
{
  void show ();

  void hide ();

  void onSubmit ();

  void onShow ();

  void onHide ();

  boolean isShown ();

  Actor asActor ();
}
