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

package com.forerunnergames.peril.common.game;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DieRange
{
  private final int minDieCount;
  private final int maxDieCount;

  public DieRange (final int minDieCount, final int maxDieCount)
  {
    Arguments.checkIsNotNegative (minDieCount, "minDieCount");
    Arguments.checkUpperInclusiveBound (minDieCount, maxDieCount, "minDieCount", "maxDieCount");

    this.minDieCount = minDieCount;
    this.maxDieCount = maxDieCount;
  }

  public int getMinDieCount ()
  {
    return minDieCount;
  }

  public int getMaxDieCount ()
  {
    return maxDieCount;
  }

  @Override
  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o == null || getClass () != o.getClass ()) return false;
    final DieRange dieRange = (DieRange) o;
    return minDieCount == dieRange.minDieCount && maxDieCount == dieRange.maxDieCount;
  }

  @Override
  public int hashCode ()
  {
    int result = minDieCount;
    result = 31 * result + maxDieCount;
    return result;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: MinDieCount: [{}] | MaxDieCount: [{}]", getClass ().getSimpleName (), minDieCount,
            maxDieCount);
  }

  @RequiredForNetworkSerialization
  private DieRange ()
  {
    minDieCount = -1;
    maxDieCount = -1;
  }
}
