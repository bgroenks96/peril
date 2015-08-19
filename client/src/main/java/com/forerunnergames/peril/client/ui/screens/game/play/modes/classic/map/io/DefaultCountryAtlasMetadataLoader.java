package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCountryAtlasMetadataLoader implements CountryAtlasMetadataLoader
{
  private static final Logger log = LoggerFactory.getLogger (DefaultCountryAtlasMetadataLoader.class);
  private final MapResourcesPathParser absoluteMapResourcesPathParser;
  private final MapResourcesPathParser relativeMapResourcesPathParser;
  private final MapMetadata mapMetadata;

  public DefaultCountryAtlasMetadataLoader (final MapMetadata mapMetadata)

  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    this.mapMetadata = mapMetadata;
    absoluteMapResourcesPathParser = new AbsoluteMapResourcesPathParser (mapMetadata.getMode ());
    relativeMapResourcesPathParser = new RelativeMapResourcesPathParser (mapMetadata.getMode ());
  }

  @Override
  public ImmutableSet <CountryAtlasMetadata> load ()
  {
    final Set <CountryAtlasMetadata> countryAtlasMetadatas = new HashSet <> ();
    final File externalCountryAtlasesDirectory = new File (
            absoluteMapResourcesPathParser.parseCountryAtlasesPath (mapMetadata));
    final File[] childPathFiles = externalCountryAtlasesDirectory.listFiles ();

    if (childPathFiles == null)
    {
      throw new PlayMapLoadingException (
              Strings.format ("Cannot find any country atlases in [{}].", externalCountryAtlasesDirectory));
    }

    int expectedAtlasNumber = 1;

    final String relativeCountryAtlasesPath = relativeMapResourcesPathParser.parseCountryAtlasesPath (mapMetadata);

    for (final File childPathFile : childPathFiles)
    {
      if (childPathFile.isDirectory () || !AssetSettings.isAtlasFileType (childPathFile.getName ())) continue;

      final String rawCountryAtlasFileName = childPathFile.getName ();

      if (!AssetSettings.isValidCountryAtlasFileName (rawCountryAtlasFileName, expectedAtlasNumber++))
      {
        log.warn ("Invalid country atlas name detected [{}], ignoring...", rawCountryAtlasFileName);
        continue;
      }

      final CountryAtlasMetadata countryAtlasMetadata = new DefaultCountryAtlasMetadata (
              new AssetDescriptor <> (relativeCountryAtlasesPath + rawCountryAtlasFileName, TextureAtlas.class),
              mapMetadata);

      if (!countryAtlasMetadatas.add (countryAtlasMetadata))
      {
        log.warn ("Duplicate {} detected [{}], ignoring...", countryAtlasMetadata, rawCountryAtlasFileName);
      }
    }

    if (countryAtlasMetadatas.isEmpty ())
    {
      throw new PlayMapLoadingException (
              Strings.format ("Cannot find any country atlases in [{}].", externalCountryAtlasesDirectory));
    }

    return ImmutableSet.copyOf (countryAtlasMetadatas);
  }
}
