package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.DieFaceValue;

import com.google.common.collect.ImmutableList;

public interface Dice
{
  int getActiveCount ();

  void roll (ImmutableList <DieFaceValue> dieFaceValues);

  void clampToMax (int minDieCount, int maxDieCount);

  void setTouchable (final boolean isTouchable);

  void reset ();

  void resetPreservingFaceValue ();

  void refreshAssets ();

  Actor asActor ();

  @Override
  String toString ();
}
