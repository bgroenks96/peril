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

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

public final class MusicController extends ControllerAdapter implements MusicChanger
{
  private final MusicFactory musicFactory;

  public MusicController (final MusicFactory musicFactory)
  {
    Arguments.checkIsNotNull (musicFactory, "musicFactory");

    this.musicFactory = musicFactory;
  }

  @Override
  public void changeMusic (final ScreenId fromScreen, final ScreenId toScreen)
  {
    Arguments.checkIsNotNull (fromScreen, "fromScreen");
    Arguments.checkIsNotNull (toScreen, "toScreen");

    if (!MusicSettings.IS_ENABLED) return;

    final Music oldMusic = musicFactory.create (fromScreen);
    final Music newMusic = musicFactory.create (toScreen);

    if (newMusic.equals (oldMusic)) return;
    if (oldMusic.isPlaying ()) oldMusic.stop ();
    if (newMusic.isPlaying ()) return;

    newMusic.setVolume (MusicSettings.INITIAL_VOLUME);
    newMusic.setLooping (true);
    newMusic.play ();
  }
}
