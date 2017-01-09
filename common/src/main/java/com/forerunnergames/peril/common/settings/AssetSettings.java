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

package com.forerunnergames.peril.common.settings;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapDirectoryType;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

/*
 * These are compile-time paths that are divided in sections such that runtime-based
 * path components can be inserted between them.
 *
 * They are used to help locate required assets.
 *
 * The path sections should match up according to their append / prepend comments.
 * For example, if SECTION_1 says "Append: ABC (at runtime)", and SECTION_2 says "Prepend: ABC (at runtime),
 * these sections can be safely concatenated at runtime with ABC in between them:
 *
 *    SECTION_1 + getABC() + SECTION2
 *
 * Warning: Do not concatenate internal & external path sections.
 *
 * Directory path types:
 *
 *   External: Existing outside of the jar, in the external filesystem.
 *
 *   Internal: Existing inside of the jar as a classpath resource.
 *
 *   Absolute: The path begins from an external or internal root directory. Always begins and ends with a /.
 *             Nothing should be prepended, but can optionally be appended.
 *
 *   Relative: The path begins and ends with a /, but must be prepended back to the root, as it is relative.
 *             Can be optionally appended.
 */
public final class AssetSettings
{
  // @formatter:off

  // Prepend: Nothing (absolute path from external root)
  // Append: Relative play maps directory (compile-time)
  public static final String ABSOLUTE_EXTERNAL_ASSETS_DIRECTORY = System.getProperty ("user.home") + "/peril/assets/";

  // Prepend: Absolute external assets directory (compile-time or runtime)
  // Append: Game mode (runtime, external) OR
  // Append: Relative mode directory (compile-time, internal)
  public static final String RELATIVE_PLAY_MAPS_DIRECTORY = "maps/";

  // Prepend: Nothing (absolute path from external root)
  // Append: Game mode (runtime)
  public static final String ABSOLUTE_EXTERNAL_PLAY_MAPS_DIRECTORY = ABSOLUTE_EXTERNAL_ASSETS_DIRECTORY + RELATIVE_PLAY_MAPS_DIRECTORY;

  // Prepend: Game mode (runtime, external) OR
  // Prepend: Relative play maps directory (compile-time, internal)
  // Append: Game mode (runtime)
  public static final String RELATIVE_MODE_DIRECTORY = "mode/";

  // Prepend: Nothing (absolute path from internal root)
  // Append: Game mode (runtime)
  public static final String ABSOLUTE_INTERNAL_PLAY_MAPS_MODE_DIRECTORY = "/" + RELATIVE_PLAY_MAPS_DIRECTORY + RELATIVE_MODE_DIRECTORY;

  // Prepend: Play map name (runtime)
  // Append: Relative country data directory (compile-time)
  public static final String RELATIVE_COUNTRIES_DIRECTORY = "countries/";

  // Prepend: Relative countries directory (compile-time)
  // Append: Countries data filename (compile-time)
  public static final String RELATIVE_COUNTRY_DATA_DIRECTORY = "data/";

  // Prepend: Play map name (runtime)
  // Append: Relative continent data directory (compile-time)
  public static final String RELATIVE_CONTINENTS_DIRECTORY = "continents/";

  // Prepend: Relative continents directory (compile-time)
  // Append: Continents data filename (compile-time)
  public static final String RELATIVE_CONTINENT_DATA_DIRECTORY = "data/";

  // Prepend: Play map name (runtime)
  // Append: Relative card data directory (compile-time)
  public static final String RELATIVE_CARDS_DIRECTORY = "cards/";

  // Prepend: Relative cards directory (compile-time)
  // Append: Card data filename (compile-time)
  public static final String RELATIVE_CARD_DATA_DIRECTORY = "data/";

  // Prepend: Relative country data directory (compile-time)
  // Append: Nothing
  public static final String COUNTRY_DATA_FILENAME = "countries.txt";

  // Prepend: Relative continent data directory (compile-time)
  // Append: Nothing
  public static final String CONTINENT_DATA_FILENAME = "continents.txt";

  // Prepend: Relative country data directory (compile-time)
  // Append: Nothing
  public static final String COUNTRY_GRAPH_FILENAME = "countryGraph.txt";

  // Prepend: Relative continent data directory (compile-time)
  // Append: Nothing
  public static final String CONTINENT_GRAPH_FILENAME = "continentGraph.txt";

  // Prepend: Relative card data directory (compile-time)
  // Append: Nothing
  public static final String CARD_DATA_FILENAME = "cards.txt";

  // @formatter:on

  public static String asExternalPathSegment (final GameMode mode)
  {
    Arguments.checkIsNotNull (mode, "mode");

    return mode.name ().toLowerCase ().replace ("_", " ") + " " + RELATIVE_MODE_DIRECTORY;
  }

  public static String asInternalPathSegment (final GameMode mode)
  {
    Arguments.checkIsNotNull (mode, "mode");

    return mode.name ().toLowerCase () + "/";
  }

  public static String asPathSegment (final PlayMapType type)
  {
    Arguments.checkIsNotNull (type, "type");

    return type.name ().toLowerCase () + "/";
  }

  public static String asExternalPlayMapDirName (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    if (metadata.isDirType (PlayMapDirectoryType.EXTERNAL)) return metadata.getDirName ();

    return internalToExternalDirName (metadata.getDirName ());
  }

  public static String asInternalPlayMapDirName (final PlayMapMetadata metadata)
  {
    Arguments.checkIsNotNull (metadata, "metadata");

    if (metadata.isDirType (PlayMapDirectoryType.INTERNAL)) return metadata.getDirName ();

    return externalToInternalDirName (metadata.getDirName ());
  }

  private static String externalToInternalDirName (final String dirName)
  {
    return dirName.replace (" ", "_");
  }

  private static String internalToExternalDirName (final String dirName)
  {
    return dirName.replace ("_", " ");
  }

  private AssetSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
