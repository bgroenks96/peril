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

package com.forerunnergames.peril.common.net.events.client.request.inform;

import com.forerunnergames.peril.common.net.events.client.interfaces.BattleRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerInformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerAttackCountryEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class PlayerAttackCountryRequestEvent
        implements BattleRequestEvent, PlayerInformRequestEvent <PlayerAttackCountryEvent>
{
  private final int dieCount;

  public PlayerAttackCountryRequestEvent (final int dieCount)
  {
    Arguments.checkIsNotNegative (dieCount, "dieCount");

    this.dieCount = dieCount;
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public Class <PlayerAttackCountryEvent> getQuestionType ()
  {
    return PlayerAttackCountryEvent.class;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: DieCount: {}", getClass ().getSimpleName (), dieCount);
  }
}
