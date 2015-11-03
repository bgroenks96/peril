package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.tools.common.Message;

public interface MessageBox <T extends Message>
{
  void addMessage (final T message);

  void showLastMessage ();

  void clear ();

  Actor asActor ();

  void refreshAssets ();
}
