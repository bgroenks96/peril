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

package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.net.packets.battle.FinalBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultFinalBattleActorPacket extends DefaultPendingBattleActorPacket
        implements FinalBattleActorPacket
{
  private final int dieCount;

  public DefaultFinalBattleActorPacket (final PlayerPacket player,
                                        final CountryPacket country,
                                        final DieRange dieRange,
                                        final int dieCount)
  {
    super (player, country, dieRange);

    Arguments.checkIsNotNegative (dieCount, "dieCount");
    Arguments.checkLowerInclusiveBound (dieCount, dieRange.min (), "dieCount", "dieRange.min ()");
    Arguments.checkUpperInclusiveBound (dieCount, dieRange.max (), "dieCount", "dieRange.max ()");

    this.dieCount = dieCount;
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Die count: {}", super.toString (), dieCount);
  }

  @RequiredForNetworkSerialization
  private DefaultFinalBattleActorPacket ()
  {
    dieCount = 0;
  }
}
