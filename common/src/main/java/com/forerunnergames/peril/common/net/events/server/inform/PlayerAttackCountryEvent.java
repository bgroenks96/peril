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

package com.forerunnergames.peril.common.net.events.server.inform;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractBattleEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleSetupEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputInformEvent;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerAttackCountryEvent extends AbstractBattleEvent
        implements BattleSetupEvent, PlayerInputInformEvent
{
  public PlayerAttackCountryEvent (final PendingBattleActorPacket attacker, final PendingBattleActorPacket defender)
  {
    super (attacker.getPlayer (), attacker, defender);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryEvent ()
  {
  }
}
