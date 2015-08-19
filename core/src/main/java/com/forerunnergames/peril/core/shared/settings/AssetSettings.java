package com.forerunnergames.peril.core.shared.settings;

import com.forerunnergames.tools.common.Classes;

/*
 * These are compile-time paths that are divided in sections such that runtime-based
 * path components can be inserted between them.
 *
 * They are used to help locate required assets.
 *
 * The path sections should match up according to their append / prepend comments.
 * For example, if SECTION_1 says "Append: ABC (at runtime)", and SECTION_2 says "Prepend: ABC (at rutime),
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
  // Append: Relative maps directory (compile-time)
  public static final String ABSOLUTE_EXTERNAL_ASSETS_DIRECTORY = System.getProperty ("user.home") + "/peril/assets/";

  // Prepend: Absolute external assets directory (compile-time or runtime)
  // Append: Game mode (runtime, external) or relative mode directory (compile-time, internal)
  public static final String RELATIVE_MAPS_DIRECTORY = "maps/";

  // Prepend: Nothing (absolute path from external root)
  // Append: Game mode (runtime)
  public static final String ABSOLUTE_EXTERNAL_MAPS_DIRECTORY = ABSOLUTE_EXTERNAL_ASSETS_DIRECTORY + RELATIVE_MAPS_DIRECTORY;

  // Prepend: Relative maps directory (compile-time)
  // Append: Game mode (runtime)
  public static final String RELATIVE_MODE_DIRECTORY = "mode/";

  // Prepend: Nothing (absolute path from internal root)
  // Append: Game mode (runtime)
  public static final String ABSOLUTE_INTERNAL_MAPS_MODE_DIRECTORY = "/" + RELATIVE_MAPS_DIRECTORY + RELATIVE_MODE_DIRECTORY;

  // Prepend: Map name (runtime)
  // Append: Relative country data directory (compile-time)
  public static final String RELATIVE_COUNTRIES_DIRECTORY = "countries/";

  // Prepend: Relative countries directory (compile-time)
  // Append: Countries data filename (compile-time)
  public static final String RELATIVE_COUNTRY_DATA_DIRECTORY = "data/";

  // Prepend: Map name (runtime)
  // Append: Relative continent data directory (compile-time)
  public static final String RELATIVE_CONTINENTS_DIRECTORY = "continents/";

  // Prepend: Relative continents directory (compile-time)
  // Append: Continents data filename (compile-time)
  public static final String RELATIVE_CONTINENT_DATA_DIRECTORY = "data/";

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

  // @formatter:on

  private AssetSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
