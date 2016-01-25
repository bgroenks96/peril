/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
