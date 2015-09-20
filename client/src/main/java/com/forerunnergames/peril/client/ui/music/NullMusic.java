package com.forerunnergames.peril.client.ui.music;

import com.badlogic.gdx.audio.Music;

import com.forerunnergames.tools.common.Arguments;

public final class NullMusic implements Music
{
  @Override
  public void play ()
  {
  }

  @Override
  public void pause ()
  {
  }

  @Override
  public void stop ()
  {
  }

  @Override
  public boolean isPlaying ()
  {
    return false;
  }

  @Override
  public void setLooping (final boolean isLooping)
  {
  }

  @Override
  public boolean isLooping ()
  {
    return false;
  }

  @Override
  public void setVolume (final float volume)
  {
  }

  @Override
  public float getVolume ()
  {
    return 0;
  }

  @Override
  public void setPan (final float pan, final float volume)
  {
  }

  @Override
  public void setPosition (final float position)
  {
  }

  @Override
  public float getPosition ()
  {
    return 0;
  }

  @Override
  public void dispose ()
  {
  }

  @Override
  public void setOnCompletionListener (final OnCompletionListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");
  }
}
