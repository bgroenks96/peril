package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;

public interface MapResourcesPathParser extends MapDataPathParser
{
  String parseCountryAtlasesPath (final MapMetadata mapMetadata);

  String parseCountryImageDataFileNamePath (final MapMetadata mapMetadata);

  String parseCountryInputDetectionDataFileNamePath (final MapMetadata mapMetadata);

  String parseContinentInputDetectionDataFileNamePath (final MapMetadata mapMetadata);

  String parseInputDetectionImageFileNamePath (final MapMetadata mapMetadata);

  String parseBackgroundImageFileNamePath (final MapMetadata mapMetadata);
}
