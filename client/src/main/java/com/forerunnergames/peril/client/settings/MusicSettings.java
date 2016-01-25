/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;

public final class MusicSettings
{
  public static final float MIN_VOLUME = 0.0f;
  public static final float MAX_VOLUME = 1.0f;
  public static boolean IS_ENABLED = true;
  public static float INITIAL_VOLUME = MAX_VOLUME;

  private MusicSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
