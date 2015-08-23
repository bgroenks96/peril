package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public abstract class AbstractMapDataPathParser implements MapDataPathParser
{
  private final GameMode gameMode;

  protected AbstractMapDataPathParser (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    this.gameMode = gameMode;
  }

  @Override
  public final String parseCountriesFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountryDataPath (mapMetadata) + AssetSettings.COUNTRY_DATA_FILENAME;
  }

  @Override
  public final String parseCountryGraphFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountryDataPath (mapMetadata) + AssetSettings.COUNTRY_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentGraphFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseContinentDataPath (mapMetadata) + AssetSettings.CONTINENT_GRAPH_FILENAME;
  }

  @Override
  public final String parseContinentsFileNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseContinentDataPath (mapMetadata) + AssetSettings.CONTINENT_DATA_FILENAME;
  }

  @Override
  public final String parseCountryDataPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseCountriesPath (mapMetadata) + AssetSettings.RELATIVE_COUNTRY_DATA_DIRECTORY;
  }

  @Override
  public final String parseContinentDataPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseContinentsPath (mapMetadata) + AssetSettings.RELATIVE_CONTINENT_DATA_DIRECTORY;
  }

  @Override
  public final String parseMapNamePath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapTypePath (mapMetadata.getType ()) + parseMapNameAsPath (mapMetadata);
  }

  @Override
  public final String parseCountriesPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapNamePath (mapMetadata) + AssetSettings.RELATIVE_COUNTRIES_DIRECTORY;
  }

  @Override
  public final String parseContinentsPath (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return parseMapNamePath (mapMetadata) + AssetSettings.RELATIVE_CONTINENTS_DIRECTORY;
  }

  @Override
  public final String parseMapTypePath (final MapType mapType)
  {
    Arguments.checkIsNotNull (mapType, "mapType");

    return parseMapsModePath (mapType, gameMode) + parseMapTypeAsPath (mapType);
  }

  @Override
  public final GameMode getGameMode ()
  {
    return gameMode;
  }

  protected abstract String parseMapsModePath (final MapType mapType, final GameMode gameMode);
  protected abstract String parseMapNameAsPath (final MapMetadata mapMetadata);

  private String parseMapTypeAsPath (final MapType mapType)
  {
    return mapType.name ().toLowerCase () + "/";
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}: {}", getClass ().getSimpleName (), gameMode.getClass ().getSimpleName (), gameMode);
  }
}
