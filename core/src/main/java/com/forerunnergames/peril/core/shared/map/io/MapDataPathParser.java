package com.forerunnergames.peril.core.shared.map.io;

import com.forerunnergames.peril.core.shared.game.GameMode;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.MapType;

public interface MapDataPathParser
{
  String parseCountriesFileNamePath (final MapMetadata mapMetadata);

  String parseCountryGraphFileNamePath (final MapMetadata mapMetadata);

  String parseContinentGraphFileNamePath (final MapMetadata mapMetadata);

  String parseContinentsFileNamePath (final MapMetadata mapMetadata);

  String parseCountryDataPath (final MapMetadata mapMetadata);

  String parseContinentDataPath (final MapMetadata mapMetadata);

  String parseMapNamePath (final MapMetadata mapMetadata);

  String parseCountriesPath (final MapMetadata mapMetadata);

  String parseContinentsPath (final MapMetadata mapMetadata);

  String parseMapTypePath (MapType mapType);

  GameMode getGameMode ();
}
