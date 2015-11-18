package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.DieFaceValue;

public interface Die extends Comparable <Die>
{
  int getIndex ();

  boolean isActive ();

  void roll (final DieFaceValue faceValue);

  void activate ();

  void deactivate ();

  void setTouchable (boolean isTouchable);

  void addListener (final DieListener listener);

  void reset ();

  void refreshAssets ();

  Actor asActor ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();
}
