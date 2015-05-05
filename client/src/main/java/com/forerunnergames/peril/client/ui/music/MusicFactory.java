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
      case MULTIPLAYER_GAME_MODES_MENU:
      case MULTIPLAYER_CLASSIC_GAME_MODE_MENU:
      case MULTIPLAYER_PERIL_GAME_MODE_MENU:
      case MULTIPLAYER_CLASSIC_GAME_MODE_CREATE_GAME_MENU:
      {
        return Assets.menuMusic;
      }
      case PLAY_CLASSIC:
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
