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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryAtlasMetadata;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.DefaultCountryAtlasMetadata;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.AbsolutePlayMapGraphicsPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.PlayMapGraphicsPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.RelativePlayMapGraphicsPathParser;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCountryAtlasMetadataLoader implements CountryAtlasMetadataLoader
{
  private static final Logger log = LoggerFactory.getLogger (DefaultCountryAtlasMetadataLoader.class);

  @Override
  public ImmutableSet <CountryAtlasMetadata> load (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    // @formatter:off
    final PlayMapGraphicsPathParser absolutePlayMapGraphicsPathParser = new AbsolutePlayMapGraphicsPathParser (playMapMetadata.getMode ());
    final PlayMapGraphicsPathParser relativePlayMapGraphicsPathParser = new RelativePlayMapGraphicsPathParser (playMapMetadata.getMode ());
    final File externalCountryAtlasesDirectory = new File (absolutePlayMapGraphicsPathParser.parseCountryAtlasesPath (playMapMetadata));
    final Set <CountryAtlasMetadata> countryAtlasMetadatas = new HashSet <> ();
    // @formatter:on

    try
    {
      final File[] childPathFiles = externalCountryAtlasesDirectory.listFiles ();
      int expectedAtlasIndex = 1;

      if (childPathFiles == null)
      {
        generalCountryAtlasError ("Cannot find any country atlases", externalCountryAtlasesDirectory, playMapMetadata,
                                  expectedAtlasIndex);
      }

      Arrays.sort (childPathFiles);

      final String relativeCountryAtlasesPath = relativePlayMapGraphicsPathParser
              .parseCountryAtlasesPath (playMapMetadata);

      for (final File childPathFile : childPathFiles)
      {
        log.trace ("Processing potential country atlas file [{}].", childPathFile.getAbsolutePath ());

        if (childPathFile.isDirectory ())
        {
          log.trace ("Ignoring potential country atlas file [{}] for play map [{}] because it is a directory",
                     childPathFile, playMapMetadata);
          continue;
        }

        if (childPathFile.isHidden ())
        {
          log.trace ("Ignoring potential country atlas file [{}] for play map [{}] because it is hidden.",
                     childPathFile, playMapMetadata);
          continue;
        }

        // Each country atlas pack file, e.g., countries1.atlas,
        // must be accompanied by a country atlas image file with matching atlas index, e.g., countries1.png.
        // The following two guard clauses skip valid, matching .png country atlas image files.

        // Covers the case where countries1.png is found BEFORE countries1.atlas, for example,
        // so expectedAtlasIndex is still 1.
        if (AssetSettings.isValidCountryAtlasImageFileName (childPathFile.getName (), expectedAtlasIndex))
        {
          log.trace ("Ignoring potential country atlas file [{}] for play map [{}] because although it's a valid country "
                  + "atlas *image* file, we're looking for the country atlas *pack* file for atlas index [{}].",
                     childPathFile, playMapMetadata, expectedAtlasIndex);
          continue;
        }

        // Covers the case where countries1.png is found AFTER countries1.atlas, for example,
        // so expectedAtlasIndex is already incremented to 2.
        // In the first iteration, this will result in checking for countries0.png,
        // but in that corner case it will always return false, so it's not a problem.
        if (AssetSettings.isValidCountryAtlasImageFileName (childPathFile.getName (), expectedAtlasIndex - 1))
        {
          log.trace ("Ignoring child path file [{}] for play map [{}] because although it's a valid country atlas "
                  + "*image* file, we're looking for the country atlas *pack* file for atlas index [{}].",
                     childPathFile, playMapMetadata, expectedAtlasIndex);
          continue;
        }

        final String rawCountryAtlasFileName = childPathFile.getName ();

        if (!AssetSettings.isAtlasPackFileType (rawCountryAtlasFileName))
        {
          invalidCountryAtlasError ("Found invalid file in country atlas directory", rawCountryAtlasFileName,
                                    externalCountryAtlasesDirectory, playMapMetadata, expectedAtlasIndex);
        }

        if (!AssetSettings.isValidCountryAtlasPackFileName (rawCountryAtlasFileName, expectedAtlasIndex))
        {
          invalidCountryAtlasError ("Found invalid country atlas filename", rawCountryAtlasFileName,
                                    externalCountryAtlasesDirectory, playMapMetadata, expectedAtlasIndex);
        }

        final CountryAtlasMetadata countryAtlasMetadata = new DefaultCountryAtlasMetadata (
                new AssetDescriptor<> (relativeCountryAtlasesPath + rawCountryAtlasFileName, TextureAtlas.class),
                playMapMetadata);

        if (!countryAtlasMetadatas.add (countryAtlasMetadata))
        {
          invalidCountryAtlasError ("Found duplicate country atlas filename", rawCountryAtlasFileName,
                                    externalCountryAtlasesDirectory, playMapMetadata, expectedAtlasIndex);
        }

        log.debug ("Successfully loaded country atlas metadata [{}].", countryAtlasMetadata);

        ++expectedAtlasIndex;
      }

      if (countryAtlasMetadatas.isEmpty ())
      {
        generalCountryAtlasError ("Cannot find any country atlases", externalCountryAtlasesDirectory, playMapMetadata,
                                  1);
      }

      return ImmutableSet.copyOf (countryAtlasMetadatas);
    }
    catch (final SecurityException e)
    {
      // @formatter:off
      throw new PlayMapLoadingException (Strings.format ("Could not load country atlases for {} map: \'{}\'",
                                                         playMapMetadata.getType ().name ().toLowerCase (),
                                                         playMapMetadata.getName ()), e);
      // @formatter:on
    }
  }

  private static void invalidCountryAtlasError (final String prependedMessage,
                                                final String rawCountryAtlasFileName,
                                                final File externalCountryAtlasesDirectory,
                                                final PlayMapMetadata playMapMetadata,
                                                final int expectedAtlasIndex)
  {
    generalCountryAtlasError (Strings.format ("{}: \'{}\',", prependedMessage, rawCountryAtlasFileName),
                              externalCountryAtlasesDirectory, playMapMetadata, expectedAtlasIndex);
  }

  private static void generalCountryAtlasError (final String prependedMessage,
                                                final File externalCountryAtlasesDirectory,
                                                final PlayMapMetadata playMapMetadata,
                                                final int expectedAtlasIndex)
  {
    // @formatter:off
    throw new PlayMapLoadingException (Strings
            .format ("{} for {} map: \'{}\'\n\nIn Location:\n\n{}\n\nExpected country atlas named: \'{}\'\n\n" +
                    "Naming rules for country atlases:\n\n{}",
                     prependedMessage, playMapMetadata.getType ().name ().toLowerCase (),
                     playMapMetadata.getName (), externalCountryAtlasesDirectory.getAbsolutePath (),
                     AssetSettings.getValidCountryAtlasPackFileName (expectedAtlasIndex),
                     AssetSettings.VALID_COUNTRY_ATLAS_FILENAME_DESCRIPTION));
    // @formatter:on
  }
}
