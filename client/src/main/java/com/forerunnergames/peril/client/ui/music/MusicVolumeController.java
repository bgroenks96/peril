/*
 * Copyright © 2016 Forerunner Games, LLC.
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MusicVolumeController extends ControllerAdapter implements MusicVolume
{
  private static final Logger log = LoggerFactory.getLogger (MusicVolumeController.class);
  private float volume = MusicSettings.INITIAL_VOLUME;
  private MusicVolumeListener listener = new NullMusicVolumeListener ();

  @Override
  public void update ()
  {
    if (Gdx.input.isKeyPressed (Input.Keys.COMMA))
    {
      if (Gdx.input.isKeyPressed (Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed (Input.Keys.SHIFT_RIGHT))
      {
        setVolume (MusicSettings.MIN_VOLUME);
      }
      else
      {
        changeVolumeBy (-MusicSettings.VOLUME_INCREMENT);
      }
    }

    if (Gdx.input.isKeyPressed (Input.Keys.PERIOD))
    {
      if (Gdx.input.isKeyPressed (Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed (Input.Keys.SHIFT_RIGHT))
      {
        setVolume (MusicSettings.MAX_VOLUME);
      }
      else
      {
        changeVolumeBy (MusicSettings.VOLUME_INCREMENT);
      }
    }

    if (Gdx.input.isKeyPressed (Input.Keys.SLASH)) setVolume (MusicSettings.INITIAL_VOLUME);
  }

  @Override
  public float getVolume ()
  {
    return volume;
  }

  @Override
  public void setVolume (final float volume)
  {
    Arguments.checkLowerInclusiveBound (volume, MusicSettings.MIN_VOLUME, "volume");
    Arguments.checkUpperInclusiveBound (volume, MusicSettings.MAX_VOLUME, "volume");

    if (Math.abs (this.volume - volume) < MusicSettings.VOLUME_EQUALITY_EPSILON) return;

    this.volume = volume;

    listener.onVolumeChanged (volume);
  }

  @Override
  public void changeVolumeBy (@AllowNegative final float volumeDelta)
  {
    if (volume + volumeDelta > MusicSettings.MAX_VOLUME || volume + volumeDelta < MusicSettings.MIN_VOLUME) return;

    log.debug ("Changing volume level from {} to {}.", volume, volume + volumeDelta);

    volume += volumeDelta;

    listener.onVolumeChanged (volume);
  }

  @Override
  public void setListener (final MusicVolumeListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    this.listener = listener;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Volume: {}", getClass ().getSimpleName (), volume);
  }
}
