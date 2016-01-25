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
