package com.forerunnergames.peril.client.ui.music;

import com.badlogic.gdx.audio.Music;

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public final class MusicController extends ControllerAdapter implements MusicChanger
{
  private final MusicFactory musicFactory;
  private final MusicSettings musicSettings;
  private Map <ScreenId, Music> music = new HashMap <> (ScreenId.values ().length);

  public MusicController (final MusicFactory musicFactory, final MusicSettings musicSettings)
  {
    Arguments.checkIsNotNull (musicFactory, "musicFactory");
    Arguments.checkIsNotNull (musicSettings, "musicSettings");

    this.musicFactory = musicFactory;
    this.musicSettings = musicSettings;
  }

  @Override
  public void initialize ()
  {
    for (final ScreenId screenId : ScreenId.values ())
    {
      music.put (screenId, musicFactory.create (screenId));
    }
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
    Arguments.checkIsTrue (fromScreen == null || music.containsKey (fromScreen), "Cannot find music for screen ["
            + fromScreen + "].");
    Arguments.checkIsTrue (music.containsKey (toScreen), "Cannot find music for screen [" + toScreen + "].");

    if (! musicSettings.isEnabled ()) return;

    final Music oldMusic = music.get (fromScreen);
    final Music newMusic = music.get (toScreen);

    if (newMusic.equals (oldMusic)) return;
    if (oldMusic != null && oldMusic.isPlaying ()) oldMusic.stop ();
    if (newMusic.isPlaying ()) return;

    newMusic.setVolume (musicSettings.getInitialVolume ());
    newMusic.setLooping (true);
    newMusic.play ();
  }
}
