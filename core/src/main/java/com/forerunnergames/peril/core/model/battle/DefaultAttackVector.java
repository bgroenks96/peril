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

package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;

public class DefaultAttackVector implements AttackVector
{
  private final Id playerId;
  private final Id sourceCountryId;
  private final Id targetCountryId;

  public DefaultAttackVector (final Id playerId, final Id sourceCountryId, final Id targetCountryId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (sourceCountryId, "sourceCountryId");
    Arguments.checkIsNotNull (targetCountryId, "targetCountryId");

    this.playerId = playerId;
    this.sourceCountryId = sourceCountryId;
    this.targetCountryId = targetCountryId;
  }

  @Override
  public Id getPlayerId ()
  {
    return playerId;
  }

  @Override
  public Id getSourceCountry ()
  {
    return sourceCountryId;
  }

  @Override
  public Id getTargetCountry ()
  {
    return targetCountryId;
  }
}
