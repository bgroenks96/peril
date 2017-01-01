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
import com.badlogic.gdx.utils.Timer;

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MusicController extends ControllerAdapter implements MusicChanger
{
  private static final Logger log = LoggerFactory.getLogger (MusicController.class);
  private final MusicFactory musicFactory;
  private final MusicVolume masterVolume;
  private Music currentMusic = new NullMusic ();

  public MusicController (final MusicFactory musicFactory, final MusicVolume masterVolume)
  {
    Arguments.checkIsNotNull (musicFactory, "musicFactory");
    Arguments.checkIsNotNull (masterVolume, "masterVolume");

    this.musicFactory = musicFactory;
    this.masterVolume = masterVolume;

    this.masterVolume.setListener (new MusicVolumeListener ()
    {
      @Override
      public void onVolumeChanged (final float newVolume)
      {
        currentMusic.setVolume (newVolume);

        log.debug ("Set volume of current music [{}] to [{}].", currentMusic, newVolume);
      }
    });
  }

  @Override
  public void changeMusic (final ScreenId screen)
  {
    Arguments.checkIsNotNull (screen, "screen");

    if (!MusicSettings.IS_ENABLED) return;

    final Music newMusic = musicFactory.create (screen);

    if (newMusic.equals (currentMusic)) return;
    if (currentMusic.isPlaying ()) stopMusicWithFadeOut (currentMusic);

    log.debug ("Changing music from [{}] to [{}] for screen [{}].", currentMusic, newMusic, screen);

    currentMusic = newMusic;

    if (currentMusic.isPlaying ()) return;

    currentMusic.setLooping (true);
    startMusicWithFadeIn (currentMusic);
  }

  private void stopMusicWithFadeOut (final Music music)
  {
    log.trace ("Fading out music [{}]...", music);

    Timer.schedule (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        if (!music.isPlaying ())
        {
          cancel ();
          log.trace ("Stopping fading out music [{}] because it isn't playing anymore.", music);
          return;
        }

        final float currentVolume = music.getVolume ();
        final float delta = masterVolume.getVolume () / MusicSettings.FADE_VOLUME_REPEAT_COUNT;
        final float newVolume = currentVolume - delta;

        if (newVolume <= MusicSettings.MIN_VOLUME)
        {
          music.stop ();
          cancel ();
          log.trace ("Done fading out & stopping music [{}].", music);
          return;
        }

        music.setVolume (newVolume);
      }
    }, 0.0f, MusicSettings.FADE_VOLUME_INTERVAL_SECONDS, MusicSettings.FADE_VOLUME_REPEAT_COUNT);
  }

  private void startMusicWithFadeIn (final Music music)
  {
    log.trace ("Fading in music [{}]...", music);

    music.setVolume (MusicSettings.MIN_VOLUME);
    music.play ();

    Timer.schedule (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        if (!music.isPlaying ())
        {
          cancel ();
          log.trace ("Stopping fading in music [{}] because it isn't playing anymore.", music);
          return;
        }

        final float currentVolume = music.getVolume ();
        final float delta = masterVolume.getVolume () / MusicSettings.FADE_VOLUME_REPEAT_COUNT;
        final float newVolume = currentVolume + delta;

        if (newVolume > masterVolume.getVolume ())
        {
          cancel ();
          log.trace ("Done fading in music [{}].", music);
          return;
        }

        music.setVolume (newVolume);
      }
    }, 0.0f, MusicSettings.FADE_VOLUME_INTERVAL_SECONDS, MusicSettings.FADE_VOLUME_REPEAT_COUNT);
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Current Music: [{}] | Is Playing: [{}] | Is Looping: [{}] | "
                                   + "Volume: [{}] | Position: [{}] | Master Volume: [{}]",
                           getClass ().getSimpleName (), currentMusic, currentMusic.isPlaying (),
                           currentMusic.isLooping (), currentMusic.getVolume (), currentMusic.getPosition (),
                           masterVolume);
  }
}
