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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.PlayMapGraphicsPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.RelativePlayMapGraphicsPathParser;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultPlayMapBackgroundImageLoader implements PlayMapBackgroundImageLoader
{
  private static final Logger log = LoggerFactory.getLogger (DefaultPlayMapBackgroundImageLoader.class);
  private final Map <PlayMapMetadata, String> loadedImageFileNames = new HashMap<> ();
  private final AssetManager assetManager;

  public DefaultPlayMapBackgroundImageLoader (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public void load (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions
            .checkIsTrue (!loadedImageFileNames.containsKey (playMapMetadata),
                          Strings.format ("Background image for play map [{}] was already loaded.", playMapMetadata));

    final PlayMapGraphicsPathParser pathParser = new RelativePlayMapGraphicsPathParser (playMapMetadata.getMode ());
    final String imageFileNamePath = pathParser.parseBackgroundImageFileNamePath (playMapMetadata);

    assetManager.load (imageFileNamePath, AssetSettings.PLAY_MAP_BACKGROUND_IMAGE_TYPE,
                       AssetSettings.PLAY_MAP_BACKGROUND_IMAGE_PARAMETER);

    loadedImageFileNames.put (playMapMetadata, imageFileNamePath);
  }

  @Override
  public boolean isFinishedLoading (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions
            .checkIsTrue (loadedImageFileNames.containsKey (playMapMetadata),
                          Strings.format ("Background image for play map [{}] was never loaded.", playMapMetadata));

    return assetManager.isLoaded (loadedImageFileNames.get (playMapMetadata));
  }

  @Override
  public Image get (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions
            .checkIsTrue (loadedImageFileNames.containsKey (playMapMetadata),
                          Strings.format ("Background image for play map [{}] was never loaded.", playMapMetadata));

    final String imageFileNamePath = loadedImageFileNames.get (playMapMetadata);

    if (!assetManager.isLoaded (imageFileNamePath, AssetSettings.PLAY_MAP_BACKGROUND_IMAGE_TYPE))
    {
      throw new PlayMapLoadingException (
              Strings.format ("Background image [{}] for map [{}] is not loaded.", imageFileNamePath, playMapMetadata));
    }

    return new Image (assetManager.get (imageFileNamePath, AssetSettings.PLAY_MAP_BACKGROUND_IMAGE_TYPE));
  }

  @Override
  public void unload (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    if (!loadedImageFileNames.containsKey (playMapMetadata))
    {
      log.warn ("Cannot unload background image for play map [{}] because it is not loaded.", playMapMetadata);
      return;
    }

    final String imageFileName = loadedImageFileNames.remove (playMapMetadata);

    if (!assetManager.isLoaded (imageFileName))
    {
      log.warn ("Cannot unload background image [{}] for play map [{}] because it is not loaded.", imageFileName,
                playMapMetadata);
      return;
    }

    assetManager.unload (imageFileName);
  }
}
