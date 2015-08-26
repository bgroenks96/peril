package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.MapResourcesPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.RelativeMapResourcesPathParser;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultPlayMapInputDetectionImageLoader implements PlayMapInputDetectionImageLoader
{
  private static final Logger log = LoggerFactory.getLogger (DefaultPlayMapInputDetectionImageLoader.class);
  private final Map <MapMetadata, String> loadedImageFileNames = new HashMap <> ();
  private final AssetManager assetManager;

  public DefaultPlayMapInputDetectionImageLoader (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public void load (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Preconditions.checkIsTrue (!loadedImageFileNames.containsKey (mapMetadata),
                               Strings.format ("Input detection image for map [{}] was already loaded.", mapMetadata));

    final MapResourcesPathParser pathParser = new RelativeMapResourcesPathParser (mapMetadata.getMode ());
    final String imageFileNamePath = pathParser.parseInputDetectionImageFileNamePath (mapMetadata);

    assetManager.load (imageFileNamePath, AssetSettings.MAP_INPUT_DETECTION_IMAGE_TYPE);
    loadedImageFileNames.put (mapMetadata, imageFileNamePath);
  }

  @Override
  public boolean isFinishedLoading (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Preconditions.checkIsTrue (loadedImageFileNames.containsKey (mapMetadata),
                               Strings.format ("Input detection image for map [{}] was never loaded.", mapMetadata));

    return assetManager.isLoaded (loadedImageFileNames.get (mapMetadata));
  }

  @Override
  public Pixmap get (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Preconditions.checkIsTrue (loadedImageFileNames.containsKey (mapMetadata),
                               Strings.format ("Input detection image for map [{}] was never loaded.", mapMetadata));

    final String imageFileNamePath = loadedImageFileNames.get (mapMetadata);

    if (!assetManager.isLoaded (imageFileNamePath, AssetSettings.MAP_INPUT_DETECTION_IMAGE_TYPE))
    {
      throw new PlayMapLoadingException (Strings.format ("Input detection image [{}] for map [{}] is not loaded.",
                                                         imageFileNamePath, mapMetadata));
    }

    return assetManager.get (imageFileNamePath, AssetSettings.MAP_INPUT_DETECTION_IMAGE_TYPE);
  }

  @Override
  public void unload (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (!loadedImageFileNames.containsKey (mapMetadata))
    {
      log.warn ("Cannot unload input detection image for map [{}] because it is not loaded.", mapMetadata);
      return;
    }

    final String imageFileName = loadedImageFileNames.remove (mapMetadata);

    assetManager.finishLoadingAsset (imageFileName);

    if (!assetManager.isLoaded (imageFileName))
    {
      log.warn ("Cannot unload input detection image [{}] for map [{}] because it is not loaded.", imageFileName,
                mapMetadata);
      return;
    }

    assetManager.unload (imageFileName);
  }
}
