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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class CountryImageData
{
  private final String countryName;
  private final Vector2 referenceDestination;
  private final Vector2 referenceTextUpperLeft;
  private final Vector2 referenceSize;

  public CountryImageData (final String countryName,
                           final Vector2 referenceDestination,
                           final Vector2 referenceTextUpperLeft,
                           final Vector2 referenceSize)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (referenceDestination, "referenceDestination");
    Arguments.checkIsNotNull (referenceTextUpperLeft, "referenceTextUpperLeft");
    Arguments.checkIsNotNull (referenceSize, "referenceSize");

    this.countryName = countryName;
    this.referenceDestination = referenceDestination;
    this.referenceTextUpperLeft = referenceTextUpperLeft;
    this.referenceSize = referenceSize;
  }

  public String getCountryName ()
  {
    return countryName;
  }

  public Vector2 getReferenceDestination ()
  {
    return referenceDestination;
  }

  public Vector2 getReferenceTextUpperLeft ()
  {
    return referenceTextUpperLeft;
  }

  public float getReferenceWidth ()
  {
    return referenceSize.x;
  }

  public float getReferenceHeight ()
  {
    return referenceSize.y;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Country Name: {} | Reference Destination: {} | Reference Text Upper Left: {} | "
                                   + "Reference Size: {}",
                           getClass ().getSimpleName (), countryName, referenceDestination, referenceTextUpperLeft,
                           referenceSize);
  }
}
