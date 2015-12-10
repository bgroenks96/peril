package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;

public interface Die extends Comparable <Die>
{
  DieState DEFAULT_STATE = DieState.ENABLED;
  DieOutcome DEFAULT_OUTCOME = DieOutcome.NONE;
  Touchable DEFAULT_TOUCHABLE = Touchable.disabled;
  Die NULL_DIE = new NullDie ();

  int getIndex ();

  void roll (final DieFaceValue faceValue);

  void setOutcomeAgainst (final DieFaceValue competingFaceValue);

  void setOutcome (final DieOutcome outcome);

  DieOutcome getOutcome ();

  boolean hasWinOutcome ();

  boolean hasLoseOutcome ();

  void enable ();

  void disable ();

  void setTouchable (final boolean isTouchable);

  void resetSpinning ();

  void addListener (final DieListener listener);

  void resetState ();

  void resetFaceValue ();

  void resetOutcome ();

  void resetAll ();

  void refreshAssets ();

  void update (final float delta);

  float getWidth ();

  float getHeight ();

  Actor asActor ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();
}
