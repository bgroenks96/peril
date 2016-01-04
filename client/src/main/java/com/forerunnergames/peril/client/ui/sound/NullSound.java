package com.forerunnergames.peril.client.ui.sound;

import com.badlogic.gdx.audio.Sound;

public final class NullSound implements Sound
{
  @Override
  public long play ()
  {
    return 0;
  }

  @Override
  public long play (final float volume)
  {
    return 0;
  }

  @Override
  public long play (final float volume, final float pitch, final float pan)
  {
    return 0;
  }

  @Override
  public long loop ()
  {
    return 0;
  }

  @Override
  public long loop (final float volume)
  {
    return 0;
  }

  @Override
  public long loop (final float volume, final float pitch, final float pan)
  {
    return 0;
  }

  @Override
  public void stop ()
  {
  }

  @Override
  public void pause ()
  {
  }

  @Override
  public void resume ()
  {
  }

  @Override
  public void dispose ()
  {
  }

  @Override
  public void stop (final long soundId)
  {
  }

  @Override
  public void pause (final long soundId)
  {
  }

  @Override
  public void resume (final long soundId)
  {
  }

  @Override
  public void setLooping (final long soundId, final boolean looping)
  {
  }

  @Override
  public void setPitch (final long soundId, final float pitch)
  {
  }

  @Override
  public void setVolume (final long soundId, final float volume)
  {
  }

  @Override
  public void setPan (final long soundId, final float pan, final float volume)
  {
  }
}
