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
