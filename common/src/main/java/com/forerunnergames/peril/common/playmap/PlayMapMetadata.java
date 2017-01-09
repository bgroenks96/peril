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

package com.forerunnergames.peril.common.playmap;

import com.forerunnergames.peril.common.game.GameMode;

public interface PlayMapMetadata
{
  PlayMapMetadata NULL = new NullPlayMapMetadata ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();

  /**
   * @return The descriptive name of this play map, parsed from {@link #getDirName()}.
   */
  String getName ();

  /**
   * @return The exact name of the directory containing this play map's resources. Usually the name of the play map, but
   *         not the same as {@link #getName()}, which is parsed from the directory name.
   */
  String getDirName ();

  /**
   * @return True if the directory type matches the specified type, {@link PlayMapDirectoryType#INTERNAL} for a package
   *         inside of the jar, {@link PlayMapDirectoryType#EXTERNAL} an external filesystem directory name, otherwise
   *         false.
   *
   *         Note: This is important because an internal directory name is not allowed to have spaces between words, but
   *         only underscores, and must be all lowercase, whereas an external directory name could either have spaces or
   *         underscores, and could be any case, all depending on whether the play map type is {@link PlayMapType#STOCK}
   *         or {@link PlayMapType#CUSTOM}. This makes it possible to know how to parse the directory name correctly
   *         depending on the context.
   *
   *         If we are looking for an:
   * 
   *         <pre>
   *
   *         1) Internal play map resource & directory name is {@link PlayMapDirectoryType#INTERNAL}, leave as is.
   *            Indicates a {@link PlayMapType#STOCK} play map looking for an internal data resource.
   *            Data resources for stock maps are internal.
   * 
   *         2) External play map resource & directory name is {@link PlayMapDirectoryType#INTERNAL}, convert underscores to spaces.
   *            Indicates a {@link PlayMapType#STOCK} play map looking for an external graphics resource.
   *            Graphics resources for stock maps are external.
   * 
   *         3) External play map resource & directory name is {@link PlayMapDirectoryType#EXTERNAL}, leave as is.
   *            Indicates a {@link PlayMapType#CUSTOM} play map looking for either an external graphics resource OR
   *            an external data resource.
   *            Data & graphics resources for custom maps are both external.
   * 
   *         4) Internal play map resource & directory name is {@link PlayMapDirectoryType#EXTERNAL}, convert spaces to underscores.
   *            This case is actually not possible.
   *
   *         </pre>
   */
  boolean isDirType (final PlayMapDirectoryType dirType);

  PlayMapType getType ();

  GameMode getMode ();
}
