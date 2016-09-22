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

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class PlayMapSettings
{
  public static final float ACTUAL_WIDTH = 1600.0f;
  public static final float ACTUAL_HEIGHT = 800.0f;
  public static final boolean ENABLE_HOVER_EFFECTS = true;
  public static final boolean ENABLE_CLICK_EFFECTS = false;
  public static final Vector2 REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION = new Vector2 (-9, -9);
  public static final Vector2 COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE = new Vector2 (32, 30); // TODO Try 29,
                                                                                                        // 29 (or 28,
                                                                                                        // 30) with play
                                                                                                        // screen
                                                                                                        // revision.

  public static Vector2 referenceToActualPlayMapScaling (final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");
    Arguments.checkLowerExclusiveBound (playMapReferenceSize.x, 0, "playMapReferenceSize.x");
    Arguments.checkLowerExclusiveBound (playMapReferenceSize.y, 0, "playMapReferenceSize.y");

    return new Vector2 (ACTUAL_WIDTH / playMapReferenceSize.x, ACTUAL_HEIGHT / playMapReferenceSize.y);
  }

  public static Vector2 actualToReferencePlayMapScaling (final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    return new Vector2 (playMapReferenceSize.x / ACTUAL_WIDTH, playMapReferenceSize.y / ACTUAL_HEIGHT);
  }

  public static Vector2 countryArmyCircleSizeActualPlayMapSpace (final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    return new Vector2 (COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE)
            .scl (referenceToActualPlayMapScaling (playMapReferenceSize));
  }

  private PlayMapSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
