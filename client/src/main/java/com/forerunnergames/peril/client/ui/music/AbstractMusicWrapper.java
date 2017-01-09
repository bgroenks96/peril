/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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
import com.forerunnergames.tools.common.Strings;

import java.util.Objects;

abstract class AbstractMusicWrapper implements MusicWrapper
{
  private final Music music;
  private final String fileName;

  AbstractMusicWrapper (final Music music, final String fileName)
  {
    Arguments.checkIsNotNull (music, "music");
    Arguments.checkIsNotNull (fileName, "fileName");

    this.music = music;
    this.fileName = fileName;
  }

  @Override
  public final String getFileName ()
  {
    return fileName;
  }

  @Override
  public final void play ()
  {
    music.play ();
  }

  @Override
  public final void pause ()
  {
    music.pause ();
  }

  @Override
  public final void stop ()
  {
    music.stop ();
  }

  @Override
  public final boolean isPlaying ()
  {
    return music.isPlaying ();
  }

  @Override
  public int hashCode ()
  {
    int result = music.hashCode ();
    result = 31 * result + fileName.hashCode ();
    return result;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    final AbstractMusicWrapper that = (AbstractMusicWrapper) obj;

    return Objects.equals (music, that.music) && Objects.equals (fileName, that.fileName);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Music: [{}] | FileName: [{}]", getClass ().getSimpleName (), music, fileName);
  }

  @Override
  public final void setLooping (final boolean isLooping)
  {
    music.setLooping (isLooping);
  }

  @Override
  public final boolean isLooping ()
  {
    return music.isLooping ();
  }

  @Override
  public final void setVolume (final float volume)
  {
    music.setVolume (volume);
  }

  @Override
  public final float getVolume ()
  {
    return music.getVolume ();
  }

  @Override
  public final void setPan (final float pan, final float volume)
  {
    music.setPan (pan, volume);
  }

  @Override
  public final void setPosition (final float position)
  {
    music.setPosition (position);
  }

  @Override
  public final float getPosition ()
  {
    return music.getPosition ();
  }

  @Override
  public final void dispose ()
  {
    music.dispose ();
  }

  @Override
  public final void setOnCompletionListener (final OnCompletionListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    music.setOnCompletionListener (listener);
  }
}
