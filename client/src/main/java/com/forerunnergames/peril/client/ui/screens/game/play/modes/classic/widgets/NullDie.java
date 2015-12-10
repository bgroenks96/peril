package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.tools.common.Arguments;

final class NullDie implements Die
{
  private static final Actor NULL_ACTOR = new Actor ();
  private static final int NULL_INDEX = -1;

  @Override
  public int getIndex ()
  {
    return NULL_INDEX;
  }

  @Override
  public void roll (final DieFaceValue faceValue)
  {
    Arguments.checkIsNotNull (faceValue, "faceValue");
  }

  @Override
  public void setOutcomeAgainst (final DieFaceValue competingFaceValue)
  {
    Arguments.checkIsNotNull (competingFaceValue, "competingFaceValue");
  }

  @Override
  public void setOutcome (final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (outcome, "outcome");
  }

  @Override
  public DieOutcome getOutcome ()
  {
    return DieOutcome.NONE;
  }

  @Override
  public boolean hasWinOutcome ()
  {
    return false;
  }

  @Override
  public boolean hasLoseOutcome ()
  {
    return false;
  }

  @Override
  public void enable ()
  {
  }

  @Override
  public void disable ()
  {
  }

  @Override
  public void setTouchable (final boolean isTouchable)
  {
  }

  @Override
  public void resetSpinning ()
  {
  }

  @Override
  public void addListener (final DieListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");
  }

  @Override
  public void resetState ()
  {
  }

  @Override
  public void resetFaceValue ()
  {
  }

  @Override
  public void resetOutcome ()
  {
  }

  @Override
  public void resetAll ()
  {
  }

  @Override
  public void refreshAssets ()
  {
  }

  @Override
  public void update (final float delta)
  {
  }

  @Override
  public float getWidth ()
  {
    return NULL_ACTOR.getWidth ();
  }

  @Override
  public float getHeight ()
  {
    return NULL_ACTOR.getHeight ();
  }

  @Override
  public Actor asActor ()
  {
    return NULL_ACTOR;
  }

  @Override
  public int compareTo (final Die o)
  {
    if (NULL_INDEX == o.getIndex ()) return 0;

    return NULL_INDEX < o.getIndex () ? -1 : 1;
  }

  @Override
  public int hashCode ()
  {
    return NULL_INDEX;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    return NULL_INDEX == ((Die) obj).getIndex ();
  }
}
