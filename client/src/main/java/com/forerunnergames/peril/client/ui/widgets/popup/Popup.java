package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import com.forerunnergames.tools.common.Message;

import javax.annotation.Nullable;

public interface Popup
{
  void show ();

  void show (@Nullable final Action action);

  void hide ();

  void hide (@Nullable final Action action);

  void setTitle (final String title);

  void setMessage (final Message message);

  boolean isShown ();

  void addListener (final EventListener listener);

  void enableInput ();

  void disableInput ();

  void update (final float delta);

  void refreshAssets ();
}
