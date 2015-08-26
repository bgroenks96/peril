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

public final class DefaultCountryAtlasMetadataLoader implements CountryAtlasMetadataLoader
{
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
        if (childPathFile.isDirectory () || childPathFile.isHidden ()) continue;

        // Each country atlas pack file, e.g., countries1.atlas,
        // must be accompanied by a country atlas image file with matching atlas index, e.g., countries1.png.
        // These two guard clauses skip valid, matching .png country atlas image files.

        // Covers the case where countries1.png is found BEFORE countries1.atlas, for example,
        // so expectedAtlasIndex is still 1.
        if (AssetSettings.isValidCountryAtlasImageFileName (childPathFile.getName (), expectedAtlasIndex)) continue;

        // Covers the case where countries1.png is found AFTER countries1.atlas, for example,
        // so expectedAtlasIndex is already incremented to 2.
        // In the first iteration, this will result in checking for countries0.png,
        // but in that corner case it will always return false, so it's not a problem.
        if (AssetSettings.isValidCountryAtlasImageFileName (childPathFile.getName (), expectedAtlasIndex - 1)) continue;

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
    throw new PlayMapLoadingException (Strings
            .format ("{} for {} map: \'{}\'\n\nIn Location:\n\n{}\n\nExpected country atlas named: \'{}\'\n\nNaming rules for country atlases:\n\n{}",
                     prependedMessage, mapMetadata.getType ().name ().toLowerCase (),
                     Strings.toProperCase (mapMetadata.getName ()), externalCountryAtlasesDirectory.getAbsolutePath (),
                     AssetSettings.getValidCountryAtlasPackFileName (expectedAtlasIndex),
                     AssetSettings.VALID_COUNTRY_ATLAS_FILENAME_DESCRIPTION));
  }
}
