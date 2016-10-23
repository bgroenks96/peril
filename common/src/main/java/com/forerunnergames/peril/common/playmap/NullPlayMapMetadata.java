/*
 * Copyright © 2016 Forerunner Games, LLC.
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
import com.forerunnergames.tools.common.Strings;

final class NullPlayMapMetadata implements PlayMapMetadata
{
  private static final String NAME = "";
  private static final PlayMapType TYPE = PlayMapType.STOCK;
  private static final GameMode MODE = GameMode.CLASSIC;

  @Override
  public String getName ()
  {
    return NAME;
  }

  @Override
  public PlayMapType getType ()
  {
    return TYPE;
  }

  @Override
  public GameMode getMode ()
  {
    return MODE;
  }

  @Override
  public int hashCode ()
  {
    int result = NAME.hashCode ();
    result = 31 * result + TYPE.hashCode ();
    result = 31 * result + MODE.hashCode ();
    return result;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    final PlayMapMetadata playMapMetadata = (PlayMapMetadata) obj;

    return NAME.equals (playMapMetadata.getName ()) && TYPE == playMapMetadata.getType () && MODE == playMapMetadata.getMode ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | Type: {} | Mode: {}", getClass ().getSimpleName (), NAME, TYPE, MODE);
  }
}
