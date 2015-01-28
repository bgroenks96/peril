package com.forerunnergames.peril.client.ui.screens.menus.main;

import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;

public final class MainMenuScreenMusic implements ScreenMusic
{
  @Override
  public void start ()
  {
    Assets.menuMusic.setVolume (MusicSettings.INITIAL_VOLUME);
    Assets.menuMusic.setLooping (true);
    Assets.menuMusic.play ();
  }

  @Override
  public void stop ()
  {
    Assets.menuMusic.stop ();
  }
}
