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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDefendCountryEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerDefendCountryResponseSuccessEvent extends AbstractPlayerDefendCountryEvent
        implements PlayerResponseSuccessEvent
{
  private final BattleActorPacket defenderData;

  public PlayerDefendCountryResponseSuccessEvent (final PlayerPacket defendingPlayer,
                                                  final CountryPacket defendingCountry,
                                                  final int defenderDieCount,
                                                  final BattleActorPacket attackerData)
  {
    super (defendingPlayer, defendingCountry, attackerData);

    defenderData = new DefaultBattleActorPacket (defendingPlayer, defendingCountry, defenderDieCount);
  }

  public BattleActorPacket getDefenderData ()
  {
    return defenderData;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: DefenderData: [{}]", getClass ().getSimpleName (), defenderData);
  }

  @RequiredForNetworkSerialization
  private PlayerDefendCountryResponseSuccessEvent ()
  {
    defenderData = null;
  }
}
