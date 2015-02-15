package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.tools.common.Message;

public interface MessageBox <T extends Message>
{
  public void addMessage (final T message);
  public void showLastMessage ();
  public void clear ();
  public Actor asActor();
}
