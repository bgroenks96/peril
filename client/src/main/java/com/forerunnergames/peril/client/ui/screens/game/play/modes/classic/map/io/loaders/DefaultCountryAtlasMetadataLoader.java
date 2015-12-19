package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryAtlasMetadata;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.DefaultCountryAtlasMetadata;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.AbsoluteMapResourcesPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.MapResourcesPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.RelativeMapResourcesPathParser;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
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

  @Override
  public ImmutableSet <CountryAtlasMetadata> load (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    // @formatter:off
    final MapResourcesPathParser absoluteMapResourcesPathParser = new AbsoluteMapResourcesPathParser (mapMetadata.getMode ());
    final MapResourcesPathParser relativeMapResourcesPathParser = new RelativeMapResourcesPathParser (mapMetadata.getMode ());
    final File externalCountryAtlasesDirectory = new File (absoluteMapResourcesPathParser.parseCountryAtlasesPath (mapMetadata));
    final Set <CountryAtlasMetadata> countryAtlasMetadatas = new HashSet <> ();
    // @formatter:on

    try
    {
      final File[] childPathFiles = externalCountryAtlasesDirectory.listFiles ();
      int expectedAtlasIndex = 1;

      if (childPathFiles == null)
      {
        generalCountryAtlasError ("Cannot find any country atlases", externalCountryAtlasesDirectory, mapMetadata,
                                  expectedAtlasIndex);
      }

      final String relativeCountryAtlasesPath = relativeMapResourcesPathParser.parseCountryAtlasesPath (mapMetadata);

      for (final File childPathFile : childPathFiles)
      {
        if (childPathFile.isDirectory ())
        {
          log.trace ("Ignoring potential country atlas file [{}] for map [{}] because it is a directory", childPathFile,
                     mapMetadata);
          continue;
        }

        if (childPathFile.isHidden ())
        {
          log.trace ("Ignoring potential country atlas file [{}] for map [{}] because it is hidden.", childPathFile,
                     mapMetadata);
          continue;
        }

        // Each country atlas pack file, e.g., countries1.atlas,
        // must be accompanied by a country atlas image file with matching atlas index, e.g., countries1.png.
        // The following two guard clauses skip valid, matching .png country atlas image files.

        // Covers the case where countries1.png is found BEFORE countries1.atlas, for example,
        // so expectedAtlasIndex is still 1.
        if (AssetSettings.isValidCountryAtlasImageFileName (childPathFile.getName (), expectedAtlasIndex))
        {
          log.trace ("Ignoring potential country atlas file [{}] for map [{}] because although it's a valid country "
                  + "atlas *image* file, we're looking for the country atlas *pack* file for atlas index [{}].",
                     childPathFile, mapMetadata, expectedAtlasIndex);
          continue;
        }

        // Covers the case where countries1.png is found AFTER countries1.atlas, for example,
        // so expectedAtlasIndex is already incremented to 2.
        // In the first iteration, this will result in checking for countries0.png,
        // but in that corner case it will always return false, so it's not a problem.
        if (AssetSettings.isValidCountryAtlasImageFileName (childPathFile.getName (), expectedAtlasIndex - 1))
        {
          log.trace ("Ignoring child path file [{}] for map [{}] because although it's a valid country atlas "
                  + "*image* file, we're looking for the country atlas *pack* file for atlas index [{}].",
                     childPathFile, mapMetadata, expectedAtlasIndex);
          continue;
        }

        final String rawCountryAtlasFileName = childPathFile.getName ();

        if (!AssetSettings.isAtlasPackFileType (rawCountryAtlasFileName))
        {
          invalidCountryAtlasError ("Found invalid file in country atlas directory", rawCountryAtlasFileName,
                                    externalCountryAtlasesDirectory, mapMetadata, expectedAtlasIndex);
        }

        if (!AssetSettings.isValidCountryAtlasPackFileName (rawCountryAtlasFileName, expectedAtlasIndex))
        {
          invalidCountryAtlasError ("Found invalid country atlas filename", rawCountryAtlasFileName,
                                    externalCountryAtlasesDirectory, mapMetadata, expectedAtlasIndex);
        }

        final CountryAtlasMetadata countryAtlasMetadata = new DefaultCountryAtlasMetadata (
                new AssetDescriptor <> (relativeCountryAtlasesPath + rawCountryAtlasFileName, TextureAtlas.class),
                mapMetadata);

        if (!countryAtlasMetadatas.add (countryAtlasMetadata))
        {
          invalidCountryAtlasError ("Found duplicate country atlas filename", rawCountryAtlasFileName,
                                    externalCountryAtlasesDirectory, mapMetadata, expectedAtlasIndex);
        }

        log.debug ("Successfully loaded country atlas metadata [{}].", countryAtlasMetadata);

        ++expectedAtlasIndex;
      }

      if (countryAtlasMetadatas.isEmpty ())
      {
        generalCountryAtlasError ("Cannot find any country atlases", externalCountryAtlasesDirectory, mapMetadata, 1);
      }

      return ImmutableSet.copyOf (countryAtlasMetadatas);
    }
    catch (final SecurityException e)
    {
      // @formatter:off
      throw new PlayMapLoadingException (Strings.format ("Could not load country atlases for {} map: \'{}\'",
                                                         mapMetadata.getType ().name ().toLowerCase (),
                                                         Strings.toProperCase (mapMetadata.getName ())), e);
      // @formatter:on
    }
  }

  private static void invalidCountryAtlasError (final String prependedMessage,
                                                final String rawCountryAtlasFileName,
                                                final File externalCountryAtlasesDirectory,
                                                final MapMetadata mapMetadata,
                                                final int expectedAtlasIndex)
  {
    generalCountryAtlasError (Strings.format ("{}: \'{}\',", prependedMessage, rawCountryAtlasFileName),
                              externalCountryAtlasesDirectory, mapMetadata, expectedAtlasIndex);
  }

  private static void generalCountryAtlasError (final String prependedMessage,
                                                final File externalCountryAtlasesDirectory,
                                                final MapMetadata mapMetadata,
                                                final int expectedAtlasIndex)
  {
    // @formatter:off
    throw new PlayMapLoadingException (Strings
            .format ("{} for {} map: \'{}\'\n\nIn Location:\n\n{}\n\nExpected country atlas named: \'{}\'\n\n" +
                    "Naming rules for country atlases:\n\n{}",
                     prependedMessage, mapMetadata.getType ().name ().toLowerCase (),
                     Strings.toProperCase (mapMetadata.getName ()), externalCountryAtlasesDirectory.getAbsolutePath (),
                     AssetSettings.getValidCountryAtlasPackFileName (expectedAtlasIndex),
                     AssetSettings.VALID_COUNTRY_ATLAS_FILENAME_DESCRIPTION));
    // @formatter:on
  }
}
