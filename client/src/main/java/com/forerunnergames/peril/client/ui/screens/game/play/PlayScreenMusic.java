package com.forerunnergames.peril.client.ui.screens.game.play;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;

public final class PlayScreenMusic implements ScreenMusic
{
  @Override
  public void start ()
  {
    Assets.playScreenMusic.setVolume (1.0f);
    Assets.playScreenMusic.setLooping (true);
    Assets.playScreenMusic.play ();
  }

  @Override
  public void stop ()
  {
    Assets.playScreenMusic.stop ();
  }
}
