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

package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultBattleActor implements BattleActor
{
  private final Id playerId;
  private final Id countryId;
  private final int dieCount;

  public DefaultBattleActor (final Id playerId, final Id countryId, final int dieCount)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNegative (dieCount, "dieCount");

    this.playerId = playerId;
    this.countryId = countryId;
    this.dieCount = dieCount;
  }

  @Override
  public Id getPlayerId ()
  {
    return playerId;
  }

  @Override
  public Id getCountryId ()
  {
    return countryId;
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | Country: [{}] | Die count: {}", getClass ().getSimpleName (), playerId,
                           countryId, dieCount);
  }
}
