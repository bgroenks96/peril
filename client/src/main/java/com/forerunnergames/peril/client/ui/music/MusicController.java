package com.forerunnergames.peril.client.ui.music;

import com.badlogic.gdx.audio.Music;

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nullable;

public final class MusicController extends ControllerAdapter implements MusicChanger
{
  private final MusicFactory musicFactory;
  private final Map <ScreenId, Music> music = new EnumMap <> (ScreenId.class);

  public MusicController (final MusicFactory musicFactory)
  {
    Arguments.checkIsNotNull (musicFactory, "musicFactory");

    this.musicFactory = musicFactory;
  }

  @Override
  public void shutDown ()
  {
    music.clear ();
  }

  @Override
  public void changeMusic (@Nullable final ScreenId fromScreen, final ScreenId toScreen)
  {
    Arguments.checkIsNotNull (toScreen, "toScreen");

    if (!MusicSettings.isEnabled ()) return;
    if (!music.containsKey (toScreen)) music.put (toScreen, musicFactory.create (toScreen));

    final Music oldMusic = music.get (fromScreen);
    final Music newMusic = music.get (toScreen);

    if (newMusic.equals (oldMusic)) return;
    if (oldMusic != null && oldMusic.isPlaying ()) oldMusic.stop ();
    if (newMusic.isPlaying ()) return;

    newMusic.setVolume (MusicSettings.getInitialVolume ());
    newMusic.setLooping (true);
    newMusic.play ();
  }
}
