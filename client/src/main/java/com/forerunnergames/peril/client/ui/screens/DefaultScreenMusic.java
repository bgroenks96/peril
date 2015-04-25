package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.audio.Music;

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultScreenMusic implements ScreenMusic
{
  private final Music music;
  private final MusicSettings settings;

  public DefaultScreenMusic (final Music music, final MusicSettings settings)
  {
    Arguments.checkIsNotNull (music, "music");
    Arguments.checkIsNotNull (settings, "settings");

    this.music = music;
    this.settings = settings;
  }

  @Override
  public void start ()
  {
    if (!settings.isEnabled () || music.isPlaying ()) return;

    music.setVolume (settings.getInitialVolume ());
    music.setLooping (true);
    music.play ();
  }

  @Override
  public void stop ()
  {
    if (!settings.isEnabled () || !music.isPlaying ()) return;

    music.stop ();
  }
}
