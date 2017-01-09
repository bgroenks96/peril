/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.Arguments;

public final class MusicFactory
{
  private final AssetManager assetManager;

  public MusicFactory (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  public MusicWrapper create (final ScreenId screenId)
  {
    Arguments.checkIsNotNull (screenId, "screenId");

    switch (screenId)
    {
      case NONE:
      case SPLASH:
      {
        return MusicWrapper.NULL;
      }
      case MAIN_MENU:
      case GAME_MODES_MENU:
      case CLASSIC_GAME_MODE_MENU:
      case PLAYER_PERIL_GAME_MODE_MENU:
      case CLASSIC_GAME_MODE_CREATE_GAME_MENU:
      case CLASSIC_GAME_MODE_JOIN_GAME_MENU:
      case MENU_TO_PLAY_LOADING:
      {
        return getMusic (AssetSettings.MENU_SCREEN_MUSIC_ASSET_DESCRIPTOR);
      }
      case PLAY_CLASSIC:
      case PLAY_PERIL:
      case PLAY_TO_MENU_LOADING:
      {
        return getMusic (AssetSettings.PLAY_SCREEN_MUSIC_ASSET_DESCRIPTOR);
      }
      default:
      {
        throw new IllegalStateException ("Unknown " + ScreenId.class.getSimpleName () + " [" + screenId + "].");
      }
    }
  }

  private MusicWrapper getMusic (final AssetDescriptor <Music> descriptor)
  {
    assert descriptor != null;
    if (!assetManager.isLoaded (descriptor)) return MusicWrapper.NULL;
    final Music music = assetManager.get (descriptor);
    return new DefaultMusicWrapper (music, descriptor.fileName);
  }
}
