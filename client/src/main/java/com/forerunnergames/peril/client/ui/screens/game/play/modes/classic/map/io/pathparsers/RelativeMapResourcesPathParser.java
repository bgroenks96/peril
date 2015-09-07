package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

/**
 * Use to resolve map resource paths ONLY for resources that WILL be loaded with an
 * {@link com.badlogic.gdx.assets.AssetManager}
 */
public final class RelativeMapResourcesPathParser extends AbstractMapResourcesPathParser
{
  public RelativeMapResourcesPathParser (final GameMode gameMode)
  {
    super (gameMode);
  }

  @Override
  protected String parseMapsModePath (final MapType mapType, final GameMode gameMode)
  {
    Arguments.checkIsNotNull (mapType, "mapType");
    Arguments.checkIsNotNull (gameMode, "gameMode");

    switch (mapType)
    {
      case STOCK:
      case CUSTOM:
      {
        return AssetSettings.RELATIVE_MAPS_DIRECTORY + gameMode.name ().toLowerCase () + " mode/";
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Unsupported {}: [{}].", MapType.class.getSimpleName (), mapType));
      }
    }
  }

  @Override
  protected String parseMapNameAsPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return mapMetadata.getName ().toLowerCase () + "/";
  }
}
