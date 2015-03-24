package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;

public final class PlayScreenMusic implements ScreenMusic
{
  @Override
  public void start ()
  {
    Assets.playScreenMusic.setVolume (MusicSettings.INITIAL_VOLUME);
    Assets.playScreenMusic.setLooping (true);
    Assets.playScreenMusic.play ();
  }

  @Override
  public void stop ()
  {
    Assets.playScreenMusic.stop ();
  }
}
