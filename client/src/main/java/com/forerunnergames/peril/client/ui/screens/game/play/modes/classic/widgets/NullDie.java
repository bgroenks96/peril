package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.tools.common.Arguments;

public final class NullDie implements Die
{
  private static final Actor NULL_ACTOR = new Actor ();
  private static final int NULL_INDEX = -1;

  NullDie ()
  {
  }

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
  public void addListener (final DieListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");
  }

  @Override
  public void reset ()
  {
  }

  @Override
  public void resetPreservingFaceValue ()
  {
  }

  @Override
  public void refreshAssets ()
  {
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
