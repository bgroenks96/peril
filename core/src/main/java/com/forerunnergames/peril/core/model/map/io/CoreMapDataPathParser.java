package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.MapType;
import com.forerunnergames.peril.core.shared.map.PlayMapLoadingException;
import com.forerunnergames.peril.core.shared.map.io.AbstractMapDataPathParser;
import com.forerunnergames.peril.core.shared.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class CoreMapDataPathParser extends AbstractMapDataPathParser
{
  public CoreMapDataPathParser (final GameMode gameMode)
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
      case CUSTOM:
      {
        return AssetSettings.ABSOLUTE_EXTERNAL_MAPS_DIRECTORY + gameMode.name ().toLowerCase () + " mode/";
      }
      case STOCK:
      {
        return AssetSettings.ABSOLUTE_INTERNAL_MAPS_MODE_DIRECTORY + gameMode.name ().toLowerCase () + "/";
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

    switch (mapMetadata.getType ())
    {
      case CUSTOM:
      {
        return mapMetadata.getName ().toLowerCase () + "/";
      }
      case STOCK:
      {
        return mapMetadata.getName ().replace (" ", "_").toLowerCase () + "/";
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Unsupported {}: [{}].", MapType.class.getSimpleName (), mapMetadata.getType ()));
      }
    }
  }
}
