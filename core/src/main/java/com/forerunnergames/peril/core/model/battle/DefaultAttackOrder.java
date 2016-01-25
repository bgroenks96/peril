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
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

final class DefaultAttackOrder implements AttackOrder
{
  private final Id attackOrderId = IdGenerator.generateUniqueId ();
  private final Id playerId;
  private final Id sourceCountry;
  private final Id targetCountry;
  private final int dieCount;

  DefaultAttackOrder (final Id playerId, final Id sourceCountry, final Id targetCountry, final int dieCount)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkIsNotNegative (dieCount, "dieCount");

    this.playerId = playerId;
    this.sourceCountry = sourceCountry;
    this.targetCountry = targetCountry;
    this.dieCount = dieCount;
  }

  @Override
  public Id getId ()
  {
    return attackOrderId;
  }

  @Override
  public Id getPlayerId ()
  {
    return playerId;
  }

  @Override
  public Id getSourceCountry ()
  {
    return sourceCountry;
  }

  @Override
  public Id getTargetCountry ()
  {
    return targetCountry;
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof AttackOrder)) return false;
    return ((AttackOrder) obj).getId ().equals (attackOrderId);
  }

  @Override
  public int hashCode ()
  {
    return attackOrderId.hashCode ();
  }
}
