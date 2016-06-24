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

package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public class DefaultPendingBattleActor implements PendingBattleActor
{
  private final Id playerId;
  private final Id countryId;
  private final DieRange dieRange;

  public DefaultPendingBattleActor (final Id playerId, final Id countryId, final DieRange dieRange)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNull (dieRange, "dieRange");

    this.playerId = playerId;
    this.countryId = countryId;
    this.dieRange = dieRange;
  }

  @Override
  public final Id getPlayerId ()
  {
    return playerId;
  }

  @Override
  public final Id getCountryId ()
  {
    return countryId;
  }

  @Override
  public DieRange getDieRange ()
  {
    return dieRange;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | Country: [{}] | DieRange: [{}]", getClass ().getSimpleName (), playerId,
                           countryId, dieRange);
  }
}
