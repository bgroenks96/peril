package com.forerunnergames.peril.client.ui.music;

import com.badlogic.gdx.audio.Music;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;

public final class MusicFactory
{
  public Music create (final ScreenId screenId)
  {
    Arguments.checkIsNotNull (screenId, "screenId");

    switch (screenId)
    {
      case MAIN_MENU:
      {
        return Assets.menuMusic;
      }
      case MULTIPLAYER_GAME_MODES_MENU:
      {
        return Assets.menuMusic;
      }
      case MULTIPLAYER_CLASSIC_GAME_MODE_MENU:
      {
        return Assets.menuMusic;
      }
      case MULTIPLAYER_PERIL_GAME_MODE_MENU:
      {
        return Assets.menuMusic;
      }
      case PLAY_CLASSIC:
      {
        return Assets.playScreenMusic;
      }
      case PLAY_PERIL:
      {
        return Assets.playScreenMusic;
      }
      default:
      {
        throw new IllegalStateException ("Unknown " + ScreenId.class.getSimpleName () + " [" + screenId + "].");
      }
    }
  }
}
